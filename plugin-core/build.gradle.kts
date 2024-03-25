repositories {
    maven("https://repo.codemc.io/repository/nms")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    implementation("cc.dreamcode.platform:core:1.9.8")
    implementation("cc.dreamcode.platform:bukkit:1.9.8")
    implementation("cc.dreamcode.platform:bukkit-command:1.9.8")
    implementation("cc.dreamcode.platform:bukkit-config:1.9.8")
    implementation("cc.dreamcode.platform:persistence:1.9.15")
    implementation("cc.dreamcode:utilities:1.2.22")
    implementation("cc.dreamcode:utilities-bukkit:1.2.22")
    implementation("cc.dreamcode.notice:minecraft:1.3.11")
    implementation("cc.dreamcode.notice:bukkit:1.4.9")
    implementation("cc.dreamcode.notice:bukkit-serdes:1.4.9")
    implementation("cc.dreamcode.command:core:1.4.16")
    implementation("cc.dreamcode.command:bukkit:1.4.16")

    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:5.0.0-beta.5")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:5.0.0-beta.5")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:5.0.0-beta.5")
    implementation("eu.okaeri:okaeri-persistence-flat:2.0.1")
    implementation("eu.okaeri:okaeri-persistence-jdbc:2.0.1")
    implementation("eu.okaeri:okaeri-persistence-mongo:2.0.1")
    implementation("eu.okaeri:okaeri-configs-json-gson:5.0.0-beta.5")
    implementation("eu.okaeri:okaeri-configs-json-simple:5.0.0-beta.5")
    implementation("eu.okaeri:okaeri-injector:2.1.0")
    implementation("eu.okaeri:okaeri-placeholders-core:4.0.7")
    implementation("eu.okaeri:okaeri-tasker-bukkit:2.1.0-beta.3")

    implementation("com.github.cryptomorin:XSeries:9.8.1")

    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.5")
}

tasks {
    shadowJar {

        archiveFileName.set("dream-antilogout-${project.version}.jar")

        minimize()

        relocate("com.cryptomorin", "cc.dreamcode.antilogout.libs.com.cryptomorin")
        relocate("eu.okaeri", "cc.dreamcode.antilogout.libs.eu.okaeri")

        relocate("cc.dreamcode.platform", "cc.dreamcode.antilogout.libs.cc.dreamcode.platform")
        relocate("cc.dreamcode.utilities", "cc.dreamcode.antilogout.libs.cc.dreamcode.utilities")
        relocate("cc.dreamcode.menu", "cc.dreamcode.antilogout.libs.cc.dreamcode.menu")
        relocate("cc.dreamcode.command", "cc.dreamcode.antilogout.libs.cc.dreamcode.command")
        relocate("cc.dreamcode.notice", "cc.dreamcode.antilogout.libs.cc.dreamcode.notice")

        relocate("org.bson", "cc.dreamcode.antilogout.libs.org.bson")
        relocate("com.mongodb", "cc.dreamcode.antilogout.libs.com.mongodb")
        relocate("com.zaxxer", "cc.dreamcode.antilogout.libs.com.zaxxer")
        relocate("org.slf4j", "cc.dreamcode.antilogout.libs.org.slf4j")
        relocate("org.json", "cc.dreamcode.antilogout.libs.org.json")
        relocate("com.google.gson", "cc.dreamcode.antilogout.libs.com.google.gson")
    }
}