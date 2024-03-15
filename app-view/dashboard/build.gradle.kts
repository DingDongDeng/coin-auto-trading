import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("project.npm-conventions")
}

node {
    version = "21.7.1" // 사용할 Node.js 버전
    npmVersion = "10.5.0" // 사용할 npm 버전
    download = true // Node.js 및 npm 자동 다운로드 여부
}

tasks.register<NpmTask>("npmBuild") {
    dependsOn("npmInstall")
    dependsOn("clean")

    args = listOf("run", "build")
}

tasks.register("npmClean") {
    doFirst {
        delete("dist")
    }
}