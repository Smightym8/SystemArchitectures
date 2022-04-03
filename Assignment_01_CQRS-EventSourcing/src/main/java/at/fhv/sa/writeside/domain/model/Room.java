package at.fhv.sa.writeside.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Room {
    private UUID id;
    private String roomNumber;
    private int capacity;
    private boolean isBooked;

    public Room(UUID roomId, String roomNumber, int capacity) {
        this.id = roomId;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.isBooked = false;
    }

    public void book() {
        this.isBooked = true;
    }

    public void free() {
        this.isBooked = false;
    }

    public UUID getRoomId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isBooked() {
        return isBooked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
