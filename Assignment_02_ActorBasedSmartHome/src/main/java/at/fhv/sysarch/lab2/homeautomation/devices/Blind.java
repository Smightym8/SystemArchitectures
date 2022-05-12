package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.environment.Weather;

import java.util.Optional;

public class Blind extends AbstractBehavior<Blind.BlindCommand> {
    public interface BlindCommand {}

    public static final class ChangedWeather implements BlindCommand {
        Optional<Weather> weatherCondition;

        public ChangedWeather(Optional<Weather> weatherCondition) {
            this.weatherCondition = weatherCondition;
        }
    }

    public static final class ChangedMoviePlaying implements BlindCommand {
        Optional<Boolean> isPlaying;

        public ChangedMoviePlaying(Optional<Boolean> isPlaying) {
            this.isPlaying = isPlaying;
        }
    }

    private final String groupId;
    private final String deviceId;
    private boolean isOpen = true;
    private boolean isMediaStationMoviePlaying = false;
    private Weather currentWeather = Weather.RAINY;

    public static Behavior<BlindCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new Blind(context, groupId, deviceId));
    }

    private Blind(ActorContext<BlindCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
    }

    @Override
    public Receive<BlindCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ChangedWeather.class, this::onReadWeatherCondition)
                .onMessage(ChangedMoviePlaying.class, this::onChangedMoviePlaying)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<BlindCommand> onReadWeatherCondition(ChangedWeather changedWeather) {
        getContext().getLog().info("Blind reading {}", changedWeather.weatherCondition.get().getFriendlyName());

        this.currentWeather = changedWeather.weatherCondition.get();
        changeBlindCondition();

        return this; // Same behavior
    }

    private Behavior<BlindCommand> onChangedMoviePlaying(ChangedMoviePlaying changedMoviePlaying){
        getContext().getLog().info("Blinds reading from MediaStation {}", changedMoviePlaying.isPlaying.get());

        this.isMediaStationMoviePlaying = changedMoviePlaying.isPlaying.get();
        changeBlindCondition();

        return this;
    }

    private void changeBlindCondition() {
        if(this.isMediaStationMoviePlaying == false && !this.currentWeather.equals(Weather.SUNNY)) {
            getContext().getLog().info("Blinds opened");
            this.isOpen = true;
        } else if(this.isMediaStationMoviePlaying == false && this.currentWeather.equals(Weather.SUNNY)) {
            getContext().getLog().info("Blinds closed");
            this.isOpen = false;
        } else if(this.isMediaStationMoviePlaying == true) {
            getContext().getLog().info("Blinds closed");
            this.isOpen = false;
        }
    }

    private Blind onPostStop() {
        getContext().getLog().info("Blind actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}
