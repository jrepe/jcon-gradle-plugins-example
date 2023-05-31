plugins {
    // Gradle provided plugin for developing plugins
    `java-gradle-plugin`
    id("whimsical.plugin-publish")
    id("whimsical.spotless-format")
}

dependencies {
    implementation("de.undercouch:gradle-download-task:5.4.0")
}

gradlePlugin {
    val profiler by plugins.creating {
        id = "com.whimsical.profiler"
        description = "Downloads the GCP Profiler binary and places it into the designated directory."
        implementationClass = "com.whimsical.profiler.GcpProfilerPlugin"
    }
}
