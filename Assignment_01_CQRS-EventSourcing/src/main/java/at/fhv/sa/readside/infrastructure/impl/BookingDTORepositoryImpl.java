package at.fhv.sa.readside.infrastructure.impl;

import at.fhv.sa.readside.application.dto.BookingDTO;
import at.fhv.sa.readside.infrastructure.api.BookingDTORepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingDTORepositoryImpl implements BookingDTORepository {
    private final List<BookingDTO> bookings = new ArrayList<>();

    @Override
    public void add(BookingDTO bookingDTO) {
        bookings.add(bookingDTO);
        System.out.println(bookings.size());
    }

    @Override
    public void remove(BookingDTO bookingDTO) {
        bookings.remove(bookingDTO);
    }

    @Override
    public List<BookingDTO> getByTimePeriod(LocalDate startDate, LocalDate endDate) {
        return bookings.stream()
                .filter(booking -> booking.getStartDate().isAfter(startDate))
                .filter(booking -> booking.getEndDate().isBefore(endDate))
                .collect(Collectors.toList());
    }
}