package at.fhv.sa.readside;

import at.fhv.sa.eventside.event.BookingCanceled;
import at.fhv.sa.eventside.event.BookingCreated;
import at.fhv.sa.readside.application.dto.BookingDTO;
import at.fhv.sa.readside.application.dto.FreeRoomDTO;
import at.fhv.sa.readside.infrastructure.api.BookingDTORepository;
import at.fhv.sa.readside.infrastructure.api.FreeRoomDTORepository;
import at.fhv.sa.readside.infrastructure.impl.BookingDTORepositoryImpl;
import at.fhv.sa.readside.infrastructure.impl.FreeRoomDTORepositoryImpl;
import at.fhv.sa.readside.projection.Projector;
import at.fhv.sa.readside.projection.ProjectorImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ProjectorImpl.class, BookingDTORepositoryImpl.class, FreeRoomDTORepositoryImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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

    @Test
    void given_bookingInformation_when_getFreeRoomsByTimePeriod_then_bookedRooms_notIncluded() {
        // given
        LocalDate startDateExpected = LocalDate.parse("10-05-2022", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate endDateExpected = LocalDate.parse("20-05-2022", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        List<String> roomNumbersExpected = List.of("1", "2");
        String guestFirstNameExpected = "Michael";
        String guestLastNameExpected = "Spiegel";
        String reservationNumberExpected = "1";

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

        List<FreeRoomDTO> freeRooms = freeRoomDTORepository.byTimePeriodAndCapacity(startDateExpected, endDateExpected, 0);
        List<String> freeRoomNumbers = freeRooms.stream().map(FreeRoomDTO::getRoomNumber).collect(Collectors.toList());

        // then
        assertFalse(freeRoomNumbers.contains(roomNumbersExpected.get(0)));
        assertFalse(freeRoomNumbers.contains(roomNumbersExpected.get(1)));
    }

    @Test
    void given_booking_when_cancelBooking_then_bookingIsNotReturned() {
        LocalDate startDateExpected = LocalDate.parse("10-05-2022", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate endDateExpected = LocalDate.parse("20-05-2022", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        List<String> roomNumbersExpected = List.of("1", "2");
        String guestFirstNameExpected = "Michael";
        String guestLastNameExpected = "Spiegel";
        String reservationNumberExpected = "1";

        BookingCreated bookingCreated = new BookingCreated(
                reservationNumberExpected,
                guestFirstNameExpected,
                guestLastNameExpected,
                startDateExpected,
                endDateExpected,
                roomNumbersExpected
        );
        projector.processBookingCreatedEvent(bookingCreated);

        BookingCanceled bookingCanceled = new BookingCanceled(
                reservationNumberExpected,
                guestFirstNameExpected,
                guestLastNameExpected,
                roomNumbersExpected
        );

        // when
        projector.processBookingCanceledEvent(bookingCanceled);

        // then
        Optional<BookingDTO> bookingDTOOptional = bookingDTORepository.getByReservationNumber(reservationNumberExpected);
        assertFalse(bookingDTOOptional.isPresent());
    }

    @Test
    void given_booking_when_bookingCanceled_then_roomsAreFree() {
        LocalDate startDateExpected = LocalDate.parse("10-05-2022", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate endDateExpected = LocalDate.parse("20-05-2022", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        List<String> roomNumbersExpected = List.of("1", "2");
        String guestFirstNameExpected = "Michael";
        String guestLastNameExpected = "Spiegel";
        String reservationNumberExpected = "1";

        BookingCreated bookingCreated = new BookingCreated(
                reservationNumberExpected,
                guestFirstNameExpected,
                guestLastNameExpected,
                startDateExpected,
                endDateExpected,
                roomNumbersExpected
        );
        projector.processBookingCreatedEvent(bookingCreated);

        BookingCanceled bookingCanceled = new BookingCanceled(
                reservationNumberExpected,
                guestFirstNameExpected,
                guestLastNameExpected,
                roomNumbersExpected
        );

        // when
        projector.processBookingCanceledEvent(bookingCanceled);

        // then
        List<FreeRoomDTO> freeRooms = freeRoomDTORepository.byTimePeriodAndCapacity(startDateExpected, endDateExpected, 0);
        List<String> freeRoomNumbers = freeRooms.stream().map(FreeRoomDTO::getRoomNumber).collect(Collectors.toList());

        assertTrue(freeRoomNumbers.contains(roomNumbersExpected.get(0)));
        assertTrue(freeRoomNumbers.contains(roomNumbersExpected.get(1)));
    }
}