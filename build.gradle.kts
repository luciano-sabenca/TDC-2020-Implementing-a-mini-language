plugins {
    java
    kotlin("jvm") version "1.3.72"
    id("antlr")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    antlr("org.antlr:antlr4:4.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

sourceSets {
    create("antlr-generated") {
        compileClasspath += fileTree("build/generated-src/antlr")
        runtimeClasspath += fileTree("build/generated-src/antlr")
    }
}

tasks {
    compileKotlin {
        dependsOn("generateGrammarSource")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.generateGrammarSource {
    maxHeapSize = "128m"
    arguments = arguments + listOf("-visitor")
}