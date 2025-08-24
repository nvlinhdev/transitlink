package vn.edu.fpt.transitlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import vn.edu.fpt.transitlink.mail_sender.config.MailProperties;
import vn.edu.fpt.transitlink.shared.config.AppProperties;
import vn.edu.fpt.transitlink.shared.config.SpringDocProperties;

@EnableConfigurationProperties({
        AppProperties.class,
        SpringDocProperties.class,
        MailProperties.class
})
@SpringBootApplication
@EnableAsync
@EnableCaching
public class TransitLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransitLinkApplication.class, args);
    }

}
