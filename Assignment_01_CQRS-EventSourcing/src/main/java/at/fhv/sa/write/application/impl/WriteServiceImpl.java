package at.fhv.sa.write.application.impl;

import at.fhv.sa.write.application.api.WriteService;
import at.fhv.sa.write.domain.Guest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class WriteServiceImpl implements WriteService {
    @Override
    public void bookRoom(LocalDate startDate, LocalDate endDate, String roomNumber, Guest guest) {

    }

    @Override
    public void cancelBooking(String reservationNumber) {

    }
}
