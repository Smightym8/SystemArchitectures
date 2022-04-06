package at.fhv.sa.readside.application.dto;

import java.time.LocalDate;

public class FreeRoomDTO {
    private String roomNumber;
    private int capacity;
    private LocalDate from;
    private LocalDate to;

    private FreeRoomDTO() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final FreeRoomDTO instance;

        public Builder() {
            this.instance = new FreeRoomDTO();
        }

        public Builder withRoomNumber(String roomNumber) {
            this.instance.roomNumber = roomNumber;
            return this;
        }

        public Builder withCapacity(int capacity) {
            this.instance.capacity = capacity;
            return this;
        }

        public Builder withFrom(LocalDate from) {
            this.instance.from = from;
            return this;
        }

        public Builder withTo(LocalDate to) {
            this.instance.to = to;
            return this;
        }

        public FreeRoomDTO build() {
            return this.instance;
        }
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }
}