FROM gradle:8.13-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar

FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu AS runtime
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
