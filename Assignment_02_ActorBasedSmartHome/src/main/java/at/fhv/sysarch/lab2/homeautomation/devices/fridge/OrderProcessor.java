package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

public class OrderProcessor extends AbstractBehavior<OrderProcessor.OrderProcessorCommand> {
    public interface OrderProcessorCommand{}

    public OrderProcessor(ActorContext<OrderProcessorCommand> context) {
        super(context);
    }

    @Override
    public Receive<OrderProcessorCommand> createReceive() {
        return null;
    }
}
