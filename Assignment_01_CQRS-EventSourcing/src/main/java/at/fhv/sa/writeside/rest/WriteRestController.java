package at.fhv.sa.writeside.rest;

import at.fhv.sa.writeside.application.api.WriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

// http://localhost:8081/swagger-ui/index.html
@RestController
public class WriteRestController {
    @Autowired
    WriteService writeService;

    @PostMapping(value = "/bookroom")
    public String bookRoomRest(@RequestParam String startDate, @RequestParam String endDate,
                                @RequestParam String[] roomNumbers, @RequestParam String guestFirstName,
                                @RequestParam String guestLastName, @RequestParam int amountOfGuests) {

        return writeService.bookRoom(
                LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                Arrays.stream(roomNumbers).collect(Collectors.toList()),
                guestFirstName,
                guestLastName,
                amountOfGuests
        );
    }

    @PostMapping(value = "/cancelbooking")
    public boolean cancelBookingRest(String reservationNumber) {
        return writeService.cancelBooking(reservationNumber);
    }
}