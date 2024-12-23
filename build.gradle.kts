import com.expediagroup.graphql.plugin.gradle.graphql
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm").version(Kotlin.version)

    id(GraphQL.pluginId) version GraphQL.version
    id(Shadow.pluginId) version Shadow.version

    // Apply the application plugin to add support for building a CLI application.
    application
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
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
    implementation(Caffeine.caffeine)
    implementation(GraphQL.kotlinClient)
    implementation(GraphQL.kotlinKtorClient)
    implementation(JacksonDatatype.datatypeJsr310)
    implementation(Kotlinx.coroutines)
    implementation(KotlinLogging.logging)
    implementation(KtorClientLogging.logging)
    implementation(Ktor.Server.auth)
    implementation(Ktor.Server.authJwt)
    implementation(Ktor.Client.apache)
    implementation(Ktor.Client.contentNegotiation)
    implementation(Ktor.Serialization.jackson)
    implementation(Ktor.Server.contentNegotiation)
    implementation(Ktor.Server.cors)
    implementation(Ktor.Server.defaultHeaders)
    implementation(Ktor.Server.htmlDsl)
    implementation(Ktor.Server.netty)
    implementation(Ktor.Server.statusPages)
    implementation(Logstash.logbackEncoder)
    implementation(Prometheus.metricsCore)
    implementation(Lettuce.core)
    implementation(TmsCommonLib.metrics)
    implementation(TmsCommonLib.utils)
    implementation(TmsCommonLib.observability)
    implementation(TmsKtorTokenSupport.idportenSidecar)
    implementation(TmsKtorTokenSupport.tokendingsExchange)

    testImplementation(Junit.api)
    testImplementation(Ktor.Test.clientMock)
    testImplementation(Ktor.Test.serverTestHost)
    testImplementation(TmsKtorTokenSupport.idportenSidecarMock)
    testImplementation(Kotest.runnerJunit5)
    testImplementation(Kotest.assertionsCore)
    testImplementation(Mockk.mockk)
    testImplementation(TmsCommonLib.testutils)

    testRuntimeOnly(Jjwt.impl)
    testRuntimeOnly(Jjwt.jackson)
    testRuntimeOnly(Junit.engine)
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
