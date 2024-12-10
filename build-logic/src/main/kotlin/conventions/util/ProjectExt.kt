package conventions.util

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

internal object ProjectExt {
    /** Accessor for the `libs` version catalog. */
    internal val Project.libs: VersionCatalog
        get() = extensions.getByType<VersionCatalogsExtension>().named(/* name = */ "libs")


    /** Context runner for PluginManager. */
    internal inline fun Project.plugins(action: PluginManager.() -> Unit) {
        pluginManager.apply(action)
    }

    /** Context runner for ApplicationExtension. */
    internal fun Project.application(action: ApplicationExtension.() -> Unit) {
        extensions.configure<ApplicationExtension>(action)
    }

    /** Context runner for LibraryExtension. */
    internal fun Project.library(action: LibraryExtension.() -> Unit) {
        extensions.configure<LibraryExtension>(action)
    }
}
