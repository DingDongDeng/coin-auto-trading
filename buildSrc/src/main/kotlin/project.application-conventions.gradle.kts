import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("project.kotlin-conventions")
}

group = "${property("projectGroup")}"
version = "${property("projectVersion")}"

tasks.withType<Jar> {
    enabled = false
}
tasks.withType<BootJar> {
    enabled = true
}