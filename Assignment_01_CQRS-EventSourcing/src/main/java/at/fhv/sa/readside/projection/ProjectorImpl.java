package at.fhv.sa.readside.projection;

import at.fhv.sa.eventside.event.BookingCanceled;
import at.fhv.sa.eventside.event.BookingCreated;
import at.fhv.sa.readside.application.dto.BookingDTO;
import at.fhv.sa.readside.infrastructure.impl.BookingDTORepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectorImpl implements Projector {
    @Autowired
    BookingDTORepositoryImpl bookingDTORepository;

    @Override
    public void processBookingCreatedEvent(BookingCreated bookingCreated) {
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

    }
}
