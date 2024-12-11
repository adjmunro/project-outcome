plugins {
    alias(libs.plugins.adjmunro.android.library)
}

android {
    namespace = "nz.adjmunro.nomadic.error"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlinx.atomicfu)
    implementation(libs.kotlinx.coroutines.core)
}
