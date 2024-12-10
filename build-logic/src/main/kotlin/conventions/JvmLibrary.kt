package conventions

import conventions.config.KotlinAndroidConfig.configureKotlinJvm
import conventions.util.PluginManagerExt.id
import conventions.util.ProjectExt.libs
import conventions.util.ProjectExt.plugins
import conventions.util.VersionCatalogExt.plugin

internal class JvmLibrary : ConventionPlugin({
    plugins {
        id(libs.plugin(alias = "kotlin-jvm"))
    }

    configureKotlinJvm()
})
