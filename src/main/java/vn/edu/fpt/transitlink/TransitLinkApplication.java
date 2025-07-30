package vn.edu.fpt.transitlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import vn.edu.fpt.transitlink.shared.config.AppProperties;
import vn.edu.fpt.transitlink.shared.config.SpringDocProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        AppProperties.class,
        SpringDocProperties.class
})
public class TransitLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransitLinkApplication.class, args);
    }

}
