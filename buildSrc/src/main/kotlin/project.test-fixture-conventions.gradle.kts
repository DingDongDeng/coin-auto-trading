plugins {
    id("project.kotlin-conventions")
    id("project.spring-conventions")
    `java-test-fixtures`
    idea
}

dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")

    // textFixture 모듈의 의존성을 사용하려는 다른 모듈에서는 아래처럼 사용
    // testImplementation(testFixtures(project(":some-module")))
    // integrationTestImplementation(testFixtures(project(":some-module")))
}

// IDE에서 test로 마킹
// https://ryanharrison.co.uk/2018/07/25/kotlin-add-integration-test-module.html
idea {
    module {
        testSources.from(project.sourceSets["testFixtures"].java.srcDirs)
        testSources.from(project.sourceSets["testFixtures"].kotlin.srcDirs)
        testResources.from(project.sourceSets["testFixtures"].resources.srcDirs)
    }
}