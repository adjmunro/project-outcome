package conventions

import conventions.config.KotlinAndroidConfig.configureKotlinAndroid
import conventions.util.PluginManagerExt.id
import conventions.util.ProjectExt.application
import conventions.util.ProjectExt.libs
import conventions.util.ProjectExt.plugins
import conventions.util.VersionCatalogExt.plugin
import conventions.util.VersionCatalogExt.versionOrNull

internal class AndroidApplication : ConventionPlugin({
    plugins {
        id(libs.plugin(alias = "android-application"))
        id(libs.plugin(alias = "kotlin-android"))
    }

    application {
        configureKotlinAndroid(commonExtension = this@application)
        defaultConfig.targetSdk = libs.versionOrNull(alias = "targetSdk")?.toIntOrNull() ?: 34
    }
})
