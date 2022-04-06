package at.fhv.sa.readside.projection;

import at.fhv.sa.eventside.event.BookingCanceled;
import at.fhv.sa.eventside.event.BookingCreated;

public interface Projector {
    void processBookingCreatedEvent(BookingCreated bookingCreated);
    void processBookingCanceledEvent(BookingCanceled bookingCanceled);
}
