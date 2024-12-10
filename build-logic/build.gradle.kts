import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

// This java version is only used for build-logic.
private val javaVersion = JavaVersion.VERSION_11

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.gradle.plugin)
}

gradlePlugin {
    plugins {
        register(/* name = */ "android-application") {
            id = "adjmunro-android-application"
            implementationClass = "conventions.AndroidApplication"
        }
        register(/* name = */ "android-library") {
            id = "adjmunro-android-library"
            implementationClass = "conventions.AndroidLibrary"
        }
        register(/* name = */ "jvm-library") {
            id = "adjmunro-jvm-library"
            implementationClass = "conventions.JvmLibrary"
        }
    }
}
