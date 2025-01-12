package conventions

import conventions.config.KotlinAndroidConfig.configureKotlinJvm
import conventions.util.PluginManagerExt.id
import conventions.util.ProjectExt.libs
import conventions.util.ProjectExt.plugins
import conventions.util.VersionCatalogExt.plugin
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

internal class JvmLibrary : ConventionPlugin({
    plugins {
        id(libs.plugin(alias = "kotlin-jvm"))
    }

    configureKotlinJvm()

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
})
