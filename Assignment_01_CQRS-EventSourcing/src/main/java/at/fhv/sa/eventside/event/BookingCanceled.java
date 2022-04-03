package at.fhv.sa.eventside.event;

import java.sql.Timestamp;
import java.util.List;

public class BookingCanceled extends Event {
    private final String reservationNumber;
    private final String guestFirstName;
    private final String guestLastName;
    private final List<String> roomNumbers;
    public BookingCanceled(String reservationNumber, String guestFirstName, String guestLastName, List<String> roomNumbers) {
        this.reservationNumber = reservationNumber;
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.roomNumbers = roomNumbers;
        setTimestamp(new Timestamp(System.currentTimeMillis()).getTime());
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

    public List<String> getRoomNumbers() {
        return roomNumbers;
    }

    @Override
    public String toString() {
        return "BookingCanceled{" +
                "reservationNumber='" + reservationNumber + '\'' +
                ", guestFirstName='" + guestFirstName + '\'' +
                ", guestLastName='" + guestLastName + '\'' +
                ", roomNumbers=" + roomNumbers +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
