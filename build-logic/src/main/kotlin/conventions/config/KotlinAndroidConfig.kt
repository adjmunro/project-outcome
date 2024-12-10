package conventions.config

import com.android.build.api.dsl.CommonExtension
import conventions.util.DependencyHandlerScopeExt.implementation
import conventions.util.ProjectExt.libs
import conventions.util.VersionCatalogExt.javaOrDefault
import conventions.util.VersionCatalogExt.library
import conventions.util.VersionCatalogExt.versionOrNull
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal object KotlinAndroidConfig {
    /**
     * Configures Kotlin for an Android project.
     *
     * @param commonExtension the Android common extension.
     */
    internal fun Project.configureKotlinAndroid(
        commonExtension: CommonExtension<*, *, *, *, *, *>,
    ) {
        val java = libs.javaOrDefault()

        commonExtension.apply {
            compileSdk = libs.versionOrNull(alias = "compileSdk")?.toIntOrNull() ?: 34
            defaultConfig.minSdk = libs.versionOrNull(alias = "minSdk")?.toIntOrNull() ?: 23

            compileOptions {
                sourceCompatibility = java
                targetCompatibility = java
            }
        }

        dependencies {
            implementation(platform(libs.library("kotlin-bom")))
            implementation(libs.library("kotlin-stdlib"))
        }

        configureKotlin()
    }

    /**
     * Configures Kotlin for a JVM project.
     */
    internal fun Project.configureKotlinJvm() {
        val java = libs.javaOrDefault()

        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = java
            targetCompatibility = java
        }

        configureKotlin()
    }

    /**
     * Configures Kotlin for a project.
     */
    private fun Project.configureKotlin() {
        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = "${libs.javaOrDefault()}"

                freeCompilerArgs += listOf(
                    // Add compiler args here.
                )
            }
        }
    }
}
