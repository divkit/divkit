plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.allopen)
}

apply(from = "../publish-java.gradle")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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

tasks.register("test${project.property("testVariant").toString().replaceFirstChar { it.uppercase() }}UnitTest") {
    dependsOn("test")
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

allOpen {
    annotation("com.yandex.div.evaluable.internal.Mockable")
}

dependencies {
    implementation(libs.javax)
    compileOnly(libs.json)

    "generatorCompileOnly"(libs.json)
    "generatorImplementation"(libs.kotlinpoet)

    testImplementation(libs.json)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
}
