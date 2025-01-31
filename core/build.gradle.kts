plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "1.7.7"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005")
}

tasks.runServer {
    minecraftVersion("1.21.3")

    downloadPlugins {
        hangar("ViaVersion", "5.1.1")
        url("https://github.com/dmulloy2/ProtocolLib/releases/download/5.3.0/ProtocolLib.jar")
        url("https://ci.lucko.me/job/spark/469/artifact/spark-bukkit/build/libs/spark-1.10.121-bukkit.jar")
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-snapshots"
        mavenContent {
            snapshotsOnly()
        }
    }
    maven("https://libraries.minecraft.net/")
    maven("https://repo.papermc.io/repository/maven-public/")
    //maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")

    implementation("org.jetbrains:annotations:24.0.0")
    implementation("org.spongepowered:configurate-yaml:4.0.0")
    implementation(project(":configurate-helper"))

    implementation("org.incendo:cloud-core:2.0.0")
    implementation("org.incendo:cloud-annotations:2.0.0")
    implementation("org.incendo:cloud-bukkit:2.0.0-beta.10")
    implementation("me.lucko:commodore:2.2")
    compileOnly("com.mojang:authlib:1.5.21")
    //compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
}

