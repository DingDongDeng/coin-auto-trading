import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("project.kotlin-conventions")
}

tasks.withType<Jar> {
    enabled = true
}
tasks.withType<BootJar> {
    enabled = false
}