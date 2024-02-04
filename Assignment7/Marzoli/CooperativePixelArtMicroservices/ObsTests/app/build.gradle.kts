/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.3/userguide/building_java_projects.html in the Gradle documentation.
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Brave
	implementation("io.zipkin.reporter2:zipkin-reporter:2.16.4")
	implementation("io.zipkin.reporter2:zipkin-sender-okhttp3:2.16.4")
	implementation("io.zipkin.brave:brave:5.16.0")
	implementation("io.zipkin.reporter2:zipkin-reporter-brave")
	 	
  	// Prometheus
  	implementation("io.prometheus:prometheus-metrics-core:1.0.0")
	implementation("io.prometheus:prometheus-metrics-instrumentation-jvm:1.0.0")
	implementation("io.prometheus:prometheus-metrics-exporter-httpserver:1.0.0")
  	
  	
    // This dependency is used by the application.
    implementation("com.google.guava:guava:32.1.1-jre")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("cooperativepixelart.App")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
