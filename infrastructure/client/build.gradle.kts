plugins {
    id("project.kotlin-conventions")
    id("project.module-conventions")
    id("project.spring-conventions")
}

dependencies {
    implementation(project(":infrastructure:common"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}