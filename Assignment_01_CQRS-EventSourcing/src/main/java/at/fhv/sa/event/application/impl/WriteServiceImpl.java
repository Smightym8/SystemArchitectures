package at.fhv.sa.event.application.impl;

import at.fhv.sa.event.EventRepository;
import at.fhv.sa.event.domain.events.BookingCanceled;
import at.fhv.sa.event.domain.events.BookingCreated;
import at.fhv.sa.event.application.api.WriteService;
import at.fhv.sa.event.domain.model.Booking;
import at.fhv.sa.event.domain.model.Guest;
import at.fhv.sa.event.domain.model.Room;
import at.fhv.sa.event.domain.repository.BookingRepository;
import at.fhv.sa.event.domain.repository.GuestRepository;
import at.fhv.sa.event.domain.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WriteServiceImpl implements WriteService {
    @Autowired
    EventRepository eventRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    GuestRepository guestRepository;

    @Autowired
    RoomRepository roomRepository;

    @Override
    public String bookRoom(LocalDate startDate, LocalDate endDate, List<String> roomNumbers, Guest guest, int amountOfGuests) {
        List<Room> rooms = new ArrayList<>();
        roomNumbers.forEach(roomNumber -> {
            Room room = roomRepository.roomByNumber(roomNumber).orElseThrow(NoSuchElementException::new);
            rooms.add(room);
            room.book();
        });

        Booking booking = new Booking(UUID.randomUUID(), startDate, endDate, amountOfGuests, guest, rooms);
        guestRepository.add(guest);
        bookingRepository.add(booking);

        eventRepository.processEvent(
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
        Booking booking = bookingRepository.bookingByReservationNumber(reservationNumber).orElseThrow(NoSuchElementException::new);
        booking.getBookedRooms().forEach(Room::free);
        bookingRepository.remove(booking);

        eventRepository.processEvent(
                new BookingCanceled(
                        booking.getReservationNumber(),
                        booking.getGuest().getFirstName(),
                        booking.getGuest().getLastName(),
                        booking.getBookedRooms().stream().map(Room::getRoomNumber).collect(Collectors.toList())
                )
        );

        return true;
    }
}
