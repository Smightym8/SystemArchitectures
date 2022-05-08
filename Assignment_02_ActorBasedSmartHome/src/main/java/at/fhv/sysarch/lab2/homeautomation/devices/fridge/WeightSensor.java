package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class WeightSensor extends AbstractBehavior<WeightSensor.WeightSensorCommand> {
    public interface WeightSensorCommand{}

    public static final class RequestFreeWeight implements WeightSensorCommand {
        ActorRef<OrderProcessor.OrderProcessorCommand> replyTo;

        public RequestFreeWeight(ActorRef<OrderProcessor.OrderProcessorCommand> replyTo) {
            this.replyTo = replyTo;
        }
    }

    private double maxWeight = 150;
    private double currentWeight = 0;

    public static Behavior<WeightSensor.WeightSensorCommand> create() {
        return Behaviors.setup(WeightSensor::new);
    }
    public WeightSensor(ActorContext<WeightSensorCommand> context) {
        super(context);
    }

    @Override
    public Receive<WeightSensorCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(RequestFreeWeight.class, this::onRequestFreeWeight)
                .build();
    }

    private Behavior<WeightSensorCommand> onRequestFreeWeight(RequestFreeWeight rfw) {
        double freeWeight = maxWeight - currentWeight;

        getContext().getLog().info("Free weight {}", freeWeight);

        rfw.replyTo.tell(new OrderProcessor.ReceiveFreeWeight(freeWeight));

        return this;
    }
}
