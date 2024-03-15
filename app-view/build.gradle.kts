import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("project.npm-conventions")
}

node {
    version = "v21.7.1" // 사용할 Node.js 버전
    npmVersion = "10.5.0" // 사용할 npm 버전
    download = true // Node.js 및 npm 자동 다운로드 여부

    /**
     * download가 true일 경우에만 사용
     * Node.js가 압축 해제된 디렉토리
     */
    workDir = file("${project.projectDir}/.gradle/nodejs")

    /**
     * npm이 설치된 디렉토리 (특정 버전이 정의된 경우)
     */
    npmWorkDir = file("${project.projectDir}/.gradle/npm")

    // Set the work directory where node_modules should be located
    nodeProjectDir = file("${project.projectDir}")

    /**
     * vue 프로젝트 디렉토리 위치
     * package.json 파일과 node_modules 디렉토리가 있는 곳
     * "저는 프로젝트 Root 아래 vue 프로젝트를 생성하였으므로 아래와 같이 주소를 작성해주었습니다."
     */
    nodeProjectDir = file("${project.projectDir}/vue-project")
}

tasks.register<NpmTask>("npmBuild") {
    dependsOn("npmInstall")
    dependsOn("clean")

    args = listOf("run", "build")
}

tasks.register("clean") {
    doFirst {
        delete("build/dist")
    }
}