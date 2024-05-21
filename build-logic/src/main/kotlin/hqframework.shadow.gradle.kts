plugins {
    java
    id("com.github.johnrengelman.shadow")
}

tasks.shadowJar {
    archiveBaseName.set(project.rootProject.name.lowercase())
    archiveVersion.set("")
    archiveClassifier.set(project.name)
    if (project.name.contains("bukkit")) {
        destinationDirectory.set(file(/*rootProject.projectDir.path + "/build_outputs"*/"D:\\서버\\1.20.6 - 개발\\plugins"))
    }
}