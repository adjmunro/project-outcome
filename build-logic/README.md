# Convention Plugins Readme
This directory is not a submodule like the rest of the project, but a [composite build](https://docs.gradle.org/current/userguide/composite_builds.html). It contains some utility functions and custom pre-compiled Gradle plugins known as ["convention plugins"](https://developer.squareup.com/blog/herding-elephants/).

## What are Convention Plugins?
Gradle Convention Plugins are a best-practise solution recommended by the Gradle Organisation, for reusing build logic across multiple projects and/or modules.

### Why Convention Plugins?
 - **Consistency:** They enforce a standardized project structure and build configuration across multiple projects, ensuring consistency.  
 - **Reusability**: Common build logic can be encapsulated in a plugin and reused across different projects, reducing duplication.  
 - **Maintainability**: Centralising build logic in plugins makes it easier to manage and update. Changes to the build logic can be made in one place and applied across all projects.  
 - **Scalability**: As projects grow, convention plugins help manage complexity by modularizing build logic.  
 - **Best Practices**: They can encapsulate best practices and enforce them across projects, improving code quality and build reliability.  
 - **Simplification**: They simplify the build scripts of individual projects by moving complex build logic into reusable plugins.

### How to use them?
The `build.gradle.kts (:build-logic)` file contains a `gradlePlugin` block, which registers our custom plugin classes as Gradle plugins. The `id` field is what we use to apply the plugin in other projects/modules.  

To apply a plugin in a module, add the following line to the `build.gradle.kts` file:
```kotlin
plugins {
    id("adjmunro-android-application")
}
```

| **Plugin ID**                  | **Description**                                          |
|--------------------------------|----------------------------------------------------------|
| `adjmunro-android-application` | Configures an Android application module.                |
| `adjmunro-android-library`     | Configures an Android library module.                    |

***Note:*** *Some plugins might REQUIRE some other plugin to be applied, such as AGP in order to access the `android` application & build variants extensions. This may cause an error such as:*
```logcatfilter 
Caused by: org.gradle.api.UnknownDomainObjectException: Extension of type 'ApplicationExtension' does not exist. Currently registered extension types: [ExtraPropertiesExtension, LibrariesForLibs, VersionCatalogsExtension, FeatureFlags.FeatureFlagsExtension]
```

---
## Further Reading:
### Square Blog Posts
*The idea of convention plugins was popularised by Square, who have written a number of blog posts on the subject.*
- [Square - Herding Elephants](https://developer.squareup.com/blog/herding-elephants/)
- [Square - Stampeding Elephants](https://developer.squareup.com/blog/stampeding-elephants/)

### Gradle User Guide
- [Gradle Composite Builds](https://docs.gradle.org/current/userguide/composite_builds.html)
- [Gradle Custom Plugins](https://docs.gradle.org/current/userguide/custom_plugins.html)

### Other
- [NowInAndroid](https://github.com/android/nowinandroid/blob/main/build-logic/README.md)
- [Idiomatic Gradle](https://github.com/jjohannes/idiomatic-gradle)]
- [Example Project](https://github.com/blank15/SplashWallpaper/tree/master/build-logic)
