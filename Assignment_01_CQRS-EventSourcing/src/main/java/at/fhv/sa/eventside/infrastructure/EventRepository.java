package at.fhv.sa.eventside.infrastructure;

import at.fhv.sa.eventside.event.Event;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventRepository {
    private final List<Event> events = new ArrayList<>();
    private List<EventSubscriber> eventSubscribers = List.of(
            new EventSubscriber("http://localhost:8082", "/bookingcreatedevent", "/bookingcanceledevent")
    );

    public void processEvent(Event event) {
        events.add(event);
        eventSubscribers.forEach(eventSubscriber -> eventSubscriber.getNotified(event));
    }
}