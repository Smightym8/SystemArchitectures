package at.fhv.sa.write.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Booking {
    private UUID id;
    private String reservationNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfGuests;
    private Guest guest;
    private List<Room> bookedRooms;

    public Booking(UUID bookingId, String reservationNumber, LocalDate startDate, LocalDate endDate, int numberOfGuests,
                   Guest guest, List<Room> bookedRooms) {
        this.id = bookingId;
        this.reservationNumber = reservationNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfGuests = numberOfGuests;
        this.guest = guest;
        this.bookedRooms = bookedRooms;
    }

    public UUID getId() {
        return id;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public Guest getGuest() {
        return guest;
    }

    public List<Room> getBookedRooms() {
        return bookedRooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
