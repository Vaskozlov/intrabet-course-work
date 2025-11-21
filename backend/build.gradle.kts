plugins {
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.7"
    java
}

group = "org.vaskozlov.is.course"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

val postgresqlVersion = "42.7.7"
val lombokVersion = "1.18.34"
val argon2Version = "2.12"

dependencies {
    runtimeOnly("com.h2database:h2")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.postgresql:postgresql:${postgresqlVersion}")
    implementation("de.mkammerer:argon2-jvm:${argon2Version}")
}

tasks.test {
    useJUnitPlatform()
}