package at.fhv.sa.eventside.event;

import java.sql.Timestamp;
import java.util.List;

public class BookingCanceled extends Event {
    private final String reservationNumber;
    private String guestFirsName;
    private String guestLastName;
    private List<String> roomNumbers;

    public BookingCanceled(String reservationNumber, String guestFirsName, String guestLastName, List<String> roomNumbers) {
        this.reservationNumber = reservationNumber;
        this.guestFirsName = guestFirsName;
        this.guestLastName = guestLastName;
        this.roomNumbers = roomNumbers;
        setTimestamp(new Timestamp(System.currentTimeMillis()).getTime());
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public String getGuestFirsName() {
        return guestFirsName;
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
                ", guestFirsName='" + guestFirsName + '\'' +
                ", guestLastName='" + guestLastName + '\'' +
                ", roomNumbers=" + roomNumbers +
                '}';
    }
}
