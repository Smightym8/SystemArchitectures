package at.fhv.sa.writeside.infrastructure;

import at.fhv.sa.eventside.event.BookingCanceled;
import at.fhv.sa.eventside.event.BookingCreated;
import at.fhv.sa.writeside.domain.repository.EventPublisherRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class EventPublisherRepositoryImpl implements EventPublisherRepository {
    private final WebClient webClient = WebClient.create("http://localhost:8080");

    @Override
    public Boolean publishBookingCreatedEvent(BookingCreated bookingCreated) {
        return webClient
                .post()
                .uri("/bookingcreatedevent")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(bookingCreated), BookingCreated.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    @Override
    public Boolean publishBookingCanceledEvent(BookingCanceled bookingCanceled) {
        return webClient
                .post()
                .uri("/bookingcanceledevent")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(bookingCanceled), BookingCanceled.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
}
