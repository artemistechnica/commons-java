plugins {
    id("java-library")
    id("java-test-fixtures")
    id("maven-publish")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/artemtechnica/commons")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}