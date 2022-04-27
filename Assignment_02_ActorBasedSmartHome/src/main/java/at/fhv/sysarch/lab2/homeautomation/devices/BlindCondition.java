package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

public class BlindCondition extends AbstractBehavior<BlindCondition.BlindCommand> {
    public interface BlindCommand {}

    public static final class ChangedWeather implements BlindCommand {
        Optional<String> weatherCondition;

        public ChangedWeather(Optional<String> weatherCondition) {
            this.weatherCondition = weatherCondition;
        }
    }

    private final String groupId;
    private final String deviceId;
    private boolean isOpen = true;

    public static Behavior<BlindCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new BlindCondition(context, groupId, deviceId));
    }

    public BlindCondition(ActorContext<BlindCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
    }

    @Override
    public Receive<BlindCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ChangedWeather.class, this::onReadWeatherCondition)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<BlindCommand> onReadWeatherCondition(ChangedWeather changedWeather) {
        getContext().getLog().info("Blind reading {}", changedWeather.weatherCondition.get());

        String weatherCondition = changedWeather.weatherCondition.get();
        // TODO: Check if movie is playing
        if(weatherCondition.equals("sunny")) {
            getContext().getLog().info("Blinds closed");
            this.isOpen = false;
        } else {
            getContext().getLog().info("Blinds opened");
            this.isOpen = false;
        }

        return this; // Same behavior
    }

    private BlindCondition onPostStop() {
        getContext().getLog().info("Blind actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}
