val jacocoVersion = "0.8.10"
plugins {
    id("project.kotlin-conventions")
    jacoco
}

configure<JacocoPluginExtension> {
    toolVersion = jacocoVersion
}

jacoco {
    toolVersion = jacocoVersion
}

tasks.withType<Test> {
    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("${buildDir}/jacoco/jacoco.exec"))
    }
    finalizedBy("jacocoTestReport")
}

tasks.withType<JacocoReport> {
    group = "verification"
    executionData.setFrom(fileTree(buildDir).include("jacoco/jacoco*.exec"))
    reports {
        html.required.set(false)
        xml.required.set(true)
        csv.required.set(false)
    }
}

tasks.withType<JacocoCoverageVerification> {
    violationRules {
        rule {
            element = "BUNDLE"

            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.5".toBigDecimal()
            }
        }
    }
}

tasks.withType<IntegrationTest> {
    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("${buildDir}/jacoco/jacocoIntegration.exec"))
    }
    finalizedBy("jacocoTestReport")
}
