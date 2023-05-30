plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("de.undercouch:gradle-download-task:5.3.0")
}

gradlePlugin {
    val profiler by plugins.creating {
        id = "com.whimsical.profiler"
        description = "Downloads the GCP Profiler binary and places it into the designated directory."
        implementationClass = "com.whimsical.profiler.GcpProfilerPlugin"
    }
}