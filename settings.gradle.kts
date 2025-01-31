pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
    }
}

rootProject.name = "little-heads"

include("core")
include("configurate-helper")
project(":configurate-helper").projectDir = File("../configurate-helper")