package at.fhv.sa.readside.infrastructure.api;

import at.fhv.sa.readside.application.dto.FreeRoomDTO;

import java.time.LocalDate;
import java.util.List;

public interface FreeRoomDTORepository {
    void add(FreeRoomDTO freeRoom);
    void remove(FreeRoomDTO freeRoom);
    List<FreeRoomDTO> byTimePeriodAndCapacity(LocalDate from, LocalDate to, int capacity);
    List<FreeRoomDTO> byRoomNumber(String roomNumber);
}
