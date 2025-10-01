#----build stage------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Wrapper + root gradle files
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle

# Service sources (default: auth-service)
ARG SERVICE=auth-service
COPY ${SERVICE} ./${SERVICE}

# Build only the target service
RUN chmod +x ./gradlew \
 && ./gradlew :${SERVICE}:clean :${SERVICE}:bootJar --no-daemon

# ---- runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
ARG SERVICE=auth-service

COPY --from=build /app/${SERVICE}/build/libs/*.jar /app/app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]