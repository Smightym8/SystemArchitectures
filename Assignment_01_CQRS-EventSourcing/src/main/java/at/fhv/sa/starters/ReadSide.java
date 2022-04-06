package at.fhv.sa.starters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("at.fhv.sa.readside")
public class ReadSide {
    public static void main(String[] args) {
        SpringApplication.run(ReadSide.class, args);
    }
}
