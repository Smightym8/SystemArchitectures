package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.Blind;

public class SpaceSensor extends AbstractBehavior<SpaceSensor.SpaceSensorCommand> {
    public interface SpaceSensorCommand{}

    public static final class RequestFreeSpace implements SpaceSensorCommand {
        ActorRef<OrderProcessor.OrderProcessorCommand> replyTo;
        public RequestFreeSpace(ActorRef<OrderProcessor.OrderProcessorCommand> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static final class OnSuccessfulOrder implements SpaceSensorCommand {
        Order order;

        public OnSuccessfulOrder(Order order) {
            this.order = order;
        }
    }

    public static final class OnConsumeProduct implements SpaceSensorCommand {
        Product product;
        int quantity;

        public OnConsumeProduct(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    private final String groupId;
    private final String deviceId;
    private int maxSpace = 30;
    private int currentSpace = 0;

    public static Behavior<SpaceSensor.SpaceSensorCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new SpaceSensor(context, groupId, deviceId));
    }

    private SpaceSensor(ActorContext<SpaceSensorCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
    }

    @Override
    public Receive<SpaceSensorCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(RequestFreeSpace.class, this::onRequestFreeSpace)
                .onMessage(OnSuccessfulOrder.class, this::onSuccessfulOrder)
                .onMessage(OnConsumeProduct.class, this::onConsumeProduct)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<SpaceSensorCommand> onRequestFreeSpace(RequestFreeSpace rfs) {
        int freeSpace = maxSpace - currentSpace;

        getContext().getLog().info("Free space {}", freeSpace);

        rfs.replyTo.tell(new OrderProcessor.ReceiveFreeSpace(freeSpace));

        return this;
    }

    private Behavior<SpaceSensorCommand> onSuccessfulOrder(OnSuccessfulOrder oso) {
        getContext().getLog().info("Received new successful order, which uses {} space", oso.order.getProduct().getSpace());

        currentSpace += oso.order.getQuantity() * oso.order.getProduct().getSpace();

        getContext().getLog().info("New current space {}", currentSpace);

        return this;
    }

    private Behavior<SpaceSensorCommand> onConsumeProduct(OnConsumeProduct ocp) {
        getContext().getLog().info("Someone is consuming {} {}", ocp.quantity, ocp.product.getName());

        currentSpace -= ocp.product.getSpace() * ocp.quantity;

        getContext().getLog().info("New current space {}", currentSpace);

        return this;
    }

    private SpaceSensor onPostStop() {
        getContext().getLog().info("SpaceSensor actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}
