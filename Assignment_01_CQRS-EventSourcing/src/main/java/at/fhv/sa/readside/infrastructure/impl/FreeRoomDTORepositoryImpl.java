package at.fhv.sa.readside.infrastructure.impl;

import at.fhv.sa.readside.application.dto.FreeRoomDTO;
import at.fhv.sa.readside.infrastructure.api.FreeRoomDTORepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
public class FreeRoomDTORepositoryImpl implements FreeRoomDTORepository {
    private final List<FreeRoomDTO> freeRooms = new ArrayList<>();

    public FreeRoomDTORepositoryImpl() {
        freeRooms.add(FreeRoomDTO.builder().withRoomNumber("1").withCapacity(1).withFrom(LocalDate.MIN).withTo(LocalDate.MAX).build());
        freeRooms.add(FreeRoomDTO.builder().withRoomNumber("2").withCapacity(4).withFrom(LocalDate.MIN).withTo(LocalDate.MAX).build());
        freeRooms.add(FreeRoomDTO.builder().withRoomNumber("3").withCapacity(2).withFrom(LocalDate.MIN).withTo(LocalDate.MAX).build());
        freeRooms.add(FreeRoomDTO.builder().withRoomNumber("4").withCapacity(6).withFrom(LocalDate.MIN).withTo(LocalDate.MAX).build());
        freeRooms.add(FreeRoomDTO.builder().withRoomNumber("5").withCapacity(4).withFrom(LocalDate.MIN).withTo(LocalDate.MAX).build());
        freeRooms.add(FreeRoomDTO.builder().withRoomNumber("6").withCapacity(8).withFrom(LocalDate.MIN).withTo(LocalDate.MAX).build());
        freeRooms.add(FreeRoomDTO.builder().withRoomNumber("7").withCapacity(5).withFrom(LocalDate.MIN).withTo(LocalDate.MAX).build());
        freeRooms.add(FreeRoomDTO.builder().withRoomNumber("8").withCapacity(2).withFrom(LocalDate.MIN).withTo(LocalDate.MAX).build());
        freeRooms.add(FreeRoomDTO.builder().withRoomNumber("9").withCapacity(1).withFrom(LocalDate.MIN).withTo(LocalDate.MAX).build());
        freeRooms.add(FreeRoomDTO.builder().withRoomNumber("10").withCapacity(4).withFrom(LocalDate.MIN).withTo(LocalDate.MAX).build());
    }

    @Override
    public void add(FreeRoomDTO freeRoom) {
        freeRooms.add(freeRoom);
    }

    @Override
    public void remove(FreeRoomDTO freeRoom) {
        freeRooms.remove(freeRoom);
    }

    @Override
    public List<FreeRoomDTO> byTimePeriodAndCapacity(LocalDate from, LocalDate to, int capacity) {
        return freeRooms.stream()
                .filter(freeRoom -> freeRoom.getCapacity() >= capacity)
                .filter(freeRoom -> freeRoom.getFrom().isBefore(from))
                .filter(freeRoom -> freeRoom.getTo().isAfter(to))
                .collect(Collectors.toList());
    }

    @Override
    public FreeRoomDTO byRoomNumber(String roomNumber) {
        return freeRooms.stream().filter(freeRoom -> freeRoom.getRoomNumber().equals(roomNumber)).findFirst().orElseThrow(
                () -> new NoSuchElementException("Room with room number " + roomNumber + " not found")
        );
    }
}
