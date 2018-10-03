import org.gradle.api.Project

fun Project.property(names: String, defaultValue: String): String {
    return if (hasProperty(names)) property(names).toString() else defaultValue
}
