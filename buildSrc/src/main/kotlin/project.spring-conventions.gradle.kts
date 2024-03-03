plugins {
    id("project.kotlin-conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}