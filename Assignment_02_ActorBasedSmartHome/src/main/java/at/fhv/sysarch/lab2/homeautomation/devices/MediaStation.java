package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

public class MediaStation extends AbstractBehavior<MediaStation.MediaStationCommand> {
    public interface MediaStationCommand{}

    public MediaStation(ActorContext<MediaStationCommand> context) {
        super(context);
    }

    @Override
    public Receive<MediaStationCommand> createReceive() {
        return null;
    }
}
