import org.objectweb.asm.*
import org.objectweb.asm.tree.*
import java.util.*
import java.util.zip.*

buildscript {
    dependencies {
        classpath("com.gradle.publish:plugin-publish-plugin:0.9.1")
        classpath("nl.javadude.gradle.plugins:license-gradle-plugin:0.11.0")
        classpath("org.ow2.asm:asm:6.2.1")
        classpath("org.ow2.asm:asm-tree:6.2.1")
    }
}

plugins {
    `maven-publish`
    java
    idea
    eclipse
    `java-gradle-plugin`
}

apply(plugin = "license")

group = "com.anatawa12.forge"

version = "2.3-${property("version")!!}"

base {
    archivesBaseName = "ForgeGradle"
}
java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenLocal()
    maven("https://maven.minecraftforge.net/") {
        name = "forge"
    }
    maven("https://repo.eclipse.org/content/groups/eclipse/") {
        // because Srg2Source needs an eclipse dependency.
        name = "eclipse"
    }
    jcenter() // get as many deps from here as possible
    mavenCentral()

    // because SS and its snapshot
    maven("https://repo.eclipse.org/content/groups/eclipse/") {
        name = "sonatype"
    }


    // because of the GradleStart stuff
    maven("https://libraries.minecraft.net/") {
        name = "mojang"
    }
}

val deployerJars by configurations.creating
val shade by configurations.creating
val compileOnly by configurations.getting
compileOnly.extendsFrom(shade)
configurations {
    all {
        resolutionStrategy {
            force("org.ow2.asm:asm-commons:6.0")
            force("org.ow2.asm:asm-tree:6.0")
            force("org.ow2.asm:asm:6.0")
            // pin eclipse for java 8
            force("org.eclipse.platform:org.eclipse.equinox.common:3.14.100")
            force("org.eclipse.platform:org.eclipse.core.resources:3.14.0")
            force("org.eclipse.platform:org.eclipse.text:3.11.0")
            force("org.eclipse.platform:org.eclipse.core.expressions:3.7.100")
            force("org.eclipse.platform:org.eclipse.core.runtime:3.22.0")
            force("org.eclipse.platform:org.eclipse.core.jobs:3.11.0")
            force("org.eclipse.platform:org.eclipse.core.contenttype:3.7.1000")
            force("org.eclipse.platform:org.eclipse.equinox.app:1.5.100")
            force("org.eclipse.platform:org.eclipse.equinox.registry:3.10.200")
            force("org.eclipse.platform:org.eclipse.core.commands:3.9.800")
        }
    }
}

dependencies {
    compile(gradleApi())

    // moved to the beginning to be the overrider
    //compile("org.ow2.asm:asm-debug-all:6.0")
    compile("com.google.guava:guava:31.1-jre")

    compile("net.sf.opencsv:opencsv:2.3") // reading CSVs.. also used by SpecialSource
    compile("com.cloudbees:diff4j:1.3") // for difing and patching
    compile("com.github.abrarsyed.jastyle:jAstyle:1.3") // formatting
    compile("net.sf.trove4j:trove4j:3.0.3") // because its awesome.

    compile("com.github.jponge:lzma-java:1.3") // replaces the LZMA binary
    compile("com.nothome:javaxdelta:2.0.1") // GDIFF implementation for BinPatches
    compile("com.google.code.gson:gson:2.10.1") // Used instead of Argo for buuilding changelog.
    compile("com.github.tony19:named-regexp:0.2.6") // 1.7 Named regexp features
    compile("net.minecraftforge:forgeflower:1.0.342-SNAPSHOT") // Fernflower Forge edition

    shade("net.md-5:SpecialSource:1.11.0") // deobf and reobf

    // because curse
    compile("org.apache.httpcomponents:httpclient:4.5.13")
    compile("org.apache.httpcomponents:httpmime:4.5.13")

    // mcp stuff
    shade("de.oceanlabs.mcp:RetroGuard:3.6.6")
    shade("de.oceanlabs.mcp:mcinjector:3.4-SNAPSHOT") {
        exclude(group = "org.ow2.asm")
    }
    shade("net.minecraftforge:Srg2Source:5.0.+") {
        exclude(group = "org.ow2.asm")
        exclude(group = "org.eclipse.equinox", module = "org.eclipse.equinox.common")
        exclude(group = "cpw.mods",            module = "modlauncher")
    }

    // pin jdt deps
    // locked means locked due to java 8
    shade("org.eclipse.jdt:org.eclipse.jdt.core:3.26.0") // locked
    shade("org.eclipse.platform:org.eclipse.core.commands:3.9.800") // locked
    shade("org.eclipse.platform:org.eclipse.core.contenttype:3.7.1000") // locked
    shade("org.eclipse.platform:org.eclipse.core.expressions:3.7.100") // locked
    shade("org.eclipse.platform:org.eclipse.core.filesystem:1.7.700") // locked
    shade("org.eclipse.platform:org.eclipse.core.jobs:3.11.0") // locked
    shade("org.eclipse.platform:org.eclipse.core.resources:3.14.0") // locked
    shade("org.eclipse.platform:org.eclipse.core.runtime:3.22.0") // locked
    shade("org.eclipse.platform:org.eclipse.equinox.app:1.5.100") // locked
    shade("org.eclipse.platform:org.eclipse.equinox.common:3.14.100") // locked
    shade("org.eclipse.platform:org.eclipse.equinox.preferences:3.9.100") // locked
    shade("org.eclipse.platform:org.eclipse.equinox.registry:3.10.200") // locked
    shade("org.eclipse.platform:org.eclipse.osgi:3.18.100")
    shade("org.eclipse.platform:org.eclipse.text:3.11.0") // locked
    shade("org.osgi:org.osgi.service.prefs:1.1.2")
    shade("org.osgi:osgi.annotation:8.1.0")

    //Stuff used in the GradleStart classes
    compileOnly("com.mojang:authlib:1.5.16")
    compileOnly("net.minecraft:launchwrapper:1.11"){
       exclude(group = "org.ow2.asm")
    }

    //compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.1.3-2")
    testCompile("junit:junit:4.13.2")
}

val wrapper by tasks.getting(Wrapper::class) {
    gradleVersion = "6.3"
    distributionType = Wrapper.DistributionType.ALL
}

sourceSets {
    val main by getting
    val test by getting
    main.compileClasspath += shade
    main.runtimeClasspath += shade
    test.compileClasspath += shade
    test.runtimeClasspath += shade
}

val compileJava by tasks.getting(JavaCompile::class) {
	options.isDeprecation = true
	//options.compilerArgs += ["-Werror"]
	//options.compilerArgs += ["-Werror", "-Xlint:unchecked"]
}

val processResources by tasks.getting(Copy::class) {
    from(sourceSets.main.get().resources.srcDirs) {
        include("forgegradle.version.txt")
        expand(mutableMapOf("version" to project.version))
    }
    from(sourceSets.main.get().resources.srcDirs) {
        exclude("forgegradle.version.txt")
    }
}

//TODO: Eclipse complains about unused messages. Find a way to make it shut up.
open class  PatchJDTClasses : DefaultTask() {
    companion object {
        val COMPILATION_UNIT_RESOLVER = "org/eclipse/jdt/core/dom/CompilationUnitResolver"
        val RANGE_EXTRACTOR = "net/minecraftforge/srg2source/ast/RangeExtractor"
    }
    val RESOLVE_METHOD = "resolve([Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;Lorg/eclipse/jdt/core/dom/FileASTRequestor;ILjava/util/Map;I)V"
    val GET_CONTENTS = "org/eclipse/jdt/internal/compiler/util/Util.getFileCharContent(Ljava/io/File;Ljava/lang/String;)[C"
    val HOOK_DESC_RESOLVE = "(Ljava/lang/String;Ljava/lang/String;)[C"

    @Input var targets = mutableSetOf<String>()
    @Input var libraries = mutableSetOf<File>()
    @OutputFile lateinit var output: File

    fun target(value: String) {
        targets.add(value)
    }

    fun library(value: File) {
        libraries.add(value)
    }

    @TaskAction
    fun patchClass() {
        val toProcess = targets.toMutableSet()
        output.outputStream().let { ZipOutputStream(it) }.use { zout ->
            libraries.filter{ !it.isDirectory() }.forEach { lib ->
                ZipFile(lib).use { zin ->
                    val remove = mutableListOf<String>()
                    toProcess.forEach toProcessEach@{ target ->
                        val entry = zin.getEntry("$target.class")
                            ?: return@toProcessEach

                        val node = ClassNode()
                        val reader = ClassReader(zin.getInputStream(entry))
                        reader.accept(node, 0)

                        //CompilationUnitResolver allows batch compiling, the problem is it is hardcoded to read the contents from a File.
                        //So we patch this call to redirect to us, so we can get the contents from our InputSupplier
                        if (COMPILATION_UNIT_RESOLVER.equals(target)) {
                            logger.lifecycle("Transforming: $target From: $lib")
                            val resolve = node.methods.find { RESOLVE_METHOD.equals(it.name + it.desc) }
                                ?: throw RuntimeException("Failed to patch $target: Could not find method $RESOLVE_METHOD")
                            for (x in 0 until resolve.instructions.size()) {
                                val insn = resolve.instructions.get(x)
                                if (insn.type == AbstractInsnNode.METHOD_INSN) {
                                    insn as MethodInsnNode
                                    if (GET_CONTENTS == "${insn.owner}.${insn.name}${insn.desc}") {
                                        if (
                                        resolve.instructions.get(x - 5).opcode == Opcodes.NEW &&
                                                resolve.instructions.get(x - 4).opcode == Opcodes.DUP &&
                                                resolve.instructions.get(x - 3).opcode == Opcodes.ALOAD &&
                                                resolve.instructions.get(x - 2).opcode == Opcodes.INVOKESPECIAL &&
                                                resolve.instructions.get(x - 1).opcode == Opcodes.ALOAD
                                        ) {
                                            resolve.instructions.set(resolve.instructions.get(x - 5), InsnNode(Opcodes.NOP)) // NEW File
                                            resolve.instructions.set(resolve.instructions.get(x - 4), InsnNode(Opcodes.NOP)) // DUP
                                            resolve.instructions.set(resolve.instructions.get(x - 2), InsnNode(Opcodes.NOP)) // INVOKESTATIC <init>
                                            insn.owner = RANGE_EXTRACTOR
                                            insn.desc = HOOK_DESC_RESOLVE
                                            logger.lifecycle("Patched ${node.name}")
                                        } else {
                                            throw IllegalStateException("Found Util.getFileCharContents call, with unexpected context")
                                        }
                                    }
                                }
                            }
                        } else if (RANGE_EXTRACTOR == target) {
                            logger.lifecycle("Tansforming: $target From: $lib")
                            val marker = node.methods.find { "hasBeenASMPatched()Z" == it.name + it.desc }
                                ?: throw RuntimeException("Failed to patch $target: Could not find method hasBeenASMPatched()Z")
                            marker.instructions.clear()
                            marker.instructions.add(InsnNode(Opcodes.ICONST_1))
                            marker.instructions.add(InsnNode(Opcodes.IRETURN))
                            logger.lifecycle("Patched: ${node.name}")
                        }

                        val writer = ClassWriter(0)
                        node.accept(writer)

                        remove.add(target)
                        val nentry = ZipEntry(entry.name)
                        nentry.time = 0
                        zout.putNextEntry(nentry)
                        zout.write(writer.toByteArray())
                        zout.closeEntry()
                    }
                    toProcess.removeAll(remove)
                }
            }
            if (toProcess.isNotEmpty())
                throw IllegalStateException("Patching class failed: $toProcess")
        }
    }
}

val patchJDT by tasks.creating(PatchJDTClasses::class) {
    target(PatchJDTClasses.COMPILATION_UNIT_RESOLVER)
    target(PatchJDTClasses.RANGE_EXTRACTOR)
    shade.resolvedConfiguration.resolvedArtifacts.filter { dep ->
        dep.name == "org.eclipse.jdt.core" || dep.name == "Srg2Source"
    }.forEach { dep -> library(dep.file) }
    output = file("build/patchJDT/patch_jdt.jar")
}

val jar by tasks.getting(Jar::class) {
    dependsOn("patchJDT")

    shade.forEach { dep ->
        /* I can use this again to find where dupes come from, so.. gunna just keep it here.
        logger.lifecycle(dep.toString())
        project.zipTree(dep).visit {
            element ->
                def path = element.relativePath.toString()
                if (path.contains("org/eclipse/core") && path.endsWith(".class"))
                    println "  $element.relativePath"

        }
        */
        from(project.zipTree(dep)) {
            exclude("META-INF",
                "META-INF/**",
                ".api_description",
                ".options",
                "about.html",
                "module-info.class",
                "plugin.properties",
                "plugin.xml",
                "about_files/**")
            duplicatesStrategy = DuplicatesStrategy.WARN
        }
    }

    from(zipTree(patchJDT.output)) {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    @Suppress("UnstableApiUsage")
    manifest {
        attributes(mapOf(
            "version" to project.version,
            "javaCompliance" to project.java.targetCompatibility,
            "group" to project.group,
            "Implementation-Version" to "${project.version}${getGitHash()}"
        ))
    }
}

val javadoc by tasks.getting(Javadoc::class) {

    // linked javadoc urls.. why not...

    val options = options as StandardJavadocDocletOptions
    options.links("https://gradle.org/docs/current/javadoc/")
    options.links("http://docs.guava-libraries.googlecode.com/git-history/v18.0/javadoc")
    options.links("http://asm.ow2.org/asm50/javadoc/user/")
}

@Suppress("UnstableApiUsage")
java {
    withJavadocJar()
    withSourcesJar()
}

artifacts {
    archives(jar)
    //archives javadocJar
}

val test by tasks.getting(Test::class) {
    if (project.hasProperty("filesmaven")) // disable this test when on the forge jenkins
        exclude("**/ExtensionMcpMappingTest*")
}

fun Project.license(configure: nl.javadude.gradle.plugins.license.LicenseExtension.() -> Unit): Unit =
    (this as ExtensionAware).extensions.configure("license", configure)
fun nl.javadude.gradle.plugins.license.LicenseExtension.ext(configure: ExtraPropertiesExtension.()->Unit): Unit =
    (this as ExtensionAware).extensions.configure("ext", configure)

license {
    ext {
        this["description"] = "A Gradle plugin for the creation of Minecraft mods and MinecraftForge plugins."
        this["year"] = "2020-" + Calendar.getInstance().get(Calendar.YEAR)
        this["fullname"] = "anatawa12 and other contributors"
    }
    header = rootProject.file("HEADER")
    include("**net/minecraftforge/gradle/**/*.java")
    excludes (listOf(
        "**net/minecraftforge/gradle/util/ZipFileTree.java",
        "**net/minecraftforge/gradle/util/json/version/*",
        "**net/minecraftforge/gradle/util/patching/Base64.java",
        "**net/minecraftforge/gradle/util/patching/ContextualPatch.java",
        "**net/minecraftforge/gradle/ArchiveTaskHelper.java",
        "**net/minecraftforge/gradle/GradleVersionUtils.java"
    ))
    ignoreFailures = false
    strictCheck = true
    mapping(mapOf(
        "java" to "SLASHSTAR_STYLE"
    ))
}

publishing {
    publications {
        val bintray by creating(MavenPublication::class) {
            from(components["java"])

            pom {
                name.set(project.base.archivesBaseName)
                description.set("Gradle plugin for Forge")
                url.set("https://github.com/anatawa12/ForgeGradle-2.3")

                scm {
                    url.set("https://github.com/anatawa12/ForgeGradle-2.3")
                    connection.set("scm:git:git://github.com/anatawa12/ForgeGradle-2.3.git")
                    developerConnection.set("scm:git:git@github.com:anatawa12/ForgeGradle-2.3.git")
                }

                issueManagement {
                    system.set("github")
                    url.set("https://github.com/anatawa12/ForgeGradle-2.3/issues")
                }

                licenses {
                    license {
                        name.set("Lesser GNU Public License, Version 2.1")
                        url.set("https://www.gnu.org/licenses/lgpl-2.1.html")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("AbrarSyed")
                        name.set("Abrar Syed")
                        roles.set(setOf("developer"))
                    }

                    developer {
                        id.set("LexManos")
                        name.set("Lex Manos")
                        roles.set(setOf("developer"))
                    }

                    developer {
                        id.set("anatawa12")
                        name.set("anatawa12")
                        roles.set(setOf("developer"))
                    }
                }
            }
        }
    }
}

// write out version so its convenient for doc deployment
file("build").mkdirs()
file("build/version.txt").writeText("$version")

fun getGitHash(): String {
    val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD").start()
    process.waitFor()
    return "-" + (if (process.exitValue() != 0) "unknown" else process.inputStream.reader().use { it.readText() }.trim())
}

gradlePlugin {
    isAutomatedPublishing = false
}

val dependenciesJava8CompatibilityCheck by tasks.creating {
    doLast {
        if (System.getenv("CHECK_JDK_COMPATIBILITY")?.toBoolean() == true) {
            shade.asSequence().forEach {
                val reading = ByteArray(8)
                val zis = ZipInputStream(it.inputStream())
                while (true) {
                    val entry = zis.nextEntry ?: break
                    if (!entry.name.endsWith(".class")) continue
                    if (entry.name == "module-info.class") continue
                    if (entry.name.startsWith("META-INF/")) continue
                    if (zis.read(reading) != reading.size) continue
                    if (reading[0] == 0xCA.toByte() &&
                        reading[1] == 0xFE.toByte() &&
                        reading[2] == 0xBA.toByte() &&
                        reading[3] == 0xBE.toByte() &&
                        reading[4] == 0x00.toByte() &&
                        reading[5] == 0x00.toByte()) {
                        @Suppress("OPT_IN_USAGE")
                        val major = reading[6].toUByte().toInt().shl(8) or reading[7].toUByte().toInt()
                        if (major > 52)
                            throw IllegalStateException("${entry.name} of $it is not compatible with java 8: class ${entry.name}")
                    }
                }
            }
        }
    }
}

tasks.check.get().dependsOn(dependenciesJava8CompatibilityCheck)
