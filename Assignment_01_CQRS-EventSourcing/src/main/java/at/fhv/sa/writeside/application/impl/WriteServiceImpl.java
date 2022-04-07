package at.fhv.sa.writeside.application.impl;

import at.fhv.sa.eventside.event.BookingCanceled;
import at.fhv.sa.eventside.event.BookingCreated;
import at.fhv.sa.writeside.application.api.WriteService;
import at.fhv.sa.writeside.domain.model.Booking;
import at.fhv.sa.writeside.domain.model.Guest;
import at.fhv.sa.writeside.domain.model.Room;
import at.fhv.sa.writeside.domain.repository.BookingRepository;
import at.fhv.sa.writeside.domain.repository.EventPublisherRepository;
import at.fhv.sa.writeside.domain.repository.GuestRepository;
import at.fhv.sa.writeside.domain.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WriteServiceImpl implements WriteService {
    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    GuestRepository guestRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    EventPublisherRepository eventPublisherRepository;

    @Override
    public String bookRoom(LocalDate startDate, LocalDate endDate, List<String> roomNumbers, String guestFirstName, String guestLastName, int amountOfGuests) {
        List<Room> rooms = new ArrayList<>();

        roomNumbers.forEach(roomNumber -> {
            Room room = roomRepository.roomByNumber(roomNumber).orElseThrow(NoSuchElementException::new);
            rooms.add(room);
        });

        Guest guest = new Guest(UUID.randomUUID(), guestFirstName, guestLastName);
        Booking booking = new Booking(UUID.randomUUID(), startDate, endDate, amountOfGuests, guest, rooms);
        guestRepository.add(guest);
        bookingRepository.add(booking);

        eventPublisherRepository.publishBookingCreatedEvent(
                new BookingCreated(
                        booking.getReservationNumber(),
                        guest.getFirstName(),
                        guest.getLastName(),
                        booking.getStartDate(),
                        booking.getEndDate(),
                        roomNumbers
                )
        );

        return booking.getReservationNumber();
    }

    @Override
    public boolean cancelBooking(String reservationNumber) {
        List<String> roomNumbers = new ArrayList<>();
        Booking booking = bookingRepository.bookingByReservationNumber(reservationNumber).orElseThrow(NoSuchElementException::new);

        booking.getBookedRooms().forEach(room -> {
            roomNumbers.add(room.getRoomNumber());
        });

        bookingRepository.remove(booking);

        eventPublisherRepository.publishBookingCanceledEvent(
                new BookingCanceled(
                        booking.getReservationNumber(),
                        booking.getGuest().getFirstName(),
                        booking.getGuest().getLastName(),
                        roomNumbers
                )
        );

        return true;
    }
}
