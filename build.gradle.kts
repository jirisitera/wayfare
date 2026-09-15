plugins {
    id("java")
    id("com.gradleup.shadow") version "9.6.1"
}

group = "com.japicraft"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // core
    implementation("net.minestom:minestom:2026.09.12-26.2")
    // logging
    implementation("org.slf4j:slf4j-simple:2.0.19")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "com.japicraft.Wayfare"
        }
    }
    shadowJar {
        archiveClassifier.set("")
        archiveVersion.set("")
        archiveBaseName.set("wayfare")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        exclude("META-INF/LICENSE.txt")
        mergeServiceFiles()
    }
}
