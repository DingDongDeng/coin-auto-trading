plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // kotlin plugin
    val kotlinVersion = "1.9.21"
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")

    // spring plugin
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.3.2")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.6")

    // test plugin
    implementation("org.asciidoctor:asciidoctor-gradle-jvm:3.3.2")
    implementation("com.avast.gradle:gradle-docker-compose-plugin:0.16.12")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:4.3.0.3225")
    implementation("com.google.cloud.tools:jib-gradle-plugin:3.4.0")

    // front
    implementation("com.github.node-gradle.node:com.github.node-gradle.node.gradle.plugin:3.2.1")
}