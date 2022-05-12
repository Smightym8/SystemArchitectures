package at.fhv.sysarch.lab2.homeautomation.environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.WeatherSensor;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;

public class EnvironmentActor extends AbstractBehavior<EnvironmentActor.EnvironmentCommand> {
    public interface EnvironmentCommand {}

    public static final class TemperatureRequest implements EnvironmentCommand {
        ActorRef<TemperatureSensor.TemperatureCommand> replyTo;

        public TemperatureRequest(ActorRef<TemperatureSensor.TemperatureCommand> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static final class WeatherRequest implements EnvironmentCommand {
        ActorRef<WeatherSensor.WeatherCommand> replyTo;

        public WeatherRequest(ActorRef<WeatherSensor.WeatherCommand> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static final class TemperatureChanger implements EnvironmentCommand {
        Optional<Double> temperature;

        public TemperatureChanger(Optional<Double> temperature) {
            this.temperature = temperature;
        }
    }

    public static final class WeatherChanger implements EnvironmentCommand {
        Optional<Weather> currentWeather;

        public WeatherChanger(Optional<Weather> weather) {
            this.currentWeather = weather;
        }
    }

    private double currentTemperature = 10;
    private Weather currentWeather = Weather.SUNNY;
    private final TimerScheduler<EnvironmentCommand> temperatureTimerScheduler;
    private final TimerScheduler<EnvironmentCommand> weatherTimerScheduler;

    public static Behavior<EnvironmentCommand> create() {
        return Behaviors.setup(context -> Behaviors.withTimers(timers -> new EnvironmentActor(context, timers, timers)));
    }

    private EnvironmentActor(
            ActorContext<EnvironmentCommand> context,
            TimerScheduler<EnvironmentCommand> tempTimer,
            TimerScheduler<EnvironmentCommand> weatherTimer
    ) {
        super(context);
        this.temperatureTimerScheduler = tempTimer;
        this.weatherTimerScheduler = weatherTimer;
        this.temperatureTimerScheduler.startTimerAtFixedRate(new TemperatureChanger(Optional.of(currentTemperature)), Duration.ofSeconds(5));
        this.weatherTimerScheduler.startTimerAtFixedRate(new WeatherChanger(Optional.of(currentWeather)), Duration.ofSeconds(20));
    }

    @Override
    public Receive<EnvironmentCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TemperatureRequest.class, this::onTemperatureRequest)
                .onMessage(TemperatureChanger.class, this::onTemperatureChange)
                .onMessage(WeatherRequest.class, this::onWeatherRequest)
                .onMessage(WeatherChanger.class, this::onWeatherChange)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<EnvironmentCommand> onTemperatureRequest(TemperatureRequest tr) {
        getContext().getLog().info("Telling {} the current temperature", tr.replyTo);
        tr.replyTo.tell(new TemperatureSensor.ReadTemperature(Optional.of(currentTemperature)));
        return this;
    }

    private Behavior<EnvironmentCommand> onTemperatureChange(TemperatureChanger t) {
        //getContext().getLog().info("Temperature changed to {}", new DecimalFormat("0.00").format(t.temperature.get()));

        Random random = new Random();

        switch (currentWeather) {
            case SUNNY:
                currentTemperature = t.temperature.get() + (35 - 20) * random.nextDouble();
                break;
            case FOGGY:
                currentTemperature = t.temperature.get() + 10 * random.nextDouble();
                break;
            case RAINY:
                currentTemperature = t.temperature.get() + (20 - 10) * random.nextDouble();
                break;
            // default: temperature = 0;
        }

        return this;
    }

    private Behavior<EnvironmentCommand> onWeatherRequest(WeatherRequest wr) {
        getContext().getLog().info("Telling {} the current weather", wr.replyTo);
        wr.replyTo.tell(new WeatherSensor.ReadWeatherCondition(Optional.of(currentWeather)));
        return this;
    }

    private Behavior<EnvironmentCommand> onWeatherChange(WeatherChanger w) {
        // Tell the WeatherSensor that the weather changed
        //getContext().getLog().info("Weather changed to " + currentWeather.getFriendlyName());

        // Switch random between sunny and not sunny
        Random random = new Random();
        currentWeather = Weather.values()[(random.nextInt(2))];

        return this;
    }

    private EnvironmentActor onPostStop() {
        getContext().getLog().info("EnvironmentActor stopped");
        return this;
    }
}
