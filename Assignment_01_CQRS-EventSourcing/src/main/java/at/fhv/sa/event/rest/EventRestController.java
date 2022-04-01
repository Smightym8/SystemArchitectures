package at.fhv.sa.event.rest;

import at.fhv.sa.event.application.api.WriteService;
import at.fhv.sa.event.domain.model.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class EventRestController {
    @Autowired
    WriteService writeService;

    @PostMapping(value = "/bookroom", consumes = "application/json", produces = "application/json")
    public boolean bookRoomRest(String startDate, String endDate,
                                List<String> roomNumbers, Guest guest, int amountOfGuests) {
        writeService.bookRoom(
                LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                roomNumbers,
                guest,
                amountOfGuests
        );

        return true;
    }
}
