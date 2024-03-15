plugins {
}

// custom extension : https://docs.gradle.org/current/userguide/custom_plugins.html

interface BuildFrontEndPluginExtension {
    var frontEndProject: Project
    var frontEndProjectBuildPath: String
    var currentProjectBuildPath: String
}

val extension = project.extensions.create<BuildFrontEndPluginExtension>("buildFrontEnd")

// (1)
tasks.named<ProcessResources>("processResources") {
    dependsOn("buildFrontEnd")
}

// (2)
project.task("buildFrontEnd") {
    dependsOn("copyBuildResult")
}

// (3)
tasks.register<Copy>("copyBuildResult") {
    dependsOn("npmBuild")
    from(extension.frontEndProjectBuildPath)
    into(extension.currentProjectBuildPath)
}

// (4)
tasks.register("npmBuild") {
    val frontEndProject = extension.frontEndProject
    dependsOn(frontEndProject.tasks.getByName("npmBuild"))
}

