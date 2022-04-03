package at.fhv.sa.eventside;

import at.fhv.sa.eventside.event.Event;
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
