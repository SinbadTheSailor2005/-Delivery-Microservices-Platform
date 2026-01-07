plugins {
    `java-library`
    id("io.spring.dependency-management")
}

group = "dev.vundirov"
version = "1.0.0"

repositories {
    mavenCentral()
}

// Это "магический" блок, который говорит Gradle, какие версии использовать
dependencyManagement {
    imports {
        // Замените 3.2.0 на версию вашего Spring Boot
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")
    }
}

dependencies {
    // Теперь Gradle найдет версию для Кафки в BOM
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-starter")

    api("org.springframework.kafka:spring-kafka")
    // Также добавьте Jackson, так как ваши DTO в библиотеке его используют
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.core:jackson-annotations")
    implementation("org.springframework.boot:spring-boot-starter-web")




    // lombok
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // additional libs
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Тесты
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}