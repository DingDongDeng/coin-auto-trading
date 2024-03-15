plugins {
}

// custom extension : https://docs.gradle.org/current/userguide/custom_plugins.html

interface BuildFrontEndExtension {
    var frontEndProject: Project?
    var frontBuildPath: String?
    var currentBuildPath: String?
}

// 프로젝트 확장(extension)을 설정합니다.
val buildFrontEndExtension = extensions.create<BuildFrontEndExtension>("buildFrontEnd")

// frontProject 변수를 설정합니다.
val frontEndProject = buildFrontEndExtension.frontEndProject
if (frontEndProject == null) {
    throw RuntimeException(
        """
       // frontEndProject는 필수값입니다.
       
       ex) 아래 extension을 추가해주세요.
       buildFrontEnd {
          frontEndProject = project(":app-view:dashboard")
       } 
    """.trimIndent()
    )
}
val frontProjectBuildPath = buildFrontEndExtension.frontBuildPath ?: "${frontEndProject!!.projectDir}/dist"
val currentProjectBuildPath = buildFrontEndExtension.currentBuildPath ?: "$projectDir/src/main/resources/static"

// npmBuild 태스크를 생성하고 설정합니다.
tasks.register("npmBuild") {
    // frontProject의 npmBuild 태스크에 의존성을 설정합니다.
    dependsOn(frontEndProject!!.tasks.getByName("npmBuild"))
}

// copyFiles 태스크를 생성하고 설정합니다.
tasks.register<Copy>("copyFiles") {
    // npmBuild 태스크가 완료된 후에 실행되도록 dependsOn을 설정합니다.
    dependsOn("npmBuild")

    // Vue.js 프로젝트의 빌드 결과물을 복사할 위치를 지정합니다.
    from(frontProjectBuildPath)
    into(currentProjectBuildPath)
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