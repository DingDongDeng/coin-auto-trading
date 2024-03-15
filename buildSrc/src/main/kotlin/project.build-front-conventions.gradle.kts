plugins {
}

val frontProject = project(":app-view:dashboard")

tasks.register("buildFront") {
    // processResources 이전에 실행
    // :app-view:dashboard 의 npmBuild를 실행
    // 빌드 결과물을 resources/static으로 추가
    //

    // 빌드하고
    dependsOn(frontProject.tasks.getByName("npmBuild"))

    // 빌드 결과물을 static/resources에 복사
}
tasks.named<ProcessResources>("processResources") {
    dependsOn("buildFront")
}

tasks.register<Copy>("copyFiles") {
    from("${frontProject.projectDir}/dist")
    into("$projectDir/src/main/resources/static")
}

