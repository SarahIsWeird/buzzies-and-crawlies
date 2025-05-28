import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom") version "1.10-SNAPSHOT"
    kotlin("jvm") version "2.1.21"
    `maven-publish`
}

val archivesBaseName: String by project
val modVersion: String by project
val mavenGroup: String by project

base {
    val archivesBaseName: String by project
    archivesName = archivesBaseName
}

version = modVersion
group = mavenGroup

repositories {
    exclusiveContent {
        forRepository {
            maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/") {
                name = "GeckoLib"
            }
        }

        filter {
            includeGroup("software.bernie.geckolib")
        }
    }

    maven("https://maven.shedaniel.me/")
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("buzzes_and_crawls") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}

dependencies {
    val minecraftVersion: String by project
    val yarnMappings: String by project
    val loaderVersion: String by project
    val fabricVersion: String by project
    val fabricKotlinVersion: String by project
    val geckolibVersion: String by project
    val log4jKotlinVersion: String by project

    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricKotlinVersion")

    modImplementation("software.bernie.geckolib:geckolib-fabric-$minecraftVersion:$geckolibVersion")

    // Makes Kotlin logging simpler. Must be included in the final jar, hence `include` (= jar-in-jar).
    implementation("org.apache.logging.log4j:log4j-api-kotlin:$log4jKotlinVersion")
    include("org.apache.logging.log4j:log4j-api-kotlin:$log4jKotlinVersion")

    // Dev-time dependencies
    val reiVersion: String by project

    modLocalRuntime("me.shedaniel:RoughlyEnoughItems-fabric:$reiVersion")
}

tasks.processResources {
    inputs.property("version", modVersion)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    inputs.property("archivesName", base.archivesName)

    from("LICENSE") {
        rename { "${it}_${properties["archivesName"]}" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = archivesBaseName
            from(components["java"])
        }
    }

    repositories {}
}
