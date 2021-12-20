plugins {
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")

    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    implementation("org.apache.pdfbox:pdfbox:2.0.25")
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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}