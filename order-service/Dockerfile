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

# Копируем сам микросервис
COPY order-service order-service

# Запускаем сборку
# Используем флаг --no-daemon, чтобы не тратить память
# Обратите внимание: путь к задаче :order-service:bootJar
RUN ./gradlew :order-service:bootJar -x test --no-daemon

# --- 2. Этап запуска (Финальный образ) ---
FROM bellsoft/liberica-openjre-alpine:21
WORKDIR /app

# Забираем только готовый JAR файл из этапа сборки
COPY --from=builder /app/order-service/build/libs/*.jar app.jar

# Запускаем
ENTRYPOINT ["java", "-jar", "app.jar"]