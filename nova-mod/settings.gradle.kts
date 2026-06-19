rootProject.name = "NovaClientMod"

include(":core")
include(":bootstrap")
include(":forge-stubs")
include(":adapter-1_7_10")
include(":adapter-1_12_2")
include(":adapter-1_16_5")
include(":adapter-1_20_x")
include(":adapter-1_21_x")
include(":adapter-26_1")
include(":adapter-26_1_2")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.fabricmc.net/") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.minecraftforge.net/") }
        maven { url = uri("https://jitpack.io") }
    }
}
