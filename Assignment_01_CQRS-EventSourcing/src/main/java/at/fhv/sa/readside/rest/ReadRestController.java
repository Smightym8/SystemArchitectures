package at.fhv.sa.readside.rest;

import at.fhv.sa.eventside.event.BookingCanceled;
import at.fhv.sa.eventside.event.BookingCreated;
import at.fhv.sa.readside.application.dto.BookingDTO;
import at.fhv.sa.readside.application.dto.FreeRoomDTO;
import at.fhv.sa.readside.infrastructure.api.BookingDTORepository;
import at.fhv.sa.readside.infrastructure.api.FreeRoomDTORepository;
import at.fhv.sa.readside.projection.Projector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class ReadRestController {
    @Autowired
    Projector projector;

    @Autowired
    BookingDTORepository bookingDTORepository;

    @Autowired
    FreeRoomDTORepository freeRoomDTORepository;

    @PostMapping(value = "/bookingcreatedevent", consumes = "application/json", produces = "application/json")
    public boolean bookingCreatedRest(@RequestBody BookingCreated bookingCreated) {
        projector.processBookingCreatedEvent(bookingCreated);
        return true;
    }

    @PostMapping(value = "/bookingcanceledevent", consumes = "application/json", produces = "application/json")
    public boolean bookingCanceledRest(@RequestBody BookingCanceled bookingCanceled) {
        projector.processBookingCanceledEvent(bookingCanceled);
        return true;
    }

    @GetMapping(value = "/bookings")
    public List<BookingDTO> getBookingsRest(@RequestParam String startDate, @RequestParam String endDate) {
        return bookingDTORepository.getByTimePeriod(
                LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        );
    }

    @GetMapping(value = "/freerooms")
    public List<FreeRoomDTO> getFreeRoomsRest(@RequestParam String startDate, @RequestParam String endDate, @RequestParam int capacity) {
        return freeRoomDTORepository.byTimePeriodAndCapacity(
                LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                capacity
        );
    }
}
