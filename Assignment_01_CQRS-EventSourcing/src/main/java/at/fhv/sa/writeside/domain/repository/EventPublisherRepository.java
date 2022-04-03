package at.fhv.sa.writeside.domain.repository;

import at.fhv.sa.eventside.event.BookingCanceled;
import at.fhv.sa.eventside.event.BookingCreated;

public interface EventPublisherRepository {
    Boolean publishBookingCreatedEvent(BookingCreated bookingCreated);
    Boolean publishBookingCanceledEvent(BookingCanceled bookingCanceled);
}
