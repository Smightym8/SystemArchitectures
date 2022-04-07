package at.fhv.sa.writeside.unit.application;

import at.fhv.sa.eventside.event.BookingCreated;
import at.fhv.sa.writeside.application.api.WriteService;
import at.fhv.sa.writeside.domain.model.Booking;
import at.fhv.sa.writeside.domain.model.Guest;
import at.fhv.sa.writeside.domain.model.Room;
import at.fhv.sa.writeside.domain.repository.BookingRepository;
import at.fhv.sa.writeside.domain.repository.EventPublisherRepository;
import at.fhv.sa.writeside.domain.repository.GuestRepository;
import at.fhv.sa.writeside.domain.repository.RoomRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class WriteServiceTests {
    @Autowired
    WriteService writeService;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    GuestRepository guestRepository;

    @MockBean
    RoomRepository roomRepository;

    @MockBean
    EventPublisherRepository eventPublisherRepository;

    @Test
    void given_booking_information_when_bookRoom_then_return_all_reservation_number() {
        // given
        UUID roomIdExpected = UUID.randomUUID();
        String roomIdExpectedStr = String.valueOf(roomIdExpected);
        String roomNumberExpected = "1";
        int capacityExpected = 2;

        Room roomExpected = new Room(roomIdExpected, roomNumberExpected, capacityExpected);

        UUID guestIdExpected = UUID.randomUUID();
        String guestIdExpectedStr = String.valueOf(guestIdExpected);
        String guestFirstNameExpected = "Rainer";
        String guestLastNameExpected = "Zufall";

        Guest guestExpected = new Guest(guestIdExpected, guestFirstNameExpected, guestLastNameExpected);

        String reservationNumberExpected = "1";
        LocalDate startDateExpected = LocalDate.of(2022, 5, 1);
        LocalDate endDateExpected = LocalDate.of(2022, 5, 10);
        int numberOfGuestsExpected = 2;

        UUID bookingIdExpected = UUID.randomUUID();
        String bookingIdExpectedStr = String.valueOf(bookingIdExpected);
        Booking bookingExpected = new Booking(
                bookingIdExpected,
                startDateExpected,
                endDateExpected,
                numberOfGuestsExpected,
                guestExpected,
                List.of(roomExpected)
        );

        BookingCreated bookingCreatedEventExpected = new BookingCreated(
                reservationNumberExpected,
                guestFirstNameExpected,
                guestLastNameExpected,
                startDateExpected,
                endDateExpected,
                List.of(roomNumberExpected)
        );

        Mockito.when(roomRepository.roomByNumber(roomNumberExpected)).thenReturn(Optional.of(roomExpected));
        Mockito.doNothing().when(guestRepository).add(guestExpected);
        Mockito.doNothing().when(bookingRepository).add(bookingExpected);
        Mockito.doNothing().when(eventPublisherRepository).publishBookingCreatedEvent(bookingCreatedEventExpected);

        // when
        String reservationNumberActual = writeService.bookRoom(
                startDateExpected,
                endDateExpected,
                List.of(roomNumberExpected),
                guestFirstNameExpected,
                guestLastNameExpected,
                numberOfGuestsExpected
        );

        // then
        Assertions.assertEquals(reservationNumberExpected, reservationNumberActual);

    }
}
