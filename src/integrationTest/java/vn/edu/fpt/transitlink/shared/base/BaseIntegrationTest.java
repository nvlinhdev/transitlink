package vn.edu.fpt.transitlink.shared.base;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DirtiesContext
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    @LocalServerPort
    protected static int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Container
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @Container
    public static final RedisContainer redis = new RedisContainer("redis:8.0.3")
            .withExposedPorts(6379)
            .withReuse(true);

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        // Đảm bảo containers được start trước
        if (!postgres.isRunning()) {
            postgres.start();
        }
        if (!redis.isRunning()) {
            redis.start();
        }

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getFirstMappedPort().toString());

        registry.add("app.storage.provider", () -> "local");
        registry.add("app.storage.root-path", () -> "./test-data");
        registry.add("app.storage.local.url", () -> "http://localhost:" + port + "/files");
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    void setUp() {
        // Common setup logic
    }

    @AfterEach
    void tearDown() {
        // Common cleanup logic
    }
}