plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

android {
    namespace = "io.github.ajaypal.swipeablelist"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.foundation)

    testImplementation(libs.junit4)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.ajaypal.compose-swipeable-list"
            artifactId = "swipeablelist"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("Compose Swipeable List")
                description.set("A reusable Jetpack Compose swipeable list and row library for Android.")
                url.set("https://github.com/ajaypal/compose-swipeable-list")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit")
                    }
                }
                developers {
                    developer {
                        id.set("ajaypal")
                        name.set("Ajay Pal")
                    }
                }
                scm {
                    url.set("https://github.com/ajaypal/compose-swipeable-list")
                    connection.set("scm:git:https://github.com/ajaypal/compose-swipeable-list.git")
                    developerConnection.set("scm:git:ssh://git@github.com/ajaypal/compose-swipeable-list.git")
                }
            }
        }
    }
}
