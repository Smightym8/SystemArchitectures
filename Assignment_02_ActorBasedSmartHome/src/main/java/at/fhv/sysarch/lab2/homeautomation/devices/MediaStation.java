package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

public class MediaStation extends AbstractBehavior<MediaStation.MediaStationCommand> {
    public interface MediaStationCommand{}

    public static final class PowerMediaStation implements MediaStationCommand {
        final Optional<Boolean> value;

        public PowerMediaStation(Optional<Boolean> value) {
            this.value = value;
        }
    }

    public static final class PlayMovie implements MediaStationCommand {
        final Optional<Boolean> value;

        public PlayMovie(Optional<Boolean> value) {
            this.value = value;
        }
    }

    private ActorRef<BlindCondition.BlindCommand> blindCondition;
    private final String groupId;
    private final String deviceId;
    private boolean poweredOn = true;
    private boolean isPlaying = false;

    public static Behavior<MediaStationCommand> create(ActorRef<BlindCondition.BlindCommand> blindCondition, String groupId, String deviceId) {
        return Behaviors.setup(context -> new MediaStation(context, blindCondition, groupId, deviceId));
    }

    public MediaStation(ActorContext<MediaStationCommand> context, ActorRef<BlindCondition.BlindCommand> blindCondition, String groupId, String deviceId) {
        super(context);
        this.blindCondition = blindCondition;
        this.groupId = groupId;
        this.deviceId = deviceId;
    }

    @Override
    public Receive<MediaStationCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(PowerMediaStation.class, this::onPowerMediaStationOff)
                .onMessage(PlayMovie.class, this::onPlayMovie)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<MediaStationCommand> onPowerMediaStationOn(PowerMediaStation powerMediaStation) {
        getContext().getLog().info("Turning MediaStation to {}", powerMediaStation.value.get());
        if(powerMediaStation.value.get() == true) {
            return this.powerOn();
        }

        return this;
    }

    private Behavior<MediaStationCommand> onPowerMediaStationOff(PowerMediaStation powerMediaStation) {
        getContext().getLog().info("Turning MediaStation to {}", powerMediaStation.value.get());

        if(powerMediaStation.value.get() == false) {
            return this.powerOff();
        }

        return this;
    }

    private Behavior<MediaStationCommand> onPlayMovie(PlayMovie playMovie) {
        if(isPlaying == false && playMovie.value.get() == true) {
            getContext().getLog().info("Turning MediaStation to playing movie {}", playMovie.value.get());
            this.isPlaying = true;
        } else if(isPlaying == true && playMovie.value.get() == true) {
            getContext().getLog().info("MediaStation is already playing a movie!");
        } else if(isPlaying == true && playMovie.value.get() == false) {
            getContext().getLog().info("Turning MediaStation to playing movie {}", playMovie.value.get());
            this.isPlaying = false;
        }

        blindCondition.tell(new BlindCondition.ChangedMoviePlaying(Optional.of(isPlaying)));

        return this; // return same behavior
    }

    private Behavior<MediaStationCommand> powerOn() {
        this.poweredOn = true;
        // Change Behavior
        return Behaviors.receive(MediaStation.MediaStationCommand.class)
                .onMessage(PowerMediaStation.class, this::onPowerMediaStationOff)
                .onMessage(PlayMovie.class, this::onPlayMovie)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<MediaStationCommand> powerOff() {
        this.poweredOn = false;
        this.isPlaying = false;

        blindCondition.tell(new BlindCondition.ChangedMoviePlaying(Optional.of(isPlaying)));
        // Change Behavior
        return Behaviors.receive(MediaStation.MediaStationCommand.class)
                .onMessage(PowerMediaStation.class, this::onPowerMediaStationOn)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private MediaStation onPostStop() {
        getContext().getLog().info("MediaStation actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}
