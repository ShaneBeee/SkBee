plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
    id("maven-publish")
}

// Version of SkBee
val projectVersion = "3.18.3"
// Minimum version of Minecraft that SkBee supports
val apiVersion = "1.21.8"
// Where this builds on the server
val serverLocation = "Minecraft/Skript/26-1"

java.sourceCompatibility = JavaVersion.VERSION_25

repositories {
    mavenCentral()
    mavenLocal()

    // Paper
    maven("https://repo.papermc.io/repository/maven-public/")

    // Skript
    maven("https://repo.skriptlang.org/releases")

    // JitPack
    maven("https://jitpack.io")

    // CodeMC (NBT-API)
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:26.1.1.build.+")

    // Skript
    compileOnly("com.github.SkriptLang:Skript:2.14.0")

    // commons-io
    compileOnly("commons-io:commons-io:2.14.0")
    compileOnly("org.apache.commons:commons-text:1.10.0")

    // NBT-API
    implementation("de.tr7zw:item-nbt-api:2.15.7")

    // FastBoard
    implementation("fr.mrmicky:fastboard:2.1.5")

    // Virtual Furnace
    implementation("com.github.ShaneBeeStudios:VirtualFurnace:1.1.2")

    // bStats
    implementation("org.bstats:bstats-bukkit:3.1.0")
}

tasks {
    register("server", Copy::class) {
        dependsOn("shadowJar")
        from("build/libs") {
            include("SkBee-*.jar")
            exclude("*-sources.jar")
            destinationDir = file("/Users/ShaneBee/Desktop/Server/${serverLocation}/plugins/")
        }
    }
    processResources {
        filesNotMatching("assets/**") {
            expand("version" to projectVersion, "apiversion" to apiVersion)
        }

    }
    compileJava {
        options.release = 25
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }
    shadowJar {
        archiveFileName = project.name + "-" + projectVersion + ".jar"
        archiveClassifier.set("")
        manifest.attributes["Main-Class"] = "com.shanebeestudios.skbee.game.GamesMain"
        relocate("de.tr7zw.changeme.nbtapi", "com.shanebeestudios.skbee.api.nbt")
        relocate("de.tr7zw.annotations", "com.shanebeestudios.skbee.api.nbt.annotations")
        relocate("fr.mrmicky.fastboard", "com.shanebeestudios.skbee.api.fastboard.base")
        relocate("com.shanebeestudios.vf", "com.shanebeestudios.skbee.api.virtualfurnace")
        relocate("org.bstats", "com.shanebeestudios.skbee.metrics")
        exclude("META-INF/**", "LICENSE", "plugin.yml")
    }
    jar {
        enabled = false
        dependsOn(shadowJar)
    }
    java {
        withSourcesJar()
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
        exclude(
            "com/shanebeestudios/skbee/api/listener", "com/shanebeestudios/skbee/elements",
            "com/shanebeestudios/skbee/api/command", "com/shanebeestudios/skbee/game"
        )
        (options as StandardJavadocDocletOptions).links(
            "https://javadoc.io/doc/org.jetbrains/annotations/latest/",
            "https://jd.papermc.io/paper/1.21.10/",
            "https://docs.skriptlang.org/javadocs/",
            "https://jd.advntr.dev/api/4.25.0/",
            "https://tr7zw.github.io/Item-NBT-API/v2-api/"
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.ShaneBeee"
            artifactId = "SkBee"
            version = projectVersion

            // For GradleUp Shadow 9.x, use this syntax:
            from(components["shadow"])

            // This adds the sources jar
            artifact(tasks["sourcesJar"])
        }
    }
}
