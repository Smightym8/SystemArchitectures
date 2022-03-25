package at.fhv.sa.write.domain;

import java.util.Objects;
import java.util.UUID;

public class Room {
    private UUID id;
    private String roomNumber;
    private int capacity;

    public Room(UUID roomId, String roomNumber, int capacity) {
        this.id = roomId;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
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
