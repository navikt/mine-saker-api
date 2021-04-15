/**
 * Anbefalte versjoner av tredjepartsbiblioteker.
 */

object Auth0 {
    private const val version = "3.11.0"
    const val javajwt = "com.auth0:java-jwt:$version"
}

object Bouncycastle {
    private const val version = "1.66"
    const val bcprovJdk15on = "org.bouncycastle:bcprov-jdk15on:$version"
}

object GraphQL {
    const val version = "3.7.0"
    private const val groupId = "com.expediagroup"
    const val pluginId = "com.expediagroup.graphql"

    const val client = "$groupId:graphql-kotlin-client:$version"
}

object Influxdb {
    private const val version = "2.20"
    const val java = "org.influxdb:influxdb-java:$version"
}

object Jackson {
    private const val version = "2.12.1"
    const val dataTypeJsr310 = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$version"
    const val moduleKotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:$version"
}

object Junit {
    private const val version = "5.4.1"
    private const val groupId = "org.junit.jupiter"

    const val api = "$groupId:junit-jupiter-api:$version"
    const val engine = "$groupId:junit-jupiter-engine:$version"
    const val params = "$groupId:junit-jupiter-params:$version"
}

object Jjwt {
    private const val version = "0.11.2"
    private const val groupId = "io.jsonwebtoken"

    const val api = "$groupId:jjwt-api:$version"
    const val impl = "$groupId:jjwt-impl:$version"
    const val jackson = "$groupId:jjwt-jackson:$version"
}

object Kluent {
    private const val version = "1.61"
    const val kluent = "org.amshove.kluent:kluent:$version"
}

object Kotlin {
    const val version = "1.4.31"
    private const val groupId = "org.jetbrains.kotlin"

    const val reflect = "$groupId:kotlin-reflect:$version"
}

object Kotlinx {
    private const val groupId = "org.jetbrains.kotlinx"

    const val coroutines = "$groupId:kotlinx-coroutines-core:1.4.2"
    const val htmlJvm = "$groupId:kotlinx-html-jvm:0.7.2"
    const val atomicfu = "$groupId:atomicfu:0.14.4"
}

object Ktor {
    const val version = "1.5.1"
    private const val groupId = "io.ktor"

    const val auth = "$groupId:ktor-auth:$version"
    const val authJwt = "$groupId:ktor-auth-jwt:$version"
    const val htmlBuilder = "$groupId:ktor-html-builder:$version"
    const val http = "$groupId:ktor-http-jvm:$version"
    const val jackson = "$groupId:ktor-jackson:$version"
    const val serverNetty = "$groupId:ktor-server-netty:$version"
    const val clientApache = "$groupId:ktor-client-apache:$version"
    const val clientCio = "$groupId:ktor-client-cio:$version"
    const val clientCore = "$groupId:ktor-client-core:$version"
    const val clientJson = "$groupId:ktor-client-json:$version"
    const val clientSerializationJvm = "$groupId:ktor-client-serialization-jvm:$version"
    const val clientJackson = "$groupId:ktor-client-jackson:$version"
    const val clientLogging = "$groupId:ktor-client-logging:$version"
    const val clientLoggingJvm = "$groupId:ktor-client-logging-jvm:$version"
    const val clientMock = "$groupId:ktor-client-mock:$version"
    const val clientMockJvm = "$groupId:ktor-client-mock-jvm:$version"
    const val serverTestHost = "$groupId:ktor-server-test-host:$version"
}

object Logback {
    private const val version = "1.2.3"
    const val classic = "ch.qos.logback:logback-classic:$version"
}

object Logstash {
    private const val version = "6.4"
    const val logbackEncoder = "net.logstash.logback:logstash-logback-encoder:$version"
}

object Mockk {
    private const val version = "1.10.5"
    const val mockk = "io.mockk:mockk:$version"
}

object NAV {
    const val vaultJdbc = "no.nav:vault-jdbc:1.3.7"
    const val kafkaEmbedded = "no.nav:kafka-embedded-env:2.5.0"
    const val tokenValidatorKtor = "no.nav.security:token-validation-ktor:1.3.0"
    const val customKtorCorsFeature = "com.github.navikt:wildcard-subdomain-ktor-cors-feature:2020.11.03-14.59-81af587291fd"
}

object Postgresql {
    private const val version = "42.2.16"
    const val postgresql = "org.postgresql:postgresql:$version"
}

object Prometheus {
    private const val version = "0.9.0"
    private const val groupId = "io.prometheus"

    const val common = "$groupId:simpleclient_common:$version"
    const val hotspot = "$groupId:simpleclient_hotspot:$version"
    const val httpServer = "$groupId:simpleclient_httpserver:$version"
    const val logback = "$groupId:simpleclient_logback:$version"
    const val simpleClient = "$groupId:simpleclient:$version"
}

object Shadow {
    const val version = "6.1.0"
    const val pluginId = "com.github.johnrengelman.shadow"
}

object TestContainers {
    private const val version = "1.15.0-rc2"
    private const val groupId = "org.testcontainers"

    const val junitJupiter = "$groupId:junit-jupiter:$version"
    const val testContainers = "$groupId:testcontainers:$version"
}

object Tms {
    object KtorTokenSupport {
        private const val version = "2021.04.14-08.19-d6139a0cb38a"
        private const val groupId = "com.github.navikt.tms-ktor-token-support"

        const val idporten = "$groupId:token-support-idporten:$version"
        const val tokendingsExchange = "$groupId:token-support-tokendings-exchange:$version"
    }
}

object Unleash {
    private const val version = "3.3.1"
    private const val groupId = "no.finn.unleash"

    const val clientJava = "$groupId:unleash-client-java:$version"
}

object Awaitility {
    private const val version = "4.0.3"
    private const val groupId = "org.awaitility"

    const val awaitilityKotlin = "$groupId:awaitility-kotlin:$version"
}
