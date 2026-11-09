plugins {
    id("divkit.convention.library-kmp")
    id("divkit.convention.publishing-module-kmp")
}

val generateFunctionProvider = tasks.register<JavaExec>("generateFunctionProvider") {
    val generatorCompilation = kotlin.jvm().compilations.getByName("generator")
    val outputDir = layout.buildDirectory.dir("generated/functionProvider/commonMain/kotlin")
    classpath = generatorCompilation.output.allOutputs + generatorCompilation.runtimeDependencyFiles
    mainClass.set("com.yandex.div.evaluable.function.BuiltinFunctionProviderGenerator")
    workingDir(outputDir)
    outputs.dir(outputDir)
    outputs.cacheIf { true }

    doFirst {
        outputDir.get().asFile.run {
            deleteRecursively()
            mkdirs()
        }
    }
}

kotlin {
    android {
        namespace = "com.yandex.div.evaluable"
    }

    jvm {
        compilations.create("generator") {
            defaultSourceSet {
                kotlin.srcDir("src/commonMain/kotlin")
                dependencies {
                    implementation(project(":core"))
                    implementation(libs.kotlinpoet)
                }
            }
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(generateFunctionProvider)
            dependencies {
                api(project(":core"))
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.mockito.kotlin)
        }
    }
}

tasks.named("metalavaGenerateSignature") {
    dependsOn(generateFunctionProvider)
}

tasks.named("metalavaCheckCompatibility") {
    dependsOn(generateFunctionProvider)
}

tasks.register<Jar>("replJar") {
    val jvmCompilations = kotlin.jvm().compilations
    val runtimeClasspath = configurations.named("jvmRuntimeClasspath")
    dependsOn(runtimeClasspath)
    from(jvmCompilations.getByName("test").output.classesDirs)
    from(jvmCompilations.getByName("main").output.classesDirs)
    from({ runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) } })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes("Main-Class" to "com.yandex.div.evaluable.repl.EvaluableRepl")
    }
    archiveClassifier.set("repl")
}
