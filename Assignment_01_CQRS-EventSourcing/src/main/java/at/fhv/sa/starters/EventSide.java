package at.fhv.sa.starters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("at.fhv.sa.eventside")
public class EventSide {
    public static void main(String[] args) {
        SpringApplication.run(EventSide.class, args);
    }
}
