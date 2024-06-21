import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("application")
}

group = "com.jan.dev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    testImplementation(kotlin("test"))
}

application {
    applicationName = "com.jan.dev.ServerKt" // Note: Replace with your actual package and class name
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.jar {
    archiveFileName.set("output.jar")
    manifest {
        attributes["Main-Class"] = project.application.applicationName
    }

    // This line of code recursively collects and copies all of a project's files
    // and adds them to the JAR itself. One can extend this task, to skip certain
    // files or particular types at will
    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
