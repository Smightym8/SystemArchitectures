package at.fhv.sa.event.application.api;

import at.fhv.sa.event.domain.model.Guest;

import java.time.LocalDate;
import java.util.List;

public interface WriteService {
    String bookRoom(LocalDate startDate, LocalDate endDate, List<String> roomNumbers, Guest guest, int amountOfGuests);
    boolean cancelBooking(String reservationNumber);
}
