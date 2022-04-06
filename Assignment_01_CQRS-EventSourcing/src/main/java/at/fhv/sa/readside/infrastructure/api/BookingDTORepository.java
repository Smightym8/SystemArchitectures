package at.fhv.sa.readside.infrastructure.api;

import at.fhv.sa.readside.application.dto.BookingDTO;

import java.time.LocalDate;
import java.util.List;

public interface BookingDTORepository {
    void add(BookingDTO bookingDTO);

    void remove(BookingDTO bookingDTO);

    BookingDTO getByReservationNumber(String reservationNumber);

    List<BookingDTO> getByTimePeriod(LocalDate startDate, LocalDate endDate);
}
