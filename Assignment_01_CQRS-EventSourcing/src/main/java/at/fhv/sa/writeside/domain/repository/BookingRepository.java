package at.fhv.sa.writeside.domain.repository;

import at.fhv.sa.writeside.domain.model.Booking;

import java.util.Optional;

public interface BookingRepository {
    void add(Booking booking);

    Optional<Booking> bookingByReservationNumber (String reservationNumber);

    void remove(Booking booking);
}
