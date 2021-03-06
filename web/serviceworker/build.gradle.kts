import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackOutput.Target

plugins {
    kotlin("js")
}

val kotlinVersion: String by rootProject.extra

kotlin {
    target {
        browser {
            webpackTask {
                output.libraryTarget = Target.SELF
            }
        }
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js:$kotlinVersion")
            }
        }
    }
}
