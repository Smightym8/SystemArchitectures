package at.fhv.sa.readside.projection;

import at.fhv.sa.eventside.event.BookingCanceled;
import at.fhv.sa.eventside.event.BookingCreated;
import at.fhv.sa.readside.application.dto.BookingDTO;
import at.fhv.sa.readside.application.dto.FreeRoomDTO;
import at.fhv.sa.readside.infrastructure.api.BookingDTORepository;
import at.fhv.sa.readside.infrastructure.api.FreeRoomDTORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectorImpl implements Projector {
    @Autowired
    BookingDTORepository bookingDTORepository;

    @Autowired
    FreeRoomDTORepository freeRoomDTORepository;

    @Override
    public void processBookingCreatedEvent(BookingCreated bookingCreated) {
        List<FreeRoomDTO> bookedRooms = new ArrayList<>();
        bookingCreated.getRoomNumbers().forEach(
                // TODO: Get rooms by free status in booked period
                roomNumber -> bookedRooms.add(freeRoomDTORepository.byRoomNumber(roomNumber))
        );

        List<FreeRoomDTO> adjustedFreeRooms = new ArrayList<>();

        for(FreeRoomDTO freeRoomDTO : bookedRooms) {
           if(!freeRoomDTO.getFrom().isEqual(bookingCreated.getStartDate())) {
               adjustedFreeRooms.add(
                       FreeRoomDTO.builder()
                               .withRoomNumber(freeRoomDTO.getRoomNumber())
                               .withCapacity(freeRoomDTO.getCapacity())
                               .withFrom(freeRoomDTO.getFrom())
                               .withTo(bookingCreated.getStartDate().minusDays(1))
                               .build()
               );
           }

           if(!freeRoomDTO.getTo().isEqual(bookingCreated.getEndDate())) {
               adjustedFreeRooms.add(
                       FreeRoomDTO.builder()
                               .withRoomNumber(freeRoomDTO.getRoomNumber())
                               .withCapacity(freeRoomDTO.getCapacity())
                               .withFrom(bookingCreated.getEndDate().plusDays(1))
                               .withTo(freeRoomDTO.getTo())
                               .build()
               );
           }
        }

        bookedRooms.forEach(bookedRoom -> freeRoomDTORepository.remove(bookedRoom));
        adjustedFreeRooms.forEach(freeRoom -> freeRoomDTORepository.add(freeRoom));

        BookingDTO bookingDTO = BookingDTO.builder()
                .withReservationNumber(bookingCreated.getReservationNumber())
                .withGuestFirstName(bookingCreated.getGuestFirstName())
                .withGuestLastName(bookingCreated.getGuestLastName())
                .withStartDate(bookingCreated.getStartDate())
                .withEndDate(bookingCreated.getEndDate())
                .withBookedRoomNumbers(bookingCreated.getRoomNumbers())
                .build();

        bookingDTORepository.add(bookingDTO);
    }

    @Override
    public void processBookingCanceledEvent(BookingCanceled bookingCanceled) {
        // TODO: Implement
    }
}
