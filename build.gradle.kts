import com.expediagroup.graphql.plugin.gradle.graphql
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm").version(Kotlin.version)
    kotlin("plugin.allopen").version(Kotlin.version)
    kotlin("plugin.serialization").version(Kotlin.version)

    id(GraphQL.pluginId) version GraphQL6.version
    id(Shadow.pluginId) version "7.0.0"

    // Apply the application plugin to add support for building a CLI application.
    application
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    mavenLocal()
}

dependencies {
    implementation(DittNAVCommonLib.utils)
    implementation(GraphQL6.kotlinClient)
    implementation(JacksonDatatype.datatypeJsr310)
    implementation(Kotlinx.coroutines)
    implementation(Kotlinx.htmlJvm)
    implementation(Logback.classic)
    implementation(Logstash.logbackEncoder)
    implementation(KotlinLogging.logging)
    implementation(KtorClientLogging.logging)
    implementation(Ktor2.Server.auth)
    implementation(Ktor2.Server.authJwt)
    implementation(Ktor2.Client.apache)
    implementation(Ktor2.Client.contentNegotiation)
    implementation(Ktor2.Serialization.jackson)
    implementation(Ktor2.Server.contentNegotiation)
    implementation(Ktor2.Server.cors)
    implementation(Ktor2.Server.defaultHeaders)
    implementation(Ktor2.Server.htmlDsl)
    implementation(Ktor2.Server.netty)
    implementation(Ktor2.Server.statusPages)
    implementation(Prometheus.common)
    implementation(Prometheus.hotspot)
    implementation(Prometheus.logback)
    implementation(KtorTokenSupport201.idportenSidecar)
    implementation(KtorTokenSupport201.tokendingsExchange)

    testImplementation(Junit.api)
    testImplementation(Ktor2.Test.clientMock)
    testImplementation(Ktor2.Test.serverTestHost)
    testImplementation(TmsKtorTokenSupport.idportenSidecarMock)
    testImplementation(Kotest.runnerJunit5)
    testImplementation(Kotest.assertionsCore)
    testImplementation(Kotest.extensions)
    testImplementation(Mockk.mockk)
    testImplementation(Jjwt.api)

    testRuntimeOnly(Bouncycastle.bcprovJdk15on)
    testRuntimeOnly(Jjwt.impl)
    testRuntimeOnly(Jjwt.jackson)
    testRuntimeOnly(Junit.engine)
}

application {
    mainClass.set("no.nav.personbruker.minesaker.api.config.AppKt")
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
    }
}


apply(plugin = Shadow.pluginId)
