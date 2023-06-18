FROM openjdk:17-alpine
WORKDIR /opt/app
RUN apk add curl
COPY target/test.jar /opt/app/test.jar
EXPOSE 8080
HEALTHCHECK --interval=5s --timeout=2s --start-period=5s \
   CMD curl -f --max-time 1 "http://localhost:8080/actuator/health" || kill 1
CMD ["java", "-jar", "/opt/app/test.jar"]
