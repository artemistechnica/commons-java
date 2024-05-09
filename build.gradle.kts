import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("java")
    id("java-test-fixtures")
}

group = "com.artemistechnica.commons"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    maxHeapSize = "1G"

    debugOptions {
        enabled = true
        host = "localhost"
        port = 4455
        server = true
        suspend = false
    }
    testLogging {
        events("passed")
        showStandardStreams = true
        debug {
            events("started", "skipped", "failed")
        }
    }
}