plugins {
}

// custom extension : https://docs.gradle.org/current/userguide/custom_plugins.html

interface BuildFrontEndPluginExtension {
    var frontEndProject: Project
    var frontEndProjectBuildPath: String
    var currentProjectBuildPath: String
}

val extension = project.extensions.create<BuildFrontEndPluginExtension>("buildFrontEnd")

project.task("buildFrontEnd") {
    dependsOn("copyFiles")
}

// npmBuild 태스크를 생성하고 설정합니다.
tasks.register("npmBuild") {
    val frontEndProject = extension.frontEndProject
    // frontProject의 npmBuild 태스크에 의존성을 설정합니다.
    dependsOn(frontEndProject.tasks.getByName("npmBuild"))
}

// copyFiles 태스크를 생성하고 설정합니다.
tasks.register<Copy>("copyFiles") {
    // npmBuild 태스크가 완료된 후에 실행되도록 dependsOn을 설정합니다.
    dependsOn("npmBuild")

    // Vue.js 프로젝트의 빌드 결과물을 복사할 위치를 지정합니다.
    from(extension.frontEndProjectBuildPath)
    into(extension.currentProjectBuildPath)
}

// processResources 태스크를 수정합니다.
tasks.named<ProcessResources>("processResources") {
    // buildFront 태스크가 완료된 후에 실행되도록 dependsOn을 설정합니다.
    dependsOn("buildFrontEnd")
}