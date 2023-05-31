allprojects {
    group = properties("pluginGroup")
    version = properties("pluginVersion")
}

fun properties(key: String) = project.findProperty(key).toString()
