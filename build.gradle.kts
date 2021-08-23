plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":web"))
}

allprojects {
    group = "org.sourcegrade"
    version = "0.1.0-SNAPSHOT"
    repositories {
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }
}

application {
    mainClass.set("org.sourcegrade.jagrweb.core.MainKt")
}

tasks {
    shadowJar {
        archiveFileName.set("JagrWeb-${project.version}.jar")
    }
}
