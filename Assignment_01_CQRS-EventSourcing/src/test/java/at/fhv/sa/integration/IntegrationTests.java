package at.fhv.sa.integration;

import at.fhv.sa.eventside.event.BookingCreated;
import at.fhv.sa.readside.application.dto.BookingDTO;
import at.fhv.sa.readside.infrastructure.api.BookingDTORepository;
import at.fhv.sa.readside.infrastructure.api.FreeRoomDTORepository;
import at.fhv.sa.readside.projection.Projector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {Projector.class, BookingDTORepository.class, FreeRoomDTORepository.class})
public class IntegrationTests {
    @Autowired
    Projector projector;

    @Autowired
    BookingDTORepository bookingDTORepository;

    @Autowired
    FreeRoomDTORepository freeRoomDTORepository;

    @Test
    void given_bookingInformation_when_bookingCreated_and_getBookingsByTimePeriod_then_equalsBookingIsReturned() {
        // given
        LocalDate startDateExpected = LocalDate.parse("10-05-2022", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate endDateExpected = LocalDate.parse("20-05-2022", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        List<String> roomNumbersExpected = List.of("1", "2");
        String guestFirstNameExpected = "Michael";
        String guestLastNameExpected = "Spiegel";
        String reservationNumberExpected = "1";
        int bookingsExpected = 1;

        BookingCreated event = new BookingCreated(
                reservationNumberExpected,
                guestFirstNameExpected,
                guestLastNameExpected,
                startDateExpected,
                endDateExpected,
                roomNumbersExpected
        );

        // when
        projector.processBookingCreatedEvent(event);

       List<BookingDTO> bookings = bookingDTORepository.getByTimePeriod(startDateExpected, endDateExpected);

        // then
        assertEquals(bookingsExpected, bookings.size());

        BookingDTO bookingDTO = bookings.get(0);
        assertEquals(reservationNumberExpected, bookingDTO.getReservationNumber());
        assertEquals(startDateExpected, bookingDTO.getStartDate());
        assertEquals(endDateExpected, bookingDTO.getEndDate());
        assertEquals(guestFirstNameExpected, bookingDTO.getGuestFirstName());
        assertEquals(guestLastNameExpected, bookingDTO.getGuestLastName());
        assertEquals(roomNumbersExpected.size(), bookingDTO.getBookedRoomNumbers().size());

        for(String roomNumber : roomNumbersExpected) {
            assertTrue(bookingDTO.getBookedRoomNumbers().contains(roomNumber));
        }
    }
}