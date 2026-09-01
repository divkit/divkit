plugins {
    alias(libs.plugins.kotlin.jvm)
    id("divkit.convention.abi-validation")
}

apply(from = "../publish-java.gradle")

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

val buildDir = layout.buildDirectory
val generatorOutputDir = buildDir.dir("generated/sources/java")

sourceSets {
    main {
        java {
            srcDir(generatorOutputDir)
        }
    }
    create("generator") {
        java {
            srcDir("src/main/java")
            srcDir("src/generator/java")
        }
    }
}

tasks.register<JavaExec>("generateFunctionProvider") {
    classpath = sourceSets["generator"].runtimeClasspath
    mainClass.set("com.yandex.div.evaluable.function.BuiltinFunctionProviderGenerator")
    workingDir(generatorOutputDir)
    outputs.dir(generatorOutputDir)
}

tasks.named("compileKotlin") {
    dependsOn("generateFunctionProvider")
}

tasks.named("sourcesJar") {
    dependsOn("generateFunctionProvider")
}

tasks.register<Jar>("replJar") {
    dependsOn("testClasses")
    from(buildDir.dir("classes/kotlin/test"))
    from(buildDir.dir("classes/kotlin/main"))
    from({
        configurations.named("runtimeClasspath").get().map { if (it.isDirectory) it else zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes("Main-Class" to "com.yandex.div.evaluable.repl.EvaluableRepl")
    }
    archiveClassifier.set("repl")
}

dependencies {
    implementation(libs.javax)
    compileOnly(libs.json)

    "generatorCompileOnly"(libs.json)
    "generatorImplementation"(libs.kotlinpoet)

    testImplementation(libs.json)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockito.kotlin)
}
