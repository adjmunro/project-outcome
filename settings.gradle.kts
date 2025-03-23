rootProject.name = "knomadic"

pluginManagement {
    /**
     * The pluginManagement.repositories block configures the repositories that Gradle uses
     * to search for and download the Gradle plugins and their transitive dependencies.
     * You can also use local repositories or define your own remote repositories.
     */
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }

    // Plugins applied here are automatically applied to all projects
    plugins {

    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {

    /**
     * The dependencyResolutionManagement.repositories block is where you configure the source
     * repositories of the dependencies used by all modules in your project. However, you should
     * configure module-specific repositories in the build.gradle.kts files of the respective modules.
     */
    repositories {
        mavenCentral()
        google()
    }

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}
