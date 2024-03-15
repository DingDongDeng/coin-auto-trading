plugins {
}

// custom extension : https://docs.gradle.org/current/userguide/custom_plugins.html

interface BuildFrontEndExtension {
    var frontEndProject: Project
}

// 프로젝트 확장(extension)을 설정합니다.
val frontendExtensionBuild = extensions.create<BuildFrontEndExtension>("buildFrontEnd")

// frontProject 변수를 설정합니다.
val frontProject = frontendExtensionBuild.frontEndProject

// npmBuild 태스크를 생성하고 설정합니다.
tasks.register("npmBuild") {
    // frontProject의 npmBuild 태스크에 의존성을 설정합니다.
    dependsOn(frontProject.tasks.getByName("npmBuild"))
}

// copyFiles 태스크를 생성하고 설정합니다.
tasks.register<Copy>("copyFiles") {
    // npmBuild 태스크가 완료된 후에 실행되도록 dependsOn을 설정합니다.
    dependsOn("npmBuild")

    // Vue.js 프로젝트의 빌드 결과물을 복사할 위치를 지정합니다.
    from("${frontProject.projectDir}/dist")
    into("$projectDir/src/main/resources/static")
}

// buildFront 태스크를 생성하고 설정합니다.
tasks.register("buildFront") {
    // copyFiles 태스크가 완료된 후에 실행되도록 dependsOn을 설정합니다.
    dependsOn("copyFiles")
    // 여기서 필요한 경우 빌드 작업을 수행합니다.
}

// processResources 태스크를 수정합니다.
tasks.named<ProcessResources>("processResources") {
    // buildFront 태스크가 완료된 후에 실행되도록 dependsOn을 설정합니다.
    dependsOn("buildFront")
}