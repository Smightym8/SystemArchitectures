package at.fhv.sa.readside.infrastructure.impl;

import at.fhv.sa.readside.application.dto.BookingDTO;
import at.fhv.sa.readside.infrastructure.api.BookingDTORepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
public class BookingDTORepositoryImpl implements BookingDTORepository {
    private final List<BookingDTO> bookings = new ArrayList<>();

    @Override
    public void add(BookingDTO bookingDTO) {
        bookings.add(bookingDTO);
    }

    @Override
    public void remove(BookingDTO bookingDTO) {
        bookings.remove(bookingDTO);
    }

    @Override
    public BookingDTO getByReservationNumber(String reservationNumber) {
        return bookings.stream()
                .filter(booking -> booking.getReservationNumber().equals(reservationNumber)).findFirst().orElseThrow(
                        () -> new NoSuchElementException("Booking with reservation number " + reservationNumber + " not found")
                );
    }

    @Override
    public List<BookingDTO> getByTimePeriod(LocalDate startDate, LocalDate endDate) {
        return bookings.stream()
                .filter(booking -> booking.getStartDate().isAfter(startDate) || booking.getStartDate().isEqual(startDate))
                .filter(booking -> booking.getEndDate().isBefore(endDate) || booking.getEndDate().isEqual(endDate))
                .collect(Collectors.toList());
    }
}