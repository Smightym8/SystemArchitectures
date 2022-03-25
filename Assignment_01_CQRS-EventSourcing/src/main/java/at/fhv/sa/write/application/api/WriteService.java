package at.fhv.sa.write.application.api;

import at.fhv.sa.write.domain.Guest;

import java.time.LocalDate;

public interface WriteService {
    void bookRoom(LocalDate startDate, LocalDate endDate, String roomNumber, Guest guest);
    void cancelBooking(String reservationNumber);
}
