rootProject.name = "JagrWeb"
include("core")
include("web")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        val kotlinVersion: String by settings
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        val shadowVersion: String by settings
        id("com.github.johnrengelman.shadow") version shadowVersion
    }
}
