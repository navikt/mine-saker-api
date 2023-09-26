import default.*

object Caffeine: DependencyGroup {
    override val groupId = "com.github.ben-manes.caffeine"
    override val version = "3.1.8"

    val caffeine = dependency("caffeine")
}

object GraphQL: DependencyGroup {
    override val groupId get() = "com.expediagroup"
    override val version = "6.3.5"

    val pluginId get() = "com.expediagroup.graphql"

    val kotlinClient get() = dependency("graphql-kotlin-client")
    val kotlinKtorClient get() = dependency("graphql-kotlin-ktor-client")
}

object KtorClientLogging: KtorDefaults.ClientDefaults {
    val logging = dependency("ktor-client-logging")
}

object Lettuce: DependencyGroup {
    override val groupId get() = "io.lettuce"
    override val version get() = "6.2.6.RELEASE"

    val core = dependency("lettuce-core")
}

object Nimbusds: DependencyGroup {
    override val version = "9.19"
    override val groupId = "com.nimbusds"

    val joseJwt = dependency("nimbus-jose-jwt")
    val oauth2OidcSdk =  dependency("oauth2-oidc-sdk")
}
