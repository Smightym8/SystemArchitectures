package at.fhv.sa.event.domain.events;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class BookingCreated implements Event {
    private final String reservationNumber;
    private final String guestFirstName;
    private final String guestLastName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<String> roomNumbers;
    private final long timestamp;

    public BookingCreated(String reservationNumber, String guestFirstName, String guestLastName, LocalDate startDate,
                          LocalDate endDate, List<String> roomNumbers) {
        this.reservationNumber = reservationNumber;
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roomNumbers = roomNumbers;
        this.timestamp = new Timestamp(System.currentTimeMillis()).getTime();
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public String getGuestFirstName() {
        return guestFirstName;
    }

    public String getGuestLastName() {
        return guestLastName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<String> getRoomNumbers() {
        return roomNumbers;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "BookingCreated{" +
                "reservationNumber='" + reservationNumber + '\'' +
                ", guestFirstName='" + guestFirstName + '\'' +
                ", guestLastName='" + guestLastName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", roomNumbers=" + roomNumbers +
                ", timestamp=" + timestamp +
                '}';
    }
}
