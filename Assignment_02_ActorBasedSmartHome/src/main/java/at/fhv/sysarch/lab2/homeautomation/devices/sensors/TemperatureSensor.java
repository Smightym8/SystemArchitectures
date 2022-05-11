package at.fhv.sysarch.lab2.homeautomation.devices.sensors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.AirCondition;
import at.fhv.sysarch.lab2.homeautomation.environment.EnvironmentActor;

import java.util.Optional;

public class TemperatureSensor extends AbstractBehavior<TemperatureSensor.TemperatureCommand> {

    public interface TemperatureCommand {}

    public static final class RequestTemperature implements TemperatureCommand {
        public RequestTemperature() {

        }
    }

    public static final class ReadTemperature implements TemperatureCommand {
        final Optional<Double> value;

        public ReadTemperature(Optional<Double> value) {
            this.value = value;
        }
    }


    private final String groupId;
    private final String deviceId;
    private ActorRef<EnvironmentActor.EnvironmentCommand> environment;
    private ActorRef<AirCondition.AirConditionCommand> airCondition;

    public static Behavior<TemperatureCommand> create(ActorRef<EnvironmentActor.EnvironmentCommand> environment, ActorRef<AirCondition.AirConditionCommand> airCondition, String groupId, String deviceId) {
        return Behaviors.setup(context -> new TemperatureSensor(context, environment, airCondition, groupId, deviceId));
    }

    private TemperatureSensor(
            ActorContext<TemperatureCommand> context,
            ActorRef<EnvironmentActor.EnvironmentCommand> environment,
            ActorRef<AirCondition.AirConditionCommand> airCondition,
            String groupId,
            String deviceId
    ) {
        super(context);
        this.environment = environment;
        this.airCondition = airCondition;
        this.groupId = groupId;
        this.deviceId = deviceId;

        getContext().getLog().info("TemperatureSensor started");
    }

    @Override
    public Receive<TemperatureCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(RequestTemperature.class, this::onRequestTemperature)
                .onMessage(ReadTemperature.class, this::onReadTemperature)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<TemperatureCommand> onRequestTemperature(RequestTemperature rt) {
        getContext().getLog().info("Requesting environment for current temperature");
        environment.tell(new EnvironmentActor.TemperatureRequest(getContext().getSelf()));
        return this;
    }

    private Behavior<TemperatureCommand> onReadTemperature(ReadTemperature r) {
        getContext().getLog().info("TemperatureSensor received {}", r.value.get());
        this.airCondition.tell(new AirCondition.EnrichedTemperature(r.value, Optional.of("Celsius")));
        return this;
    }

    private TemperatureSensor onPostStop() {
        getContext().getLog().info("TemperatureSensor actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}
