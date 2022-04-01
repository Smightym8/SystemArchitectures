package at.fhv.sa.event.infrastructure;

import at.fhv.sa.event.domain.model.Room;
import at.fhv.sa.event.domain.repository.RoomRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RoomRepositoryImpl implements RoomRepository {
    private List<Room> rooms = List.of(
            new Room(UUID.randomUUID(), "1", 1),
            new Room(UUID.randomUUID(), "2", 4),
            new Room(UUID.randomUUID(), "3", 2),
            new Room(UUID.randomUUID(), "4", 6),
            new Room(UUID.randomUUID(), "5", 4),
            new Room(UUID.randomUUID(), "6", 8)
    );

    @Override
    public Optional<Room> roomByNumber(String roomNumber) {
        return rooms.stream().filter(r -> r.getRoomNumber().equals(roomNumber)).findFirst();
    }
}
