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
    // TODO: Maybe move interface and static classes into own files
    public interface EnvironmentCommand {}

    public static final class TemperatureChanger implements EnvironmentCommand {
        Optional<Double> temperature;

        public TemperatureChanger(Optional<Double> temperature) {
            this.temperature = temperature;
        }
    }

    public static final class WeatherChanger implements EnvironmentCommand {
        Optional<Weather> currentWeater;

        public WeatherChanger(Optional<Weather> weather) {
            this.currentWeater = weather;
        }
    }

    private double temperature = 10;
    private Weather weather = Weather.SUNNY;
    private final TimerScheduler<EnvironmentCommand> temperatureTimerScheduler;
    private final TimerScheduler<EnvironmentCommand> weatherTimerScheduler;

    private final ActorRef<TemperatureSensor.TemperatureCommand> temperatureSensor;
    private final ActorRef<WeatherSensor.WeatherCommand> weatherSensor;

    public static Behavior<EnvironmentCommand> create(ActorRef<TemperatureSensor.TemperatureCommand> temperatureSensor, ActorRef<WeatherSensor.WeatherCommand> weatherSensor) {
        return Behaviors.setup(context -> Behaviors.withTimers(timers -> new EnvironmentActor(context, temperatureSensor, weatherSensor, timers, timers)));
    }

    public EnvironmentActor(
            ActorContext<EnvironmentCommand> context,
            ActorRef<TemperatureSensor.TemperatureCommand> temperatureSensor,
            ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
            TimerScheduler<EnvironmentCommand> tempTimer,
            TimerScheduler<EnvironmentCommand> weatherTimer
    ) {
        super(context);
        this.temperatureSensor = temperatureSensor;
        this.weatherSensor = weatherSensor;
        this.temperatureTimerScheduler = tempTimer;
        this.weatherTimerScheduler = weatherTimer;
        this.temperatureTimerScheduler.startTimerAtFixedRate(new TemperatureChanger(Optional.of(temperature)), Duration.ofSeconds(5));
        this.weatherTimerScheduler.startTimerAtFixedRate(new WeatherChanger(Optional.of(weather)), Duration.ofSeconds(20));
    }

    @Override
    public Receive<EnvironmentCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TemperatureChanger.class, this::onTemperatureChange)
                .onMessage(WeatherChanger.class, this::onWeatherChange)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<EnvironmentCommand> onTemperatureChange(TemperatureChanger t) {
        // Tell TemperatureSensor that the temperature changed
        getContext().getLog().info("Temperature changed to {}", new DecimalFormat("0.00").format(temperature));

        temperatureSensor.tell(
                new TemperatureSensor.ReadTemperature(Optional.of(temperature))
        );

        Random random = new Random();

        switch (weather) {
            case SUNNY:
                temperature = 21 + (35 - 20) * random.nextDouble();
                break;
            case FOGGY:
                temperature = 0 + 10 * random.nextDouble();
                break;
            case RAINY:
                temperature = 11 + (20 - 10) * random.nextDouble();
                break;
            // default: temperature = 0;
        }

        return this;
    }

    private Behavior<EnvironmentCommand> onWeatherChange(WeatherChanger w) {
        // Tell the WeatherSensor that the weather changed
        getContext().getLog().info("Weather changed to " + weather.getFriendlyName());
        weatherSensor.tell(new WeatherSensor.ReadWeatherCondition(Optional.of(weather)));

        // Switch random between sunny and not sunny
        Random random = new Random();
        weather = Weather.values()[(random.nextInt(2))];

        return this;
    }

    private EnvironmentActor onPostStop() {
        getContext().getLog().info("EnvironmentActor stopped");
        return this;
    }
}
