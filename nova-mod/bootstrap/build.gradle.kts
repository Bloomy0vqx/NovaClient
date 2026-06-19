plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    archiveBaseName.set("novaclient-bootstrap")
    archiveVersion.set("1.0")
    manifest {
        attributes(
            "Premain-Class" to "com.novaclient.bootstrap.Bootstrap",
            "Can-Redefine-Classes" to "true"
        )
    }
}
