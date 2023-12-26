plugins {
    id("project.kotlin-conventions")
    id("project.module-conventions")
    id("project.spring-conventions")
}

dependencies {
    implementation(project(":infrastructure:common"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.auth0:java-jwt:3.18.2") // upbit api 헤더 생성을 위함
}