package conventions

import conventions.util.DependencyHandlerScopeExt.androidTestImplementation
import conventions.util.DependencyHandlerScopeExt.testImplementation
import conventions.config.KotlinAndroidConfig.configureKotlinAndroid
import conventions.util.PluginManagerExt.id
import conventions.util.ProjectExt.library
import conventions.util.ProjectExt.libs
import conventions.util.ProjectExt.plugins
import conventions.util.VersionCatalogExt.plugin
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

internal class AndroidLibrary : ConventionPlugin({
    plugins {
        id(libs.plugin(alias = "android-library"))
        id(libs.plugin(alias = "kotlin-android"))
    }

    library {
        configureKotlinAndroid(commonExtension = this@library)
    }

    dependencies {
        testImplementation(kotlin("test"))
        androidTestImplementation(kotlin("test"))
    }
})
