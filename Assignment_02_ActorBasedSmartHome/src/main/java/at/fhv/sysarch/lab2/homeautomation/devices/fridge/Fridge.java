package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fridge extends AbstractBehavior<Fridge.FridgeCommand> {
    public interface FridgeCommand{}

    public static final class ReceiveApprovedOrder implements FridgeCommand {
        Order order;

        public ReceiveApprovedOrder(Order order) {
            this.order = order;
        }
    }

    public static final class ReceiveDeniedOrder implements FridgeCommand {
        String message;

        public ReceiveDeniedOrder(String message) {
            this.message = message;
        }
    }

    public static final class OrderProduct implements FridgeCommand {
        Order order;

        public OrderProduct(Order order) {
            this.order = order;
        }
    }

    private ActorRef<SpaceSensor.SpaceSensorCommand> spaceSensor;
    private ActorRef<WeightSensor.WeightSensorCommand> weightSensor;
    private Map<Product, Integer> products;
    private Map<LocalDateTime, Order> orderHistory;

    public static Behavior<Fridge.FridgeCommand> create() {
        return Behaviors.setup(Fridge::new);
    }

    public Fridge(ActorContext<FridgeCommand> context) {
        super(context);
        this.spaceSensor = getContext().spawn(SpaceSensor.create(), "SpaceSensor");
        this.weightSensor = getContext().spawn(WeightSensor.create(), "WeightSensor");
        this.products = new HashMap<>();
        this.orderHistory = new HashMap<>();
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReceiveApprovedOrder.class, this::onReceiveApprovedOrder)
                .onMessage(ReceiveDeniedOrder.class, this::onReceiveDeniedOrder)
                .build();
    }

    private Behavior<FridgeCommand> onReceiveApprovedOrder(ReceiveApprovedOrder rao) {
        orderHistory.put(LocalDateTime.now(), rao.order);

        Product product = rao.order.getProduct();
        int quantity = rao.order.getQuantity();

        if (products.containsKey(rao.order.getProduct())) {
            products.put(product, products.get(product) + quantity);
        } else {
            products.put(product, quantity);
        }

        return this;
    }

    private Behavior<FridgeCommand> onReceiveDeniedOrder(ReceiveDeniedOrder rdo) {
        getContext().getLog().info("Order denied because {}", rdo.message);

        return this;
    }
}
