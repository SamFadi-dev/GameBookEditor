plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "gamebook.editor"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.json:json:20231013")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("gamebook.editor.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "gamebook.editor.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

tasks.register<Copy>("montefiore") {
    group = "build"
    dependsOn(tasks.jar)
    doNotTrackState("tasks.jar")
    val jarFile = tasks.jar.get().archiveFile
    from(jarFile)
    into(rootDir)
    rename(jarFile.get().asFile.name, "GameBookEditor.jar")
}
