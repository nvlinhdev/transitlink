package vn.edu.fpt.transitlink;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import vn.edu.fpt.transitlink.shared.config.TestContainerConfig;

@SpringBootTest
@Testcontainers // Cho phép Testcontainers quản lý lifecycle container
class TransitLinkApplicationTests extends TestContainerConfig {

    @Test
    void contextLoads() {
        System.out.println("Redis URL: " + redis.getHost() + ":" + redis.getMappedPort(6379));
    }
}
