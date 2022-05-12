package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.Blind;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public static final class QueryOrderHistory implements FridgeCommand {
        public QueryOrderHistory() {}
    }

    // TODO: Change to stored or current products
    public static final class QueryCurrentProducts implements FridgeCommand {
        public QueryCurrentProducts() {}
    }

    public static final class ConsumeProduct implements FridgeCommand {
        Product product;
        int quantity;

        public ConsumeProduct(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    private final String groupId;
    private final String deviceId;
    private ActorRef<SpaceSensor.SpaceSensorCommand> spaceSensor;
    private ActorRef<WeightSensor.WeightSensorCommand> weightSensor;
    private Map<Product, Integer> products;
    private Map<LocalDateTime, Order> orderHistory;

    public static Behavior<Fridge.FridgeCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new Fridge(context, groupId, deviceId));
    }

    private Fridge(ActorContext<FridgeCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.spaceSensor = getContext().spawn(SpaceSensor.create("3", "2"), "SpaceSensor");
        this.weightSensor = getContext().spawn(WeightSensor.create("3", "3"), "WeightSensor");
        this.products = new HashMap<>();
        this.orderHistory = new HashMap<>();
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(OrderProduct.class, this::onOrderProduct)
                .onMessage(ReceiveApprovedOrder.class, this::onReceiveApprovedOrder)
                .onMessage(ReceiveDeniedOrder.class, this::onReceiveDeniedOrder)
                .onMessage(QueryOrderHistory.class, this::onQueryOrderHistory)
                .onMessage(QueryCurrentProducts.class, this::onQueryAvailableProducts)
                .onMessage(ConsumeProduct.class, this::onConsumeProduct)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<FridgeCommand> onOrderProduct(OrderProduct op) {
        getContext().spawn(OrderProcessor.create("3", "4", op.order,
                getContext().getSelf(), spaceSensor, weightSensor), "OrderProcessor-" + UUID.randomUUID());
        return this;
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

    private Behavior<FridgeCommand> onQueryOrderHistory(QueryOrderHistory qoh) {
        if(orderHistory.size() > 0) {
            getContext().getLog().info("Order History");

            orderHistory.forEach((timestamp ,order) -> {
                getContext().getLog().info("{}", timestamp);
                getContext().getLog().info("-----------------------------");
                getContext().getLog().info("{} of {}", order.getQuantity(), order.getProduct().getName());
                getContext().getLog().info("-----------------------------");
            });
        } else {
            getContext().getLog().info("The order history is empty");
        }

        return this;
    }

    private Behavior<FridgeCommand> onQueryAvailableProducts(QueryCurrentProducts qap) {
        if(products.size() > 0) {
            getContext().getLog().info("Available Products");

            products.forEach((product, quantity) -> getContext().getLog().info("{} of {}", product.getName(), quantity));
        } else {
            getContext().getLog().info("No products available");
        }

        return this;
    }

    private Behavior<FridgeCommand> onConsumeProduct(ConsumeProduct cp) {
        getContext().getLog().info("Someone is consuming {} {}", cp.quantity, cp.product.getName());

        if (products.containsKey(cp.product) && products.get(cp.product) >= cp.quantity) {
            products.put(cp.product, products.get(cp.product) - 1);

            if (products.get(cp.product) == 0) {
                Order order = new Order(cp.product, 1);
                getContext().getSelf().tell(new OrderProduct(order));
            }

            spaceSensor.tell(new SpaceSensor.OnConsumeProduct(cp.product, cp.quantity));
            weightSensor.tell(new WeightSensor.OnConsumeProduct(cp.product, cp.quantity));
        } else {
            getContext().getLog().info("There are not enough {} in the fridge", cp.product.getName());
        }

        return this;
    }

    private Fridge onPostStop() {
        getContext().getLog().info("Fridge actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}
