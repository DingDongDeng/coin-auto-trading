plugins {
    id("project.application-conventions")
    id("project.integration-test-conventions")
    id("project.kotlin-conventions")
    id("project.spring-conventions")
}

dependencies {
    implementation(project(":application-infra:common"))
}