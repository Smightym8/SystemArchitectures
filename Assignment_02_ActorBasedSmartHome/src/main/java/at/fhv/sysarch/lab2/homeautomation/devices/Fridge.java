package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

public class Fridge extends AbstractBehavior<Fridge.FridgeCommand> {
    public interface FridgeCommand{}

    public Fridge(ActorContext<FridgeCommand> context) {
        super(context);
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return null;
    }
}
