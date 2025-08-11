package vn.edu.fpt.transitlink.shared.config;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.wait.strategy.Wait;

@Testcontainers
public abstract class TestContainerConfig {

    protected static final String REDIS_PASSWORD = "your_redis_password";
    protected static final String KEYCLOAK_DB_NAME = "keycloak_db";
    protected static final String KEYCLOAK_DB_USER = "keycloak_user";
    protected static final String KEYCLOAK_DB_PASSWORD = "keycloak_pass";
    protected static final String TRANSITLINK_DB_NAME = "transitlink_db";
    protected static final String TRANSITLINK_DB_USER = "transitlink_user";
    protected static final String TRANSITLINK_DB_PASSWORD = "transitlink_pass";
    protected static final String KEYCLOAK_ADMIN_USER = "admin";
    protected static final String KEYCLOAK_ADMIN_PASSWORD = "admin123";

    protected static final Network network = Network.newNetwork();

    @Container
    protected static final GenericContainer<?> redis = new GenericContainer<>(
            DockerImageName.parse("bitnami/redis:8.0.3-debian-12-r2"))
            .withNetwork(network)
            .withNetworkAliases("redis")
            .withEnv("REDIS_PASSWORD", REDIS_PASSWORD)
            .withExposedPorts(6379)
            .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1));

    @Container
    protected static final GenericContainer<?> keycloakDb = new GenericContainer<>(
            DockerImageName.parse("bitnami/postgresql:17.5.0-debian-12-r20"))
            .withNetwork(network)
            .withNetworkAliases("keycloak-db")
            .withEnv("POSTGRESQL_DATABASE", KEYCLOAK_DB_NAME)
            .withEnv("POSTGRESQL_USERNAME", KEYCLOAK_DB_USER)
            .withEnv("POSTGRESQL_PASSWORD", KEYCLOAK_DB_PASSWORD)
            .withExposedPorts(5432)
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 1));

    @Container
    protected static final GenericContainer<?> transitlinkDb = new GenericContainer<>(
            DockerImageName.parse("bitnami/postgresql:17.5.0-debian-12-r20"))
            .withNetwork(network)
            .withNetworkAliases("transitlink-db")
            .withEnv("POSTGRESQL_DATABASE", TRANSITLINK_DB_NAME)
            .withEnv("POSTGRESQL_USERNAME", TRANSITLINK_DB_USER)
            .withEnv("POSTGRESQL_PASSWORD", TRANSITLINK_DB_PASSWORD)
            .withExposedPorts(5432)
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 1));

    @Container
    protected static final GenericContainer<?> keycloak = new GenericContainer<>(
            DockerImageName.parse("bitnami/keycloak:26.3.1-debian-12-r2"))
            .withNetwork(network)
            .withNetworkAliases("keycloak")
            .withEnv("KEYCLOAK_DATABASE_HOST", "keycloak-db")
            .withEnv("KEYCLOAK_DATABASE_NAME", KEYCLOAK_DB_NAME)
            .withEnv("KEYCLOAK_DATABASE_USER", KEYCLOAK_DB_USER)
            .withEnv("KEYCLOAK_DATABASE_PASSWORD", KEYCLOAK_DB_PASSWORD)
            .withEnv("KEYCLOAK_ADMIN", KEYCLOAK_ADMIN_USER)
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", KEYCLOAK_ADMIN_PASSWORD)
            .withEnv("KEYCLOAK_ENABLE_HEALTH_ENDPOINTS", "true")
            .withEnv("KEYCLOAK_EXTRA_ARGS", "--import-realm")
            .withExposedPorts(9000)
            .waitingFor(Wait.forHttp("/health/ready").forPort(9000));

    @BeforeAll
    static void printInfo() {
        System.out.println("Redis running at " + redis.getHost() + ":" + redis.getMappedPort(6379));
        System.out.println("Keycloak DB running at " + keycloakDb.getHost() + ":" + keycloakDb.getMappedPort(5432));
        System.out.println("Transitlink DB running at " + transitlinkDb.getHost() + ":" + transitlinkDb.getMappedPort(5432));
        System.out.println("Keycloak running at " + keycloak.getHost() + ":" + keycloak.getMappedPort(9000));
    }
}
