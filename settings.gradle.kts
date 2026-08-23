pluginManagement {
	repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        mavenCentral()
		gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
	}
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.7"
}

// Should match your modid
rootProject.name = "volucraft"

stonecutter {
    kotlinController = true

    // Subproject configuration
    create(rootProject) {
        fun match(version: String, vararg loaders: String) = loaders.forEach {
            version("$version-$it", version).buildscript = "build.$it.gradle.kts"
        }

        match("26.1.2", "fabric")
        vcsVersion = "26.1.2-fabric"
    }
}