package conventions.util

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.kotlin.dsl.DependencyHandlerScope

internal object DependencyHandlerScopeExt {
    internal fun DependencyHandlerScope.implementation(alias: MinimalExternalModuleDependency) {
        add(
            configurationName = "implementation",
            dependencyNotation = alias,
        )
    }

    internal fun DependencyHandlerScope.testImplementation(alias: MinimalExternalModuleDependency) {
        add(
            configurationName = "testImplementation",
            dependencyNotation = alias,
        )
    }

    internal fun DependencyHandlerScope.androidTestImplementation(alias: MinimalExternalModuleDependency) {
        add(
            configurationName = "androidTestImplementation",
            dependencyNotation = alias,
        )
    }

    internal fun DependencyHandlerScope.api(alias: MinimalExternalModuleDependency) {
        add(
            configurationName = "api",
            dependencyNotation = alias,
        )
    }

    internal fun DependencyHandlerScope.testApi(alias: MinimalExternalModuleDependency) {
        add(
            configurationName = "testApi",
            dependencyNotation = alias,
        )
    }

    internal fun DependencyHandlerScope.androidTestApi(alias: MinimalExternalModuleDependency) {
        add(
            configurationName = "androidTestApi",
            dependencyNotation = alias,
        )
    }

    internal fun DependencyHandlerScope.implementation(dependencyNotation: Any) {
        add(
            configurationName = "implementation",
            dependencyNotation = dependencyNotation,
        )
    }

    internal fun DependencyHandlerScope.testImplementation(dependencyNotation: Any) {
        add(
            configurationName = "testImplementation",
            dependencyNotation = dependencyNotation,
        )
    }

    internal fun DependencyHandlerScope.androidTestImplementation(dependencyNotation: Any) {
        add(
            configurationName = "androidTestImplementation",
            dependencyNotation = dependencyNotation,
        )
    }

    internal fun DependencyHandlerScope.api(dependencyNotation: Any) {
        add(
            configurationName = "api",
            dependencyNotation = dependencyNotation,
        )
    }

    internal fun DependencyHandlerScope.testApi(dependencyNotation: Any) {
        add(
            configurationName = "testApi",
            dependencyNotation = dependencyNotation,
        )
    }

    internal fun DependencyHandlerScope.androidTestApi(dependencyNotation: Any) {
        add(
            configurationName = "androidTestApi",
            dependencyNotation = dependencyNotation,
        )
    }
}
