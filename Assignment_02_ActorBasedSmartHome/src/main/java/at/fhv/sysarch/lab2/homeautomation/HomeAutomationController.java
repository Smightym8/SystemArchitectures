package at.fhv.sysarch.lab2.homeautomation;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.AirCondition;
import at.fhv.sysarch.lab2.homeautomation.devices.Blind;
import at.fhv.sysarch.lab2.homeautomation.devices.MediaStation;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Fridge;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.WeatherSensor;
import at.fhv.sysarch.lab2.homeautomation.environment.EnvironmentActor;
import at.fhv.sysarch.lab2.homeautomation.ui.UI;

public class HomeAutomationController extends AbstractBehavior<Void> {
    private ActorRef<EnvironmentActor.EnvironmentCommand> environment;
    private ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
    private ActorRef<WeatherSensor.WeatherCommand> weatherSensor;
    private  ActorRef<AirCondition.AirConditionCommand> airCondition;
    private ActorRef<Blind.BlindCommand> blindCondition;
    private ActorRef<MediaStation.MediaStationCommand> mediaStationCondition;
    private ActorRef<Fridge.FridgeCommand> fridge;

    public static Behavior<Void> create() {
        return Behaviors.setup(HomeAutomationController::new);
    }

    private  HomeAutomationController(ActorContext<Void> context) {
        super(context);
        this.environment = getContext().spawn(EnvironmentActor.create(), "Environment");
        this.airCondition = getContext().spawn(AirCondition.create("2", "1"), "AirCondition");
        this.tempSensor = getContext().spawn(TemperatureSensor.create(this.environment, this.airCondition, "2", "2"), "temperatureSensor");
        this.blindCondition = getContext().spawn(Blind.create("1", "1"), "BlindCondition");
        this.mediaStationCondition = getContext().spawn(MediaStation.create(this.blindCondition, "1", "2"), "MediaStation");
        this.weatherSensor = getContext().spawn(WeatherSensor.create(this.environment, this.blindCondition, "1", "3"), "WeatherSensor");
        this.fridge = getContext().spawn(Fridge.create("3", "1"), "Fridge");

        ActorRef<Void> ui = getContext().spawn(UI.create(this.environment, this.tempSensor, this.airCondition,
                this.weatherSensor, this.blindCondition, this.mediaStationCondition, this.fridge), "UI");
        getContext().getLog().info("HomeAutomation Application started");
    }

    @Override
    public Receive<Void> createReceive() {
        return newReceiveBuilder().onSignal(PostStop.class, signal -> onPostStop()).build();
    }

    private HomeAutomationController onPostStop() {
        getContext().getLog().info("HomeAutomation Application stopped");
        return this;
    }
}
