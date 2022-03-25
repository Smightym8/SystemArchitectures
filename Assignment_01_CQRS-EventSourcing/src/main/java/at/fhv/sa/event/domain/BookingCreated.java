package at.fhv.sa.event.domain;

import java.time.LocalDate;

public class BookingCreated implements Event {
    private String reservationNumber;
    private String guestFirstName;
    private String guestLastName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String roomNumber;
    private long timestamp;


    public BookingCreated(String reservationNumber, String guestFirstName, String guestLastName, LocalDate startDate,
                          LocalDate endDate, String roomNumber, long timestamp) {
        this.reservationNumber = reservationNumber;
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roomNumber = roomNumber;
        this.timestamp = timestamp;
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

    public String getRoomNumber() {
        return roomNumber;
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
                ", roomNumber='" + roomNumber + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
