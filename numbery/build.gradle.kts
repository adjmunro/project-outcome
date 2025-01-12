plugins {
    alias(libs.plugins.adjmunro.android.library)
}

android {
    namespace = "nz.adjmunro.nomadic.numbery"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {

}
