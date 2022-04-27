package at.fhv.sysarch.lab2.homeautomation.devices.sensors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.BlindCondition;

import java.util.Optional;

public class WeatherSensor extends AbstractBehavior<WeatherSensor.WeatherCommand> {
    public interface WeatherCommand {}

    public static final class ReadWeatherCondition implements WeatherCommand {
        final Optional<String> weatherCondition;

        public ReadWeatherCondition(Optional<String> weatherCondition) {
            this.weatherCondition = weatherCondition;
        }
    }

    public static Behavior<WeatherCommand> create(ActorRef<BlindCondition.BlindCommand> blindCondition, String groupId, String deviceId) {
        return Behaviors.setup(context -> new WeatherSensor(context, blindCondition, groupId, deviceId));
    }

    private final String groupId;
    private final String deviceId;
    private ActorRef<BlindCondition.BlindCommand> blindCondition;

    public WeatherSensor(ActorContext<WeatherCommand> context, ActorRef<BlindCondition.BlindCommand> blindCondition, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.blindCondition = blindCondition;

        getContext().getLog().info("WeatherSensor started");
    }

    @Override
    public Receive<WeatherCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReadWeatherCondition.class, this::onReadWeatherCondition)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<WeatherCommand> onReadWeatherCondition(ReadWeatherCondition weatherCondition) {
        getContext().getLog().info("WeatherSensor received {}", weatherCondition.weatherCondition.get());
        // Tell blinds and return same behaviour
        blindCondition.tell(new BlindCondition.ChangedWeather(weatherCondition.weatherCondition));
        return this;
    }

    private WeatherSensor onPostStop() {
        getContext().getLog().info("WeatherSensor actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}
