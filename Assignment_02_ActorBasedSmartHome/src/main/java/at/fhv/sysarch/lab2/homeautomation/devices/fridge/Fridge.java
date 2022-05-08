package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;
import java.util.List;

public class Fridge extends AbstractBehavior<Fridge.FridgeCommand> {
    public interface FridgeCommand{}

    public static final class OrderProduct implements FridgeCommand {
        Order order;

        public OrderProduct(Order order) {
            this.order = order;
        }
    }

    private ActorRef<SpaceSensor.SpaceSensorCommand> spaceSensor;
    private ActorRef<WeightSensor.WeightSensorCommand> weightSensor;

    private List<Product> products;
    private List<Order> orderHistory;

    public static Behavior<Fridge.FridgeCommand> create() {
        return Behaviors.setup(Fridge::new);
    }

    public Fridge(ActorContext<FridgeCommand> context) {
        super(context);
        this.spaceSensor = getContext().spawn(SpaceSensor.create(), "SpaceSensor");
        this.weightSensor = getContext().spawn(WeightSensor.create(), "WeightSensor");
        this.products = new ArrayList<>();
        this.orderHistory = new ArrayList<>();
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return null;
    }
}
