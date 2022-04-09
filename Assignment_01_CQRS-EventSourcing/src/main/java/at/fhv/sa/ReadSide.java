package at.fhv.sa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan("at.fhv.sa.readside")
public class ReadSide {
    public static void main(String[] args) {
        SpringApplication.run(ReadSide.class, args);
    }
}
