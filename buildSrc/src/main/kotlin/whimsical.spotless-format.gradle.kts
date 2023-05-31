import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.java

plugins {
    id("com.diffplug.spotless")
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
        // Spotless can't target `java-gradle-plugin` sources, so we need to specify manually
        target("src/*/java/**/*.java")
        palantirJavaFormat()
        importOrder()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}