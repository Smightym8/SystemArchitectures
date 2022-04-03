package at.fhv.sa.starters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan("at.fhv.sa.writeside")
public class WriteSide {
    public static void main(String[] args) {
        SpringApplication.run(WriteSide.class, args);
    }
}
