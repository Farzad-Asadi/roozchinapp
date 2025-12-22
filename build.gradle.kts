// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("nav_version", "2.5.3")
        set("room_version", "2.5.2")
    }

    dependencies {
        classpath("com.squareup:javapoet:1.13.0")
    }

    configurations.getByName("classpath").resolutionStrategy {
        force("com.squareup:javapoet:1.13.0")
    }
}
plugins {
//    id("com.android.application") version "8.0.2" apply false
//    id("com.android.library") version "8.0.2" apply false
//    id("org.jetbrains.kotlin.android") version "1.8.21" apply false

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}


subprojects {
    configurations.configureEach {
        resolutionStrategy {
            force("com.squareup:javapoet:1.13.0")
        }
    }
}