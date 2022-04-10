package at.fhv.sa.eventside.event;

import java.sql.Timestamp;

public class BookingCanceled extends Event {
    private final String reservationNumber;

    public BookingCanceled(String reservationNumber) {
        this.reservationNumber = reservationNumber;

        setTimestamp(new Timestamp(System.currentTimeMillis()).getTime());
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    @Override
    public String toString() {
        return "BookingCanceled{" +
                "reservationNumber='" + reservationNumber + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
