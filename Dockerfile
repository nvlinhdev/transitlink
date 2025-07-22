# Stage 1: Build
FROM gradle:8.7.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add user
RUN addgroup -S transitlinkgroup && adduser -S transitlink -G transitlinkgroup
USER transitlink

COPY --from=build /app/build/libs/*.jar app.jar

# Expose application port
EXPOSE 8888

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s CMD wget --no-verbose --tries=1 --spider http://localhost:8888/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
