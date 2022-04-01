package at.fhv.sa.event.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Guest {
    private UUID id;
    private String firstName;
    private String lastName;

    public Guest(UUID guestId, String firstName, String lastName) {
        this.id = guestId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guest guest = (Guest) o;
        return Objects.equals(id, guest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
