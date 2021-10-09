plugins {
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("commons-io:commons-io:2.11.0")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

// runs all tests except "toolsNotInstalled"
tasks.getByName<Test>("test") {
    useJUnitPlatform() {
        excludeTags("toolsNotInstalled")
    }
}
// runs only "toolsNotInstalled" tests
tasks.register<Test>("testToolsNotInstalled") {
    useJUnitPlatform() {
        includeTags("toolsNotInstalled")
    }
}

java {
    toolchain {
        sourceCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
        targetCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
    }
}