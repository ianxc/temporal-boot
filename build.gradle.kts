plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.spring)
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.ianxc"

version = "0.0.1-SNAPSHOT"

java { toolchain { languageVersion = JavaLanguageVersion.of(17) } }

configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }

repositories { mavenCentral() }

dependencies {
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.logstash.logback.encoder)
    implementation(libs.micrometer.otel)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.temporal.kotlin)
    implementation(libs.temporal.spring.boot.starter)

    developmentOnly(libs.spring.boot.devtools)

    annotationProcessor(libs.spring.boot.configuration.processor)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(platform(libs.strikt.bom))
    testImplementation(libs.strikt.jackson)
    testImplementation(libs.strikt.jvm)
    testImplementation(libs.strikt.spring)

    testRuntimeOnly(libs.junit.platform.launcher)

    constraints {
        implementation(libs.guava) {
            because("version pulled from temporal-spring-boot-starter includes outdated version")
        }
    }
}

kotlin { compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") } }

tasks.withType<Test> { useJUnitPlatform() }

ktfmt {
    kotlinLangStyle()
    maxWidth = 100
}
