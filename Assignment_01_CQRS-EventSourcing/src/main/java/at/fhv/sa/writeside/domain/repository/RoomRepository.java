package at.fhv.sa.writeside.domain.repository;

import at.fhv.sa.writeside.domain.model.Room;

import java.util.Optional;

public interface RoomRepository {
    Optional<Room> roomByNumber(String roomNumber);
}
