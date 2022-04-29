package at.fhv.sysarch.lab2.homeautomation.environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.AirCondition;
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
        Optional<Boolean> isSunny;

        public WeatherChanger(Optional<Boolean> isSunny) {
            this.isSunny = isSunny;
        }
    }

    private double temperature = 10;
    private boolean isSunny = false;
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
        this.weatherTimerScheduler.startTimerAtFixedRate(new WeatherChanger(Optional.of(isSunny)), Duration.ofSeconds(20));
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

        Random r = new Random();

        if(isSunny) {
            temperature = 15 + (35 - 15) * r.nextDouble();
        } else {
            temperature = 0 + 15 * r.nextDouble();
        }

        return this;
    }

    private Behavior<EnvironmentCommand> onWeatherChange(WeatherChanger w) {
        // Tell the WeatherSensor that the weather changed
        if(isSunny) {
            getContext().getLog().info("Weather changed to sunny");

            weatherSensor.tell(
                    new WeatherSensor.ReadWeatherCondition(Optional.of("sunny"))
            );
        } else {
            getContext().getLog().info("Weather changed to not sunny");
            weatherSensor.tell(
                    new WeatherSensor.ReadWeatherCondition(Optional.of("foggy"))
            );
        }



        // Switch random between sunny and not sunny
        Random random = new Random();
        isSunny = random.nextBoolean();

        return this;
    }

    private EnvironmentActor onPostStop() {
        getContext().getLog().info("EnvironmentActor stopped");
        return this;
    }
}
