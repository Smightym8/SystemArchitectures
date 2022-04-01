package at.fhv.sa.event.infrastructure;

import at.fhv.sa.event.domain.model.Booking;
import at.fhv.sa.event.domain.repository.BookingRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BookingRepositoryImpl implements BookingRepository {
    private List<Booking> bookings = new ArrayList<>();

    @Override
    public void add(Booking booking) {
        bookings.add(booking);
    }

    @Override
    public Optional<Booking> bookingByReservationNumber(String reservationNumber) {
        return bookings.stream().filter(b -> b.getReservationNumber().equals(reservationNumber)).findFirst();
    }

    @Override
    public void remove(Booking booking) {
        bookings.remove(booking);
    }
}
