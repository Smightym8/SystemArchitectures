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
import java.util.NoSuchElementException;

@Component
public class ProjectorImpl implements Projector {
    @Autowired
    BookingDTORepository bookingDTORepository;

    @Autowired
    FreeRoomDTORepository freeRoomDTORepository;

    @Override
    public void processBookingCreatedEvent(BookingCreated bookingCreated) {
        List<FreeRoomDTO> freeRooms = freeRoomDTORepository.byTimePeriodAndCapacity(
                bookingCreated.getStartDate(), bookingCreated.getEndDate(), 0
        );

        List<FreeRoomDTO> adjustedFreeRooms = new ArrayList<>();

        for(FreeRoomDTO freeRoomDTO : freeRooms) {
            if(bookingCreated.getRoomNumbers().contains(freeRoomDTO.getRoomNumber())) {
                if (!freeRoomDTO.getFrom().isEqual(bookingCreated.getStartDate())) {
                    adjustedFreeRooms.add(
                            FreeRoomDTO.builder()
                                    .withRoomNumber(freeRoomDTO.getRoomNumber())
                                    .withCapacity(freeRoomDTO.getCapacity())
                                    .withFrom(freeRoomDTO.getFrom())
                                    .withTo(bookingCreated.getStartDate().minusDays(1))
                                    .build()
                    );
                }

                if (!freeRoomDTO.getTo().isEqual(bookingCreated.getEndDate())) {
                    adjustedFreeRooms.add(
                            FreeRoomDTO.builder()
                                    .withRoomNumber(freeRoomDTO.getRoomNumber())
                                    .withCapacity(freeRoomDTO.getCapacity())
                                    .withFrom(bookingCreated.getEndDate())
                                    .withTo(freeRoomDTO.getTo())
                                    .build()
                    );
                }

                freeRoomDTORepository.remove(freeRoomDTO);
            }
        }

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
        BookingDTO bookingDTO = bookingDTORepository.getByReservationNumber(bookingCanceled.getReservationNumber()).orElseThrow(
                () -> new NoSuchElementException("Booking with reservation number " + bookingCanceled.getReservationNumber() + " not found")
        );

        bookingDTORepository.remove(bookingDTO);

        // TODO: Adapt FreeRooms
        List<FreeRoomDTO> freeRoomDTOS = new ArrayList<>();
        bookingDTO.getBookedRoomNumbers().forEach(bookedRoomNumber -> {
            List<FreeRoomDTO> freeRoomsTemp = freeRoomDTORepository.byRoomNumber(bookedRoomNumber);
            freeRoomDTOS.addAll(freeRoomsTemp);
        });

        List<FreeRoomDTO> adjustedRooms = new ArrayList<>();
        List<FreeRoomDTO> roomEntriesToChange = new ArrayList<>();
        List<FreeRoomDTO> roomEntriesToRemove = new ArrayList<>();
        for(FreeRoomDTO freeRoomDTO : freeRoomDTOS) {
            if(freeRoomDTO.getTo().isEqual(bookingDTO.getStartDate().minusDays(1))) {
                roomEntriesToChange.add(freeRoomDTO);
            }

            if(freeRoomDTO.getFrom().isEqual(bookingDTO.getEndDate())) {
                roomEntriesToRemove.add(freeRoomDTO);
            }
        }

        for(int i = 0; i < roomEntriesToChange.size(); i++) {
            FreeRoomDTO currentRoomToChange = roomEntriesToChange.get(i);
            FreeRoomDTO matchingRoomToRemove = roomEntriesToRemove.get(i);

            adjustedRooms.add(
                    FreeRoomDTO.builder()
                            .withRoomNumber(currentRoomToChange.getRoomNumber())
                            .withFrom(currentRoomToChange.getFrom())
                            .withTo(matchingRoomToRemove.getTo())
                            .withCapacity(currentRoomToChange.getCapacity())
                            .build()
            );

            // Remove old roomEntry
            roomEntriesToRemove.add(currentRoomToChange);
        }

        roomEntriesToRemove.forEach(room -> freeRoomDTORepository.remove(room));
        adjustedRooms.forEach(room -> freeRoomDTORepository.add(room));
    }
}
