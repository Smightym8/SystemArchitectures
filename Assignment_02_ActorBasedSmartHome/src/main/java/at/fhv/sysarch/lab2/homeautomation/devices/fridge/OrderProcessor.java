package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.Blind;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.TemperatureSensor;

import java.util.Optional;

public class OrderProcessor extends AbstractBehavior<OrderProcessor.OrderProcessorCommand> {
    public interface OrderProcessorCommand{}

    public static final class ReceiveFreeSpace implements OrderProcessorCommand {
        final int freeSpace;

        public ReceiveFreeSpace(int freeSpace) {
            this.freeSpace = freeSpace;
        }
    }

    public static final class ReceiveFreeWeight implements OrderProcessorCommand {
        final double freeWeight;

        public ReceiveFreeWeight(double freeWeight) {
            this.freeWeight = freeWeight;
        }
    }

    private final String groupId;
    private final String deviceId;
    private Order order;
    private ActorRef<Fridge.FridgeCommand> fridge;
    private ActorRef<SpaceSensor.SpaceSensorCommand> spaceSensor;
    private ActorRef<WeightSensor.WeightSensorCommand> weightSensor;

    private Optional<Integer> freeSpace;
    private Optional<Double> freeWeight;

    public static Behavior<OrderProcessor.OrderProcessorCommand> create(
            String groupId,
            String deviceId,
            Order order,
            ActorRef<Fridge.FridgeCommand> fridge,
            ActorRef<SpaceSensor.SpaceSensorCommand> spaceSensor,
            ActorRef<WeightSensor.WeightSensorCommand> weightSensor
    ) {
        return Behaviors.setup(context -> new OrderProcessor(context, groupId, deviceId, order, fridge, spaceSensor, weightSensor));
    }

    private OrderProcessor(
            ActorContext<OrderProcessorCommand> context,
            String groupId,
            String deviceId,
            Order order,
            ActorRef<Fridge.FridgeCommand> fridge,
            ActorRef<SpaceSensor.SpaceSensorCommand> spaceSensor,
            ActorRef<WeightSensor.WeightSensorCommand> weightSensor
    ) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.order = order;
        this.fridge = fridge;
        this.spaceSensor = spaceSensor;
        this.weightSensor = weightSensor;
        this.freeSpace = Optional.empty();
        this.freeWeight = Optional.empty();

        spaceSensor.tell(new SpaceSensor.RequestFreeSpace(getContext().getSelf()));
        weightSensor.tell(new WeightSensor.RequestFreeWeight(getContext().getSelf()));
    }

    @Override
    public Receive<OrderProcessorCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReceiveFreeSpace.class, this::onReceiveFreeSpace)
                .onMessage(ReceiveFreeWeight.class, this::onReceiveFreeWeight)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<OrderProcessorCommand> onReceiveFreeSpace(ReceiveFreeSpace rfs) {
        freeSpace = Optional.of(rfs.freeSpace);
        processOrder();
        return this;
    }
    private Behavior<OrderProcessorCommand> onReceiveFreeWeight(ReceiveFreeWeight rfw) {
        freeWeight = Optional.of(rfw.freeWeight);
        processOrder();
        return this;
    }

    private Behavior<OrderProcessorCommand> processOrder() {
        if (freeSpace.isPresent() && freeWeight.isPresent()) {
            if (freeSpace.get() - (order.getProduct().getSpace() * order.getQuantity()) < 0) {
                fridge.tell(new Fridge.ReceiveDeniedOrder("no space left for this order"));
            } else if (freeWeight.get() - (order.getProduct().getWeight() * order.getQuantity()) < 0) {
                fridge.tell(new Fridge.ReceiveDeniedOrder("order has too much weight"));
            } else {
                fridge.tell(new Fridge.ReceiveApprovedOrder(order));
                spaceSensor.tell(new SpaceSensor.OnSuccessfulOrder(order));
                weightSensor.tell(new WeightSensor.OnSuccessfulOrder(order));
            }
        }

        // In every case the OrderProcessor gets stopped in the end.
        return Behaviors.stopped();
    }

    private OrderProcessor onPostStop() {
        getContext().getLog().info("OrderProcessor actor {}-{} stopped", groupId, deviceId);
        return this;
    }

}
