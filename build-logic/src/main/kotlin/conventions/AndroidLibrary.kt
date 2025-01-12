package conventions

import conventions.config.KotlinAndroidConfig.configureKotlinAndroid
import conventions.util.PluginManagerExt.id
import conventions.util.ProjectExt.library
import conventions.util.ProjectExt.libs
import conventions.util.ProjectExt.plugins
import conventions.util.VersionCatalogExt.plugin

internal class AndroidLibrary : ConventionPlugin({
    plugins {
        id(libs.plugin(alias = "android-library"))
        id(libs.plugin(alias = "kotlin-android"))
    }

    library {
        configureKotlinAndroid(commonExtension = this@library)

        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        testOptions {
            unitTests.all {
                it.useJUnitPlatform()
            }
        }
    }
})
