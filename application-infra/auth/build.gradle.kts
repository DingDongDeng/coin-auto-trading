plugins {
    id("project.kotlin-conventions")
    id("project.module-conventions")
    id("project.spring-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
}