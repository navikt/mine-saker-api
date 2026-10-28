import com.expediagroup.graphql.plugin.gradle.graphql
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm").version(managed.versions.kotlin)

    alias(libs.plugins.graphql)

    alias(managed.plugins.tms.jarBundling)

    application
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
    mavenLocal()
}

dependencies {
    implementation(libs.caffeine)
    implementation(libs.graphql.client)
    implementation(libs.graphql.ktorClient)
    implementation(libs.valkey)

    implementation(managed.bundles.jackson)
    implementation(managed.bundles.ktorServer)
    implementation(managed.bundles.ktorClient)

    implementation(managed.kotlinx.coroutines)
    implementation(managed.kotlinLogging)
    implementation(managed.ktor.serialization.jackson)
    implementation(managed.ktor.server.cors)
    implementation(managed.ktor.server.defaultHeaders)
    implementation(managed.prometheus.metric.core)
    implementation(managed.tms.commonLib.metrics)
    implementation(managed.tms.commonLib.utils)
    implementation(managed.tms.commonLib.observability)
    implementation(managed.tms.commonLib.teamLogger)
    implementation(managed.tms.ktorTokenSupport.userToken.exchange)
    implementation(managed.tms.ktorTokenSupport.userToken.loginRoutes)
    implementation(managed.tms.ktorTokenSupport.userToken.verification)
    implementation(managed.tms.ktorTokenSupport.entraId.fetcher)

    testImplementation(managed.bundles.junit)
    testImplementation(managed.bundles.kotest)

    testImplementation(managed.ktor.test.clientMock)
    testImplementation(managed.ktor.test.server)
    testImplementation(managed.mockk)
    testImplementation(managed.tms.ktorTokenSupport.userToken.verificationMock)
}

application {
    mainClass.set("no.nav.tms.minesaker.api.AppKt")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            events("passed", "skipped", "failed")
        }
    }
}

graphql {
    client {
        sdlEndpoint = "https://navikt.github.io/safselvbetjening/schema.graphqls"
        packageName = "no.nav.dokument.saf.selvbetjening.generated.dto"
        queryFileDirectory = "${project.projectDir.absolutePath}/src/main/resources"
    }
}
