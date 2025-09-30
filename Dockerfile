FROM gradle:8.14.3-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x unitTest -x integrationTest --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /transitlink
RUN addgroup -g 1000 transitlinkgroup && adduser -u 1000 -S transitlink -G transitlinkgroup \
    && mkdir -p /transitlink/logs /transitlink/data /transitlink/config /transitlink/secrets \
    && chown -R transitlink:transitlinkgroup /transitlink
USER transitlink
COPY --from=builder /app/build/libs/*.jar transitlink.jar
COPY --chown=transitlink:transitlinkgroup src/main/resources/application*.yaml /transitlink/config/
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar transitlink.jar --spring.config.location=file:/transitlink/config/"]
