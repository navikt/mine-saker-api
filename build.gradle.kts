import com.expediagroup.graphql.plugin.gradle.graphql
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm").version(Kotlin.version)

    id(GraphQL.pluginId) version GraphQL.version

    id(TmsJarBundling.plugin)

    application
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.github.com/navikt/*") {
        credentials {
            username = System.getenv("GITHUB_ACTOR")?: "x-access-token"
            password = System.getenv("GITHUB_TOKEN")?: project.findProperty("githubPassword") as String
        }
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
    implementation(Ktor.Server.netty)
    implementation(Ktor.Server.statusPages)
    implementation(Logstash.logbackEncoder)
    implementation(Prometheus.metricsCore)
    implementation(Valkey.java)
    implementation(TmsCommonLib.metrics)
    implementation(TmsCommonLib.utils)
    implementation(TmsCommonLib.observability)
    implementation(TmsCommonLib.teamLogger)
    implementation(TokenSupport6.userLoginRoutes)
    implementation(TokenSupport6.userTokenVerification)
    implementation(TokenSupport6.userTokenExchange)

    testImplementation(JunitPlatform.launcher)
    testImplementation(JunitJupiter.api)
    testImplementation(Ktor.Test.clientMock)
    testImplementation(Ktor.Test.serverTestHost)
    testImplementation(TokenSupport6.userTokenVerificationMock)
    testImplementation(Kotest.runnerJunit5)
    testImplementation(Kotest.assertionsCore)
    testImplementation(Mockk.mockk)

    testRuntimeOnly(Jjwt.impl)
    testRuntimeOnly(Jjwt.jackson)
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
