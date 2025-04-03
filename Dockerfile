FROM eclipse-temurin:17-jdk-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app

COPY applications/app-service/build/libs/ApiBackProj.jar ApiBackProj.jar

RUN chown appuser:appgroup ApiBackProj.jar
EXPOSE 8080

ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

USER appuser

CMD ["sh", "-c", "java $JAVA_OPTS -jar ApiBackProj.jar"]



