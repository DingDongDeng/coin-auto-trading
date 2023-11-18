plugins {
    id("project.spring-conventions")
    id("com.avast.gradle.docker-compose")
    idea
}

// https://docs.gradle.org/8.2.1/userguide/java_testing.html#sec:configuring_java_integration_tests
sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}
val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
//val integrationTestRuntimeOnly by configurations.getting

configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

tasks.withType<IntegrationTest> {
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    useJUnitPlatform()
}

val integrationTestTaskName = "integrationTest"
task<IntegrationTest>(integrationTestTaskName) {
    systemProperty("spring.profiles.active", "integration-test")
    dockerCompose {
        useComposeFiles.add("${rootDir.path}/gradle/test-container/docker-compose.yml")
        isRequiredBy(tasks.named(integrationTestTaskName))
    }
}

task<IntegrationTest>("integrationEmbeddedTest") {
    systemProperty("spring.profiles.active", "integration-embedded-test")
}

dependencies {
    integrationTestImplementation("org.springframework.boot:spring-boot-starter-test")
}

// IDE에서 test로 마킹
// https://ryanharrison.co.uk/2018/07/25/kotlin-add-integration-test-module.html
idea {
    module {
        testSources.from(project.sourceSets["integrationTest"].java.srcDirs)
        testSources.from(project.sourceSets["integrationTest"].kotlin.srcDirs)
        testResources.from(project.sourceSets["integrationTest"].resources.srcDirs)
    }
}