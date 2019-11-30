plugins {
    kotlin("jvm")
    application
}

group = "woggioni.net"
version = "0.1"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile(rootProject)
//    runtime(files(rootProject.projectDir.toPath().resolve("src/test/resources")))
}

application {
    mainClassName = "net.woggioni.klevtree.benchmark.BenchmarkKt"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
