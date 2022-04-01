package at.fhv.sa.event;

import at.fhv.sa.event.domain.events.Event;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventRepository {
    private final List<Event> events = new ArrayList<>();

    public void processEvent(Event event) {
        events.add(event);
        // TODO: notify subscribed read repositories
    }
}
