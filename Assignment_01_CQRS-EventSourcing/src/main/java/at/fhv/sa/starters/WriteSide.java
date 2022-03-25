package at.fhv.sa.starters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("at.fhv.sa.write")
public class WriteSide {
    public static void main(String[] args) {
        SpringApplication.run(WriteSide.class, args);
    }
}
