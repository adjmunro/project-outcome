package conventions.util

import org.gradle.api.plugins.PluginManager
import org.gradle.plugin.use.PluginDependency

internal object PluginManagerExt {
    /** Unwrap and apply a [PluginDependency]. */
    internal fun PluginManager.id(alias: PluginDependency) {
        apply(alias.pluginId)
    }
}
