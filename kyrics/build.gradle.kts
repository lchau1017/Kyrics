plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.kyrics"
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()

    defaultConfig {
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.compose.animation)

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    // Testing - Unit Tests
    testImplementation(libs.bundles.testing.unit)

    // Testing - Android/Instrumented Tests
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.testing.android)

    // Debug
    debugImplementation(libs.bundles.compose.debug)
}

// Maven Publishing Configuration
mavenPublishing {
    coordinates(
        groupId = "com.kyrics",
        artifactId = "kyrics",
        version = "1.1.0",
    )

    pom {
        name.set("Kyrics")
        description.set(
            "A Jetpack Compose library for displaying synchronized karaoke-style lyrics " +
                "with customizable animations and visual effects.",
        )
        url.set("https://github.com/lchau1017/Kyrics")
        inceptionYear.set("2024")

        licenses {
            license {
                name.set("Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("lchau1017")
                name.set("Lung Chau")
            }
        }

        scm {
            url.set("https://github.com/lchau1017/Kyrics")
            connection.set("scm:git:git://github.com/lchau1017/Kyrics.git")
            developerConnection.set("scm:git:ssh://git@github.com/lchau1017/Kyrics.git")
        }
    }
}
