plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.11"
    id("org.beryx.jlink") version "2.24.4"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val javaFxVersion = "19-ea+2"


// separate integration testing module - cf https://docs.gradle.org/current/samples/sample_java_modules_multi_project.html
val integrationTest = sourceSets.create("integrationTest")
configurations[integrationTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
configurations[integrationTest.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())


val integrationTestJarTask = tasks.register<Jar>(integrationTest.jarTaskName) {
    archiveClassifier.set("integration-tests")
    from(integrationTest.output)
}

val commonIntegrationTestConfiguration: Test.() -> Unit = {
    description = "Runs integration tests."
    group = "verification"
    jvmArgs = listOf("--add-exports", "javafx.graphics/com.sun.javafx.application=org.testfx",
            "--add-exports", "javafx.graphics/com.sun.glass.ui=ALL-UNNAMED",
            "--add-opens", "javafx.graphics/com.sun.glass.ui=org.testfx",
            "--add-exports", "javafx.graphics/com.sun.glass.ui=org.testfx.monocle",
            "--add-reads", "org.example.csv2tex=ALL-UNNAMED"
    )

    testClassesDirs = integrationTest.output.classesDirs
    // Make sure we run the 'Jar' containing the tests (and not just the 'classes' folder) so that test resources are also part of the test module
    classpath = configurations[integrationTest.runtimeClasspathConfigurationName] + files(integrationTestJarTask)
}
val integrationTestTask = tasks.register("integrationTest", commonIntegrationTestConfiguration)
integrationTestTask {
    useJUnitPlatform {
        excludeTags("toolsNotInstalled")
        excludeTags("texPackagesNotInstalled")
    }
}

val integrationTestToolsNotInstalledTask = tasks.register("integrationTestToolsNotInstalled", commonIntegrationTestConfiguration)
integrationTestToolsNotInstalledTask {
    useJUnitPlatform {
        includeTags("toolsNotInstalled")
    }
}

dependencies {
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")

    // ############################ Logging ###########################################

    implementation("org.slf4j:slf4j-api:1.7.36")

    // slf4j bindings:
    implementation("ch.qos.logback:logback-core:1.2.10")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.10")
    // implementation("org.slf4j:slf4j-simple:1.7.32")

    // ############################ JavaFX ############################################

    implementation("org.openjfx:javafx-base:${javaFxVersion}")
    implementation("org.openjfx:javafx-controls:${javaFxVersion}")
    implementation("org.openjfx:javafx-fxml:${javaFxVersion}")
    implementation("org.openjfx:javafx-graphics:${javaFxVersion}")

    implementation("net.raumzeitfalle.fx:filechooser:0.0.8")

    // ################################################################################

    testImplementation("org.assertj:assertj-core:3.22.0")
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    testImplementation("org.apache.pdfbox:pdfbox:2.0.25")

    // ######################## JavaFX testing #########################################

    // module required by junit, it seems
    testImplementation("org.apiguardian:apiguardian-api:1.1.2")

    val testFxVersion = "4.0.16-alpha"
    testImplementation("org.testfx:testfx-core:${testFxVersion}")
    testImplementation("org.testfx:testfx-junit5:${testFxVersion}")
    // headless tests: no UI required
    testImplementation("org.testfx:openjfx-monocle:jdk-12.0.1+2")

    "integrationTestImplementation"(project)

}

// runs all tests except "toolsNotInstalled" and "texPackagesNotInstalled"
tasks.getByName<Test>("test") {
    useJUnitPlatform {
        excludeTags("toolsNotInstalled")
        excludeTags("texPackagesNotInstalled")
    }
}
// runs only "toolsNotInstalled" tests
tasks.register<Test>("testToolsNotInstalled") {
    useJUnitPlatform {
        includeTags("toolsNotInstalled")
    }
}
// runs only "texPackagesNotInstalled" tests
tasks.register<Test>("testTexPackagesNotInstalled") {
    useJUnitPlatform {
        includeTags("texPackagesNotInstalled")
    }
}

// don't run the "not installed" tests by default
tasks.check {
    dependsOn(tasks.test)
    dependsOn(integrationTestTask)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


application {
    mainModule.set("org.example.csv2tex")
    mainClass.set("org.example.csv2tex.ui.Csv2TexApplication")
}

javafx {
    version = javaFxVersion
    modules("javafx.controls", "javafx.fxml")
}



jlink {
    imageZip.set(project.file("${buildDir}/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "app"
    }
}

tasks.jlinkZip {
    group = "distribution"
}