package at.fhv.sa.eventside.rest;

import at.fhv.sa.eventside.infrastructure.EventRepository;
import at.fhv.sa.eventside.event.BookingCanceled;
import at.fhv.sa.eventside.event.BookingCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventRestController {
    @Autowired
    EventRepository eventRepository;

    @PostMapping(value = "/bookingcreatedevent", consumes = "application/json", produces = "application/json")
    public boolean bookingCreatedRest(@RequestBody BookingCreated bookingCreated) {
        eventRepository.processEvent(bookingCreated);
        return true;
    }

    @PostMapping(value = "/bookingcanceledevent", consumes = "application/json", produces = "application/json")
    public boolean bookingCanceledRest(@RequestBody BookingCanceled bookingCanceled) {
        eventRepository.processEvent(bookingCanceled);
        return true;
    }
}
