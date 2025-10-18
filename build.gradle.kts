// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
tasks.register("checkI18nKeys") {
    group = "verification"
    description = "Verify that all used string keys have FR/NL translations"
    doLast {
        val script = project.file("tools/check-i18n.ps1").absolutePath
        val pb = ProcessBuilder("pwsh", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", script)
        pb.directory(project.projectDir)
        val proc = pb.start()
        val exit = proc.waitFor()
        if (exit != 0) {
            throw GradleException("Missing FR/NL translations detected. See PowerShell output above.")
        }
    }
}

tasks.register("checkUnusedComposables") {
    group = "verification"
    description = "Warn about @Composable functions that are not referenced anywhere (potentially not routed)"
    doLast {
        val script = project.file("tools/check-unused-composables.ps1").absolutePath
        val pb = ProcessBuilder("pwsh", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", script)
        pb.directory(project.projectDir)
        val proc = pb.start()
        proc.inputStream.copyTo(System.out)
        proc.errorStream.copyTo(System.err)
        val exit = proc.waitFor()
        if (exit != 0) {
            logger.warn("Unused composables checker returned non-zero (treated as warning). See output above.")
        }
    }
}