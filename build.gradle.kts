
plugins {
    kotlin("jvm") version "1.3.41"
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
    compile("net.woggioni:jwo:1.0")
    testImplementation  ("junit:junit:4.12")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.3.41")
    testImplementation("org.apache.logging.log4j:log4j-core:2.12.1")
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.12.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


