package conventions.util

import com.android.build.gradle.tasks.asJavaVersion
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.plugin.use.PluginDependency
import kotlin.jvm.optionals.getOrNull

internal object VersionCatalogExt {
    /**
     * Obtain a version from a version catalog.
     *
     * @return the version for the given [alias].
     * @throws IllegalStateException if the version is not found.
     * @see [VersionCatalog.findVersion]
     * @see [VersionCatalog.versionOrNull]
     */
    internal fun VersionCatalog.version(alias: String): String {
        return versionOrNull(alias) ?: error("Version for '$alias' not found")
    }

    /**
     * Obtain a version from a version catalog.
     *
     * @return the version for the given [alias], or `null` if not found.
     * @see [VersionCatalog.findVersion]
     * @see [VersionCatalog.version]
     */
    internal fun VersionCatalog.versionOrNull(alias: String): String? {
        return findVersion(alias).getOrNull()?.toString()
    }

    /** Obtain the Java version from a version catalog, or a default value if not found. */
    internal fun VersionCatalog.javaOrDefault(default: String = "11"): JavaVersion {
        return JavaLanguageVersion.of(
            /* version = */ versionOrNull(alias = "java") ?: default
        ).asJavaVersion()
    }

    internal fun VersionCatalog.plugin(alias: String): PluginDependency {
        return findPlugin(alias).getOrNull()?.orNull ?: error("Plugin for '$alias' not found")
    }

    internal fun VersionCatalog.library(alias: String): MinimalExternalModuleDependency {
        return findLibrary(alias).getOrNull()?.orNull ?: error("Library for '$alias' not found")
    }
}
