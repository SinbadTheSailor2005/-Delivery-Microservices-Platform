# ВАЖНО: Этот Dockerfile нужно запускать из КОРНЯ проекта (mini-yandex-eda)

# --- 1. Этап сборки ---
FROM gradle:jdk21-alpine AS builder
WORKDIR /app

# Копируем "скелет" всего проекта (Gradle, настройки, версии)
# Это нужно, чтобы подтянуть правильные версии плагинов
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Копируем общие библиотеки (shared-libs)
# Без этого order-service не соберется, так как он зависит от них
COPY shared-libs shared-libs

COPY warehouse-service warehouse-service

RUN ./gradlew :warehouse-service:bootJar -x test --no-daemon

FROM bellsoft/liberica-openjre-alpine:21
WORKDIR /app

COPY --from=builder /app/warehouse-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]