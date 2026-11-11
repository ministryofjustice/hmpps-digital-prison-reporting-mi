FROM --platform=$BUILDPLATFORM eclipse-temurin:21-jdk-jammy AS builder

ARG BUILD_NUMBER
ENV BUILD_NUMBER=${BUILD_NUMBER:-1_0_0}

WORKDIR /app
ADD . .
RUN ./gradlew --no-daemon assemble

FROM ghcr.io/ministryofjustice/hmpps-eclipse-temurin:21-jre-jammy

ARG BUILD_NUMBER
ENV BUILD_NUMBER=${BUILD_NUMBER:-1_0_0}

WORKDIR /app
COPY --from=builder --chown=appuser:appgroup /app/build/libs/hmpps-digital-prison-reporting-mi*.jar /app/app.jar
COPY --from=builder --chown=appuser:appgroup /app/build/libs/applicationinsights-agent*.jar /app/agent.jar
COPY --from=builder --chown=appuser:appgroup /app/applicationinsights.json /app
COPY --from=builder --chown=appuser:appgroup /app/applicationinsights.dev.json /app

USER 2000

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -XX:+AlwaysActAsServerClassMachine -XshowSettings:vm -javaagent:/app/agent.jar -jar /app/app.jar"]
