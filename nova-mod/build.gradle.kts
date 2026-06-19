plugins {
    java
}

allprojects {
    group = "com.novaclient"
    version = "1.0.0"
}

subprojects {
    apply(plugin = "java")

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    // Only bundle core into non-Fabric adapters.
    // Fabric adapters (1_20_x, 1_21_x) use Loom's include() JiJ instead.
    // Fabric adapters manage their own bundling (JiJ or fat-jar); skip root bundling for them
    val fabricAdapters = setOf("adapter-1_20_x", "adapter-1_21_x", "adapter-26_1_2")
    if (name.startsWith("adapter") && !fabricAdapters.contains(name)) {
        tasks.withType<Jar>().configureEach {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            from(project(":core").extensions.getByType<JavaPluginExtension>().sourceSets["main"].output) {
                exclude("fabric.mod.json")
            }
        }
    }
}

