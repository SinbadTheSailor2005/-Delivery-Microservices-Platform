FROM gradle:jdk21-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

COPY shared-libs shared-libs

COPY payment-service payment-service

RUN ./gradlew :payment-service:bootJar -x test --no-daemon

FROM bellsoft/liberica-openjre-alpine:21
WORKDIR /app

COPY --from=builder /app/payment-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]