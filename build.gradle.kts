import org.gradle.kotlin.dsl.internal.sharedruntime.codegen.licenseHeader
import org.jetbrains.kotlin.js.backend.NoOpSourceLocationConsumer.newLine

plugins {
    kotlin("jvm") version "2.0.20"
    id("com.gradleup.shadow") version "8.3.3"
    id("io.papermc.paperweight.userdev") version "1.7.4"
    id("net.kyori.indra.licenser.spotless") version "3.1.3"
}

group = "net.strokkur"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        val libPath = "net.strokkur.craftattackreloaded.libs"

        relocate("org.jetbrains.kotlin", "$libPath.kotlin")
    }

}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

indraSpotlessLicenser {
    licenseHeaderFile(rootProject.file("HEADER"))
    property("name", project.name)
    property("year", "2024")
}
