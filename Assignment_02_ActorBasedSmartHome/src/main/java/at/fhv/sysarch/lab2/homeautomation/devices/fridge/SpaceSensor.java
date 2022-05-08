package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class SpaceSensor extends AbstractBehavior<SpaceSensor.SpaceSensorCommand> {
    public interface SpaceSensorCommand{}

    public static final class RequestFreeSpace implements SpaceSensorCommand {
        ActorRef<OrderProcessor.OrderProcessorCommand> replyTo;
        public RequestFreeSpace(ActorRef<OrderProcessor.OrderProcessorCommand> replyTo) {
            this.replyTo = replyTo;
        }
    }

    private int maxSpace = 30;
    private int currentSpace = 0;

    public static Behavior<SpaceSensor.SpaceSensorCommand> create() {
        return Behaviors.setup(SpaceSensor::new);
    }

    public SpaceSensor(ActorContext<SpaceSensorCommand> context) {
        super(context);
    }

    @Override
    public Receive<SpaceSensorCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(RequestFreeSpace.class, this::onRequestFreeSpace)
                .build();
    }

    private Behavior<SpaceSensorCommand> onRequestFreeSpace(RequestFreeSpace rfs) {
        int freeSpace = maxSpace - currentSpace;

        getContext().getLog().info("Free space {}", freeSpace);

        rfs.replyTo.tell(new OrderProcessor.ReceiveFreeSpace(freeSpace));

        return this;
    }
}
