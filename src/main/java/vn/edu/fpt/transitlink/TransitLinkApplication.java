package vn.edu.fpt.transitlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

@Modulith
@SpringBootApplication
public class TransitLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransitLinkApplication.class, args);
    }

}
