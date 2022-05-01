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
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.WeatherSensor;
import at.fhv.sysarch.lab2.homeautomation.environment.EnvironmentActor;

import java.util.Optional;
import java.util.Scanner;

public class UI extends AbstractBehavior<Void> {
    private ActorRef<EnvironmentActor.EnvironmentCommand> environment;
    private ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
    private ActorRef<WeatherSensor.WeatherCommand> weatherSensor;
    private ActorRef<AirCondition.AirConditionCommand> airCondition;
    private ActorRef<BlindCondition.BlindCommand> blindCondition;

    public static Behavior<Void> create(
            ActorRef<EnvironmentActor.EnvironmentCommand> environment,
            ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
            ActorRef<AirCondition.AirConditionCommand> airCondition,
            ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
            ActorRef<BlindCondition.BlindCommand> blindCondition) {
        return Behaviors.setup(context -> new UI(context, environment, tempSensor, airCondition, weatherSensor, blindCondition));
    }

    private  UI(
            ActorContext<Void> context,
            ActorRef<EnvironmentActor.EnvironmentCommand> environment,
            ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
            ActorRef<AirCondition.AirConditionCommand> airCondition,
            ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
            ActorRef<BlindCondition.BlindCommand> blindCondition) {
        super(context);
        // TODO: implement actor and behavior as needed
        // TODO: move UI initialization to appropriate place
        this.environment = environment;
        this.airCondition = airCondition;
        this.tempSensor = tempSensor;
        this.blindCondition = blindCondition;
        this.weatherSensor = weatherSensor;
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

    public void runCommandLine() {
        // TODO: Create Actor for UI Input-Handling
        Scanner scanner = new Scanner(System.in);
        String[] input = null;
        String reader = "";


        while (!reader.equalsIgnoreCase("quit") && scanner.hasNextLine()) {
            reader = scanner.nextLine();
            // TODO: change input handling
            String[] command = reader.split(" ");
            if(command[0].equals("t")) {
                this.tempSensor.tell(new TemperatureSensor.ReadTemperature(Optional.of(Double.valueOf(command[1]))));
            }

            if(command[0].equals("a")) {
                this.airCondition.tell(new AirCondition.PowerAirCondition(Optional.of(Boolean.valueOf(command[1]))));
            }

            if(command[0].equals("w")) {
                this.weatherSensor.tell(new WeatherSensor.ReadWeatherCondition(Optional.of(EnvironmentActor.WeatherChanger.Weather.valueOf(command[1]))));
            }

            // TODO: process Input
        }
        getContext().getLog().info("UI done");
    }
}
