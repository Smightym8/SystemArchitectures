package at.fhv.sa.event.domain.repository;

import at.fhv.sa.event.domain.model.Room;

import java.util.Optional;

public interface RoomRepository {
    Optional<Room> roomByNumber(String roomNumber);
}
