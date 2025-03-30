import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    `maven-publish`
}

group = "${libs.versions.project.group.get()}.${libs.versions.project.name.get()}"
version = libs.versions.project.version.get()

java {
    withJavadocJar() // TODO set up dokka
    withSourcesJar()
}

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.java.language.get().toInt())

    compilerOptions {
        // Target version of the generated JVM bytecode.
        jvmTarget = JvmTarget.fromTarget(target = libs.versions.java.language.get())

        // Free compiler args (e.g., experimental -X flags).
        freeCompilerArgs.addAll(
            // Suppressed Warnings
            // "-Xsuppress-warning=",
            "-opt-in=kotlin.experimental.ExperimentalTypeInference",
            "-opt-in=kotlin.contracts.ExperimentalContracts",
        )

        // Enable extra K2 warnings.
        extraWarnings = true
    }
}

sourceSets {
    val main by getting { kotlin.srcDirs("src/main/kotlin") }
    val test by getting { kotlin.srcDirs("src/test/kotlin") }
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>(name = "knomadic-maven-artifact") {
            from(components["kotlin"])
            groupId = libs.versions.project.group.get()
            artifactId = libs.versions.project.name.get()
            version = libs.versions.project.version.get()
        }
    }
    repositories {
        mavenLocal()

//       TODO
//        maven {
//            name = "GitHubPackages"
//            url = uri("https://maven.pkg.github.com/adjmunro/knomadic")
//            credentials {
//                username = System.getenv("GITHUB_ACTOR")
//                password = System.getenv("GITHUB_TOKEN")
//            }
//        }
    }
}

dependencies {
    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.stdlib)

    implementation(libs.jetbrains.coroutines.core)
    implementation(libs.jetbrains.atomicfu)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.assertions)
}
