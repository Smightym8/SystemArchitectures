package at.fhv.sa.readside.application.dto;

import java.time.LocalDate;
import java.util.List;

public class BookingDTO {
    private String reservationNumber;
    private String guestFirstName;
    private String guestLastName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> bookedRoomNumbers;

    private BookingDTO() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final BookingDTO instance;

        public Builder() {
            this.instance = new BookingDTO();
        }

        public Builder withReservationNumber(String reservationNUmber) {
            this.instance.reservationNumber = reservationNUmber;
            return this;
        }

        public Builder withGuestFirstName(String guestFirstName) {
            this.instance.guestFirstName = guestFirstName;
            return this;
        }

        public Builder withGuestLastName(String guestLastName) {
            this.instance.guestLastName = guestLastName;
            return this;
        }

        public Builder withStartDate(LocalDate fromDate) {
            this.instance.startDate = fromDate;
            return this;
        }

        public Builder withEndDate(LocalDate toDate) {
            this.instance.endDate = toDate;
            return this;
        }

        public Builder withBookedRoomNumbers(List<String> bookedRoomNumbers) {
            this.instance.bookedRoomNumbers = bookedRoomNumbers;
            return this;
        }

        public BookingDTO build() {
            return this.instance;
        }
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

    public List<String> getBookedRoomNumbers() {
        return bookedRoomNumbers;
    }
}
