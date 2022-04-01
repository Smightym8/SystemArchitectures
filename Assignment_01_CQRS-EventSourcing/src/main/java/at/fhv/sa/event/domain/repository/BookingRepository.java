package at.fhv.sa.event.domain.repository;

import java.util.Optional;

import at.fhv.sa.event.domain.model.Booking;

public interface BookingRepository {
    void add(Booking booking);

    Optional<Booking> bookingByReservationNumber (String reservationNumber);

    void remove(Booking booking);
}
