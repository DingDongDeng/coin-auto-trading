plugins {
    id("project.kotlin-conventions")
    id("project.module-conventions")
    id("project.spring-conventions")
}

dependencies {
    implementation(project(":application-infra:common"))
    // jpa
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    // querydsl
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")

    runtimeOnly("com.h2database:h2")
}