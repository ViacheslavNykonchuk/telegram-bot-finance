val telegramBotVersion = "6.8.0"

plugins {
    id("com.google.protobuf") version "0.9.4"
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.9.0"
    kotlin("plugin.allopen") version "1.9.0"
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":finance-service-api"))

    implementation("org.telegram:telegrambots:$telegramBotVersion")
    implementation("org.telegram:telegrambotsextensions:$telegramBotVersion")
    implementation("org.telegram:telegrambots-spring-boot-starter:$telegramBotVersion")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.nats:jnats:2.16.14")
    implementation("com.google.protobuf:protobuf-java:3.24.3")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("com.willowtreeapps.assertk:assertk:0.27.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}