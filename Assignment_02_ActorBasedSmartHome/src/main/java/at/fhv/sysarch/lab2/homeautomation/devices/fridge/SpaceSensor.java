package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class SpaceSensor extends AbstractBehavior<SpaceSensor.SpaceSensorCommand> {
    public interface SpaceSensorCommand{}

    private int maxSpace = 30;
    private int currenSpace = 0;

    public static Behavior<SpaceSensor.SpaceSensorCommand> create() {
        return Behaviors.setup(SpaceSensor::new);
    }

    public SpaceSensor(ActorContext<SpaceSensorCommand> context) {
        super(context);
    }

    @Override
    public Receive<SpaceSensorCommand> createReceive() {
        return null;
    }
}
