package at.fhv.sa.eventside.infrastructure;

import at.fhv.sa.eventside.event.BookingCanceled;
import at.fhv.sa.eventside.event.BookingCreated;
import at.fhv.sa.eventside.event.Event;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class EventSubscriber {
    private final WebClient webClient;
    private final String bookingCreatedEndpoint;
    private final String bookingCanceledEndpoint;

    public EventSubscriber(String url, String bookingCreatedEndpoint, String bookingCanceledEndpoint) {
        webClient = WebClient.create(url);
        this.bookingCreatedEndpoint = bookingCreatedEndpoint;
        this.bookingCanceledEndpoint = bookingCanceledEndpoint;
    }

    public void getNotified(Event event) {
        String endpoint = null;

        if(event instanceof BookingCreated) {
            endpoint = bookingCreatedEndpoint;
        } else if(event instanceof BookingCanceled) {
            endpoint = bookingCanceledEndpoint;
        }

        if(endpoint != null) {
            webClient
                .post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(event), Event.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        }
    }
}
