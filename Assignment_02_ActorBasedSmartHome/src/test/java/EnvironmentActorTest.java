import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.WeatherSensor;
import at.fhv.sysarch.lab2.homeautomation.environment.EnvironmentActor;
import at.fhv.sysarch.lab2.homeautomation.environment.Weather;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class EnvironmentActorTest {
    private ActorTestKit testKit;

    @BeforeEach
    public void beforeEach() {
        testKit = ActorTestKit.create();
    }

    @AfterEach
    public void afterEach() {
        testKit.shutdownTestKit();
    }

    @Test
    public void when_requestTemperature_then_ReadTemperature() {
        TestProbe<TemperatureSensor.TemperatureCommand> testProbe = testKit.createTestProbe();
        ActorRef<EnvironmentActor.EnvironmentCommand> underTest = testKit.spawn(EnvironmentActor.create(), "Environment");

        underTest.tell(new EnvironmentActor.TemperatureRequest(testProbe.getRef()));
        testProbe.expectMessage(new TemperatureSensor.ReadTemperature(Optional.of(10.0)));
    }

    @Test
    public void when_requestWeatherCondition_then_ReadWeatherCondition() {
        TestProbe<WeatherSensor.WeatherCommand> testProbe = testKit.createTestProbe();
        ActorRef<EnvironmentActor.EnvironmentCommand> underTest = testKit.spawn(EnvironmentActor.create(), "Environment");

        underTest.tell(new EnvironmentActor.WeatherRequest(testProbe.getRef()));
        testProbe.expectMessage(new WeatherSensor.ReadWeatherCondition(Optional.of(Weather.SUNNY)));
    }
}
