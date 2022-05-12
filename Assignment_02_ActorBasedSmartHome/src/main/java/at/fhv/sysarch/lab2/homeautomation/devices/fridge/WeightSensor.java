package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.Blind;

public class WeightSensor extends AbstractBehavior<WeightSensor.WeightSensorCommand> {
    public interface WeightSensorCommand{}

    public static final class RequestFreeWeight implements WeightSensorCommand {
        ActorRef<OrderProcessor.OrderProcessorCommand> replyTo;

        public RequestFreeWeight(ActorRef<OrderProcessor.OrderProcessorCommand> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static final class OnSuccessfulOrder implements WeightSensor.WeightSensorCommand {
        Order order;

        public OnSuccessfulOrder(Order order) {
            this.order = order;
        }
    }

    public static final class OnConsumeProduct implements WeightSensor.WeightSensorCommand {
        Product product;
        int quantity;

        public OnConsumeProduct(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    private final String groupId;
    private final String deviceId;
    private double maxWeight = 150;
    private double currentWeight = 0;

    public static Behavior<WeightSensor.WeightSensorCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new WeightSensor(context, groupId, deviceId));
    }
    private WeightSensor(ActorContext<WeightSensorCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
    }

    @Override
    public Receive<WeightSensorCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(RequestFreeWeight.class, this::onRequestFreeWeight)
                .onMessage(OnSuccessfulOrder.class, this::onSuccessfulOrder)
                .onMessage(OnConsumeProduct.class, this::onConsumeProduct)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<WeightSensorCommand> onRequestFreeWeight(RequestFreeWeight rfw) {
        double freeWeight = maxWeight - currentWeight;

        getContext().getLog().info("Free weight {}", freeWeight);

        rfw.replyTo.tell(new OrderProcessor.ReceiveFreeWeight(freeWeight));

        return this;
    }

    private Behavior<WeightSensor.WeightSensorCommand> onSuccessfulOrder(OnSuccessfulOrder oso) {
        getContext().getLog().info("Received new successful order, which uses {} kg", oso.order.getProduct().getWeight());

        currentWeight += oso.order.getProduct().getWeight() * oso.order.getQuantity();

        getContext().getLog().info("New current weight {}", currentWeight);

        return this;
    }

    private Behavior<WeightSensor.WeightSensorCommand> onConsumeProduct(OnConsumeProduct ocp) {
        getContext().getLog().info("Someone is consuming {} {}", ocp.quantity, ocp.product.getName());

        currentWeight -= ocp.product.getWeight() * ocp.quantity;

        getContext().getLog().info("New current weight {}", currentWeight);

        return this;
    }

    private WeightSensor onPostStop() {
        getContext().getLog().info("WeightSensor actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}
