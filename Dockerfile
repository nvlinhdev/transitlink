FROM eclipse-temurin:21-jre-alpine
WORKDIR /transitlink

RUN addgroup -g 1000 transitlinkgroup && adduser -u 1000 -S transitlink -G transitlinkgroup \
    && mkdir -p /transitlink/logs /transitlink/data /transitlink/config \
    && chown -R transitlink:transitlinkgroup /transitlink

USER transitlink

COPY build/libs/*.jar transitlink.jar
COPY --chown=transitlink:transitlinkgroup src/main/resources/application*.yaml /transitlink/config/

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar transitlink.jar --spring.config.location=file:/transitlink/config/"]
