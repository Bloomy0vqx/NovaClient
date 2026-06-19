plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(8)
}

tasks.register<JavaExec>("runCoreTest") {
    group = "application"
    description = "Runs a tiny Java program that loads NovaCore"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.novaclient.core.TestRunner")
}

