plugins {
    java
    id("org.springframework.boot") version "2.6.11"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "org.app"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.projectlombok:lombok:1.18.28")
    implementation("org.projectlombok:lombok:1.18.28")
    implementation("org.projectlombok:lombok:1.18.28")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
