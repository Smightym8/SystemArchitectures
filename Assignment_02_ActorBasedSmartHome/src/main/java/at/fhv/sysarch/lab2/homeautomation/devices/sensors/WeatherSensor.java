package at.fhv.sysarch.lab2.homeautomation.devices.sensors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.BlindCondition;
import at.fhv.sysarch.lab2.homeautomation.environment.EnvironmentActor;
import at.fhv.sysarch.lab2.homeautomation.environment.Weather;

import java.util.Optional;

public class WeatherSensor extends AbstractBehavior<WeatherSensor.WeatherCommand> {
    public interface WeatherCommand {}

    public static final class RequestWeather implements WeatherCommand {
        public RequestWeather(){}
    }

    public static final class ReadWeatherCondition implements WeatherCommand {
        final Optional<Weather> weatherCondition;

        public ReadWeatherCondition(Optional<Weather> weatherCondition) {
            this.weatherCondition = weatherCondition;
        }
    }

    private final String groupId;
    private final String deviceId;
    private ActorRef<EnvironmentActor.EnvironmentCommand> environment;
    private ActorRef<BlindCondition.BlindCommand> blindCondition;

    public static Behavior<WeatherCommand> create(ActorRef<EnvironmentActor.EnvironmentCommand> environment, ActorRef<BlindCondition.BlindCommand> blindCondition, String groupId, String deviceId) {
        return Behaviors.setup(context -> new WeatherSensor(context, environment, blindCondition, groupId, deviceId));
    }

    public WeatherSensor(ActorContext<WeatherCommand> context, ActorRef<EnvironmentActor.EnvironmentCommand> environment,  ActorRef<BlindCondition.BlindCommand> blindCondition, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.environment = environment;
        this.blindCondition = blindCondition;

        getContext().getLog().info("WeatherSensor started");
    }

    @Override
    public Receive<WeatherCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(RequestWeather.class, this::onRequestWeather)
                .onMessage(ReadWeatherCondition.class, this::onReadWeatherCondition)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<WeatherCommand> onRequestWeather(RequestWeather rw) {
        environment.tell(new EnvironmentActor.WeatherRequest(getContext().getSelf()));
        return this;
    }

    private Behavior<WeatherCommand> onReadWeatherCondition(ReadWeatherCondition weatherCondition) {
        getContext().getLog().info("WeatherSensor received {}", weatherCondition.weatherCondition.get().getFriendlyName());
        // Tell blinds and return same behaviour
        blindCondition.tell(new BlindCondition.ChangedWeather(weatherCondition.weatherCondition));
        return this;
    }

    private WeatherSensor onPostStop() {
        getContext().getLog().info("WeatherSensor actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}
