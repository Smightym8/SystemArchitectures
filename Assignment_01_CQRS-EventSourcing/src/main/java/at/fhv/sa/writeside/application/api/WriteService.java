package at.fhv.sa.writeside.application.api;

import java.time.LocalDate;
import java.util.List;

public interface WriteService {
    String bookRoom(LocalDate startDate, LocalDate endDate, List<String> roomNumbers, String guestFirstName, String guestLastName, int amountOfGuests);
    boolean cancelBooking(String reservationNumber);
}
