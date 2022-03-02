plugins {
    kotlin("jvm") version "1.4.10"
    application
}

group "org.example"
version "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

//task stage(dependsOn: ["build", "clean"])
//build.mustRunAfter clean

//apply plugin: "application"

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("com.github.KS2003:TelegramAPI1:28b06cd87c")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("CasinoBotKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.github.KS2003.CasinoBot.CasinoBotKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
