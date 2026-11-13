import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

val wikiConfigDir = layout.buildDirectory.dir("generated/wikiConfig/kotlin")

val generateWikiConfig by tasks.registering {
    val baseUrl = providers.gradleProperty("wiki.baseUrl")
    val userAgent = providers.gradleProperty("wiki.userAgent")
    val outDir = wikiConfigDir
    inputs.property("baseUrl", baseUrl)
    inputs.property("userAgent", userAgent)
    outputs.dir(outDir)
    doLast {
        val dir = outDir.get().asFile.resolve("com/artem/wikiapp/config")
        dir.mkdirs()
        dir.resolve("WikiConfig.kt").writeText(
            "package com.artem.wikiapp.config\n\n" +
                    "object WikiConfig {\n" +
                    "    const val BASE_URL = \"${baseUrl.get()}\"\n" +
                    "    const val USER_AGENT = \"${userAgent.get()}\"\n" +
                    "}\n"
        )
    }
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    android {
        namespace = "com.artem.wikiapp.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.ktor.client.cio)
        }
        commonMain {
            kotlin.srcDir(generateWikiConfig)
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)

                implementation("org.jetbrains.androidx.navigation3:navigation3-ui:1.0.0-alpha05") // <-- ДОБАВЛЕНО

                implementation(libs.kotlinx.coroutinesCore)
                implementation(libs.kotlinx.serializationJson)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.serialization.kotlinxJson)
                implementation(libs.ktor.client.logging)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
        iosArm64Main.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        iosSimulatorArm64Main.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
            implementation(libs.ktor.client.js)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)

}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}