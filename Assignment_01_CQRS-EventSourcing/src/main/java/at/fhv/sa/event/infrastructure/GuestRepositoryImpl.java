package at.fhv.sa.event.infrastructure;

import at.fhv.sa.event.domain.model.Guest;
import at.fhv.sa.event.domain.repository.GuestRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GuestRepositoryImpl implements GuestRepository {
    private List<Guest> guests = new ArrayList<>();

    @Override
    public void add(Guest guest) {
        guests.add(guest);
    }
}
