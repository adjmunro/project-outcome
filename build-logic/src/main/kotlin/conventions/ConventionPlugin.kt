package conventions

import org.gradle.api.Plugin
import org.gradle.api.Project

internal sealed class ConventionPlugin(
    private val block: Project.() -> Unit,
) : Plugin<Project> {
    override fun apply(target: Project) {
        target.block()
    }
}
