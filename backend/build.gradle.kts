plugins {
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.7"
    java
}

group = "org.intrabet"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

val postgresqlVersion = "42.7.7"
val lombokVersion = "1.18.34"
val jsonwebtokenVersion = "0.12.6"
val bouncycastleVersion = "1.81"
val mapstructVersion = "1.6.3"
val caffeineVersion = "3.2.3"
val jakartaCacheApiVersion = "1.1.1"
val jCacheVersion = "6.5.2.Final"
val ehcacheVersion = "3.11.1"
val druidVersion = "1.2.27"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("com.h2database:h2")
    implementation("org.mapstruct:mapstruct-processor:${mapstructVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    implementation("org.postgresql:postgresql:${postgresqlVersion}")
    implementation("io.jsonwebtoken:jjwt-api:${jsonwebtokenVersion}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${jsonwebtokenVersion}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${jsonwebtokenVersion}")
    implementation("org.bouncycastle:bcprov-jdk18on:${bouncycastleVersion}")
    implementation("com.github.ben-manes.caffeine:caffeine:${caffeineVersion}")
    implementation("com.github.ben-manes.caffeine:jcache:${caffeineVersion}")
    implementation("org.hibernate.orm:hibernate-jcache:${jCacheVersion}")
    implementation("com.alibaba:druid-spring-boot-3-starter:${druidVersion}")
    implementation("org.ehcache:ehcache:${ehcacheVersion}") {
        artifact {
            classifier = "jakarta"
        }
    }
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate6")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-cache")
}

tasks.test {
    useJUnitPlatform()
}