package at.fhv.sa.eventside.rest;

import at.fhv.sa.eventside.event.BookingCanceled;
import at.fhv.sa.eventside.event.BookingCreated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventRestController {

    @PostMapping(value = "/bookingcreatedevent", consumes = "application/json", produces = "application/json")
    public boolean bookingCreatedRest(@RequestBody BookingCreated bookingCreated) {
        System.out.println("Received:");
        System.out.println(bookingCreated);
        return true;
    }

    @PostMapping(value = "/bookingcanceledevent", consumes = "application/json", produces = "application/json")
    public boolean bookingCanceledRest(@RequestBody BookingCanceled bookingCanceled) {
        System.out.println("Received:");
        System.out.println(bookingCanceled);
        return true;
    }
}
