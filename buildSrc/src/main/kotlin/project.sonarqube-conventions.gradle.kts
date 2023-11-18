plugins {
    id("org.sonarqube")
}

// https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/analysis-parameters/#project-configuration
sonarqube {
    properties {
        property("sonar.sources", "src/main")
        property("sonar.exclusions", "**/*Test*.*,**/Q*.java, **/*.js,**/build.gradle.kts")
        property("sonar.test.inclusions", "**/*Test.kt,**/*Test.java")
        property("sonar.coverage.exclusions", "**/*Test*.*, **/Q*.kt, **/Q*.java, **/*.js")
        property("sonar.java.junit.reportPath", "${project.buildDir}/test-results")
        //property("sonar.jacoco.reportPaths", null)
        //property("sonar.jacoco.reportPath", null)
        property("sonar.host.url", "https://localhost") // TODO
        property("sonar.projectName", "autotrading")
        property("sonar.projectVersion", "${property("projectVersion")}")
        property("sonar.sourceEncoding", "UTF-8")
    }
}