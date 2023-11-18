plugins {
    id("project.sonarqube-conventions")
    id("project.jacoco-conventions")
}

allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

tasks.register<JacocoReport>("jacocoRootReport") {
    group = "verification"
    val sourceDirs = mutableListOf<File>()
    val dir = File("./")
    dir.walkTopDown().forEach { file ->
        if (file.toString().matches(".*/src/main/kotlin".toRegex())) {
            sourceDirs.add(file)
        }
    }
    sourceDirectories.setFrom(files(sourceDirs))
    classDirectories.setFrom(fileTree(dir).matching { include("**/classes") })
    additionalClassDirs.setFrom(fileTree(dir).matching {
        include("**/build/classes/kotlin/main/**/*.class")
    })
    executionData.setFrom(fileTree(dir).matching { include("**/jacoco*.exec") })
    onlyIf {
        true
    }
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
}