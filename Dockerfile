# ---- runtime only ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# JAR will be provided by Jenkins (via oc start-build --from-file)
ARG JAR_FILE=app.jar
COPY ${JAR_FILE} /app/app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]