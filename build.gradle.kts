import java.io.IOException

plugins {
    java
    application
    eclipse
    idea
    id("org.openjfx.javafxplugin") version "0.0.12"
    id("com.github.ben-manes.versions") version "0.42.0"
}

group = "net.querz"
val sourceCompatibility = JavaLanguageVersion.of(17)
val targetCompatibility = JavaLanguageVersion.of(17)

val compileJava: JavaCompile by tasks
compileJava.options.encoding = "UTF-8"

application {
    mainClass.set("net.querz.mcaselector.Main")
}

configurations {
    implementation {
        isCanBeResolved = true
    }
}

javafx {
    version = "$sourceCompatibility"
    modules = listOf("javafx.controls", "javafx.swing")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://jitpack.io/")
    }
}

dependencies {
    implementation("com.github.Querz:NBT:f279a237fb")
    implementation("org.json:json:20220320")
    implementation("ar.com.hjg:pngj:2.1.0")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("it.unimi.dsi:fastutil:8.5.8")
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("me.tongfei:progressbar:0.9.3")
    implementation("org.codehaus.groovy:groovy-jsr223:3.0.11")

    testImplementation("junit:junit:4.13.2")
    testImplementation("commons-io:commons-io:2.11.0")
}

/**
 * "Minifies" a css file by removing all comments, \n, \t and all duplicate spaces.
 *
 * @param input The input css file
 * @param output The output css file
 * @throws IOException If something goes wrong during reading or writing
 */
@Throws(IOException::class)
fun minCss(input:File,output:File){
    val regex = "/\\*.*?\\/".toRegex()
    val text = input.readText()
        .replace("\t", "")
        .replace("\r\n", " ")
        .replace("\n", " ")
        .replace(regex,"")
        .replace(" {2,}"," ").trim()
    output.writeText(text)
}

val minifyCss = tasks.register("minifyCss") {

    doLast {

        minCss(
            file("${sourceSets.main.get().resources.srcDirs.first()}/style.css"),
            file("${sourceSets.main.get().output.resourcesDir}/style.css"),
        )
    }
    dependsOn("processResources")
}

val jar: Jar by tasks

jar.apply {
    archiveFileName.set("${project.name}-${project.version}.jar")
    manifest {
        this.attributes(
            mapOf(
                "Application-Version" to project.version
            )
        )
    }
    exclude("licenses/")
    from("LICENSE")
    dependsOn(minifyCss)

}
