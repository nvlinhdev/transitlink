FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add user
RUN addgroup -S transitlinkgroup && adduser -S transitlink -G transitlinkgroup
USER transitlink

COPY build/libs/*.jar app.jar

# Expose application port
EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
