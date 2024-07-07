plugins {
    id("java-library")
    id("java-test-fixtures")
    id("maven-publish")
    id("io.freefair.lombok") version "8.6"
}

group = "com.artemistechnica.commons"
version = "0.0.8-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    api("org.redisson:redisson:3.32.0")
    api("org.postgresql:postgresql:42.7.3")
    api("org.mongodb:mongodb-driver-reactivestreams:5.1.1")
    api("org.mongodb:mongodb-driver-sync:5.1.1")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/artemistechnica/commons-java")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}