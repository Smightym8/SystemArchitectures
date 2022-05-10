package at.fhv.sysarch.lab2.homeautomation.ui;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.AirCondition;
import at.fhv.sysarch.lab2.homeautomation.devices.BlindCondition;
import at.fhv.sysarch.lab2.homeautomation.devices.MediaStation;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Fridge;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Order;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Product;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.WeatherSensor;
import at.fhv.sysarch.lab2.homeautomation.environment.EnvironmentActor;
import at.fhv.sysarch.lab2.homeautomation.environment.Weather;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UI extends AbstractBehavior<Void> {
    private ActorRef<EnvironmentActor.EnvironmentCommand> environment;
    private ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
    private ActorRef<WeatherSensor.WeatherCommand> weatherSensor;
    private ActorRef<AirCondition.AirConditionCommand> airCondition;
    private ActorRef<BlindCondition.BlindCommand> blindCondition;
    private ActorRef<MediaStation.MediaStationCommand> mediaStationCondition;
    private ActorRef<Fridge.FridgeCommand> fridge;

    private List<Product> products = List.of(
            new Product("Baklava", 5, 2),
            new Product("Snus", 2, 1),
            new Product("Ayran", 5, 1),
            new Product("Simit", 3, 10),
            new Product("Mercimek Çorbası", 100, 100),
            new Product("Kisir", 100, 100),
            new Product("Menemen", 50, 50),
            new Product("Sigara böreği", 23, 23)
    );

    public static Behavior<Void> create(
            ActorRef<EnvironmentActor.EnvironmentCommand> environment,
            ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
            ActorRef<AirCondition.AirConditionCommand> airCondition,
            ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
            ActorRef<BlindCondition.BlindCommand> blindCondition,
            ActorRef<MediaStation.MediaStationCommand> mediaStationCondition,
            ActorRef<Fridge.FridgeCommand> fridge) {
        return Behaviors.setup(context -> new UI(context, environment, tempSensor,
                airCondition, weatherSensor, blindCondition, mediaStationCondition, fridge));
    }

    private  UI(
            ActorContext<Void> context,
            ActorRef<EnvironmentActor.EnvironmentCommand> environment,
            ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
            ActorRef<AirCondition.AirConditionCommand> airCondition,
            ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
            ActorRef<BlindCondition.BlindCommand> blindCondition,
            ActorRef<MediaStation.MediaStationCommand> mediaStationCondition,
            ActorRef<Fridge.FridgeCommand> fridge) {
        super(context);
        // TODO: implement actor and behavior as needed
        // TODO: move UI initialization to appropriate place
        this.environment = environment;
        this.airCondition = airCondition;
        this.tempSensor = tempSensor;
        this.blindCondition = blindCondition;
        this.mediaStationCondition = mediaStationCondition;
        this.weatherSensor = weatherSensor;
        this.fridge = fridge;
        new Thread(this::runCommandLine).start();

        getContext().getLog().info("UI started");
    }

    @Override
    public Receive<Void> createReceive() {
        return newReceiveBuilder().onSignal(PostStop.class, signal -> onPostStop()).build();
    }

    private UI onPostStop() {
        getContext().getLog().info("UI stopped");
        return this;
    }

    // TODO: Change output when turning AirCondition on off
    public void runCommandLine() {
        Scanner scanner = new Scanner(System.in);
        String input;
        boolean isExit = false;

        System.out.println("Please enter your name:");
        input = scanner.nextLine();
        System.out.println("Hello " + input + "!");
        printUsage();

        while(!isExit) {
            System.out.println("Please enter a command:");
            input = scanner.nextLine();

            switch (input) {
                case "exit":
                    isExit = true;
                    break;
                case "settemp":
                    setTemperature(scanner);
                    break;
                case "reqtemp":
                    this.tempSensor.tell(new TemperatureSensor.RequestTemperature());
                    break;
                case "setweather":
                   setWeather(scanner);
                    break;
                case "reqweather":
                    this.weatherSensor.tell(new WeatherSensor.RequestWeather());
                    break;
                case "acpower":
                    handleAcPower(scanner);
                    break;
                case "mediapower":
                    handleMediaStationPower(scanner);
                    break;
                case "playmovie":
                    handlePlayMovie(scanner);
                    break;
                case "orderproduct":
                    handleOrder(scanner);
                    break;
                case "printorders":
                    this.fridge.tell(new Fridge.QueryOrderHistory());
                    break;
                case "printproducts":
                    this.fridge.tell(new Fridge.QueryAvailableProducts());
                    break;
                case "consume":
                    handleConsume(scanner);
                    break;
                default:
                    System.out.println("Unknown command");
                    printUsage();
                    break;
            }
        }

        System.out.println("Goodbye!");
        getContext().getLog().info("UI done");
    }

    private void setTemperature(Scanner scanner) {
        System.out.println("Please enter a value for the temperature:");
        String tempInput = scanner.nextLine();
        try {
            double temp = Double.parseDouble(tempInput);
            this.tempSensor.tell(new TemperatureSensor.ReadTemperature(Optional.of(temp)));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
        }
    }

    private void setWeather(Scanner scanner) {
        System.out.println("Available weather values");
        System.out.println("--------------------------------------------------------------------------");
        // Print all weather values

        int i = 1;
        for(Weather w : Weather.values()) {
            System.out.println("(" + i + ") " + w.getFriendlyName());
            i++;
        }

        System.out.println("Please enter one of the shown numbers to choose a weather:");
        String input = scanner.nextLine();

        Optional<Weather> chosenWeather;
        switch (input) {
            case "1":
                chosenWeather = Optional.of(Weather.SUNNY);
                break;
            case "2":
                chosenWeather = Optional.of(Weather.RAINY);
                break;
            case "3":
                chosenWeather = Optional.of(Weather.FOGGY);
                break;
            default:
                chosenWeather = Optional.empty();
                break;
        }

        if(chosenWeather.isPresent()) {
            this.weatherSensor.tell(new WeatherSensor.ReadWeatherCondition(chosenWeather));
        } else {
            System.out.println("Invalid input");
        }
    }

    // TODO: Move them to one method and add acpower/mediapower/playmovie as parameter
    private void handleAcPower(Scanner scanner) {
        System.out.println("(1) Turn AirCondition on");
        System.out.println("(2) Turn AirCondition off");
        System.out.println("Enter a value:");

        String inputStr = scanner.nextLine();
        Optional<Boolean> state;

        switch (inputStr) {
            case "1":
                state = Optional.of(true);
                break;
            case "2":
                state = Optional.of(false);
                break;
            default:
                state = Optional.empty();
        }

        if(state.isPresent()) {
            this.airCondition.tell(new AirCondition.PowerAirCondition(state));
        } else {
            System.out.println("Invalid input");
        }
    }

    private void handleMediaStationPower(Scanner scanner) {
        System.out.println("(1) Turn MediaStation on");
        System.out.println("(2) Turn MediaStation off");
        System.out.println("Enter a value:");

        String inputStr = scanner.nextLine();
        Optional<Boolean> state;

        switch (inputStr) {
            case "1":
                state = Optional.of(true);
                break;
            case "2":
                state = Optional.of(false);
                break;
            default:
                state = Optional.empty();
        }

        if(state.isPresent()) {
            this.mediaStationCondition.tell(new MediaStation.PowerMediaStation(state));
        } else {
            System.out.println("Invalid input");
        }
    }

    private void handlePlayMovie(Scanner scanner) {
        System.out.println("(1) Start playing movie");
        System.out.println("(2) Stop playing movie");
        System.out.println("Enter a value:");

        String inputStr = scanner.nextLine();
        Optional<Boolean> state;

        switch (inputStr) {
            case "1":
                state = Optional.of(true);
                break;
            case "2":
                state = Optional.of(false);
                break;
            default:
                state = Optional.empty();
        }

        if(state.isPresent()) {
            this.mediaStationCondition.tell(new MediaStation.PlayMovie(state));
        } else {
            System.out.println("Invalid input");
        }
    }

    private void handleOrder(Scanner scanner) {
        // TODO: implement
        System.out.println("Available products");
        System.out.println("--------------------");

        int i = 1;
        for(Product p : products) {
            System.out.println("(" + i + ") " + p.getName() + " - " + p.getWeight() + "kg" + " - " + p.getSpace() + " space");
            i++;
        }

        System.out.println("Please enter the number of a product:");
        String input = scanner.nextLine();
        try {
            int productNumber = Integer.parseInt(input) - 1;
            Product product = products.get(productNumber);

            System.out.println("Enter a amount to order:");
            input = scanner.nextLine();
            int amount = Integer.parseInt(input);

            if (amount < 0) {
                throw new IllegalArgumentException();
            }

            Order order = new Order(product, amount);
            fridge.tell(new Fridge.OrderProduct(order));
            System.out.println("Ordered " + amount + " of " + product.getName());
        } catch (IndexOutOfBoundsException | IllegalArgumentException exception) {
            System.out.println("Invalid input");
        }
    }

    private void handleConsume(Scanner scanner) {
        // TODO: implement
        System.out.println("Available products");
        System.out.println("--------------------");

        int i = 1;
        for(Product p : products) {
            System.out.println("(" + i + ") " + p.getName() + " - " + p.getWeight() + "kg" + " - " + p.getSpace() + " space");
            i++;
        }

        System.out.println("Please enter the number of a product:");
        String input = scanner.nextLine();

        try {
            int productNumber = Integer.parseInt(input) - 1;
            Product product = products.get(productNumber);

            System.out.println("Enter a amount to order:");
            input = scanner.nextLine();
            int quantity = Integer.parseInt(input);

            if (quantity < 0) {
                throw new IllegalArgumentException();
            }

            fridge.tell(new Fridge.ConsumeProduct(product, quantity));
        } catch (IndexOutOfBoundsException | IllegalArgumentException exception) {
            System.out.println("Invalid input");
        }
    }

    private void printUsage() {
        System.out.println("Available commands");
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("settemp         -               Set the temperature of the environment");
        System.out.println("reqtemp         -               Get the current temperature of the simulator");
        System.out.println("setweather      -               Set the weather of the environment");
        System.out.println("reqweather      -               Get the current weather of the simulator");
        System.out.println("acpower         -               Turn the AirCondition on and off");
        System.out.println("mediapower      -               Turn the MediaStation on and off");
        System.out.println("playmovie       -               Start and stop playing a movie on the mediastation");
        System.out.println("orderproduct    -               Create a new order");
        System.out.println("printorders     -               Print the order history of the fridge");
        System.out.println("printproducts   -               Print all products that are in the fridge");
        System.out.println("consume         -               Consume a product");
        System.out.println("exit            -               Exit the program");
    }
}
