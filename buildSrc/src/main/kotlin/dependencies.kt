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

object Valkey: DependencyGroup {
    override val groupId get() = "io.valkey"
    override val version get() = "5.3.0"

    val java = dependency("valkey-java")
}

object TokenSupport6: DependencyGroup {
    override val groupId = "no.nav.tms.token.support"
    override val version = "6.0.0-alpha-1"


    val entraIdTokenVerification get() = dependency("entra-id-token-verification")
    val entraIdTokenVerificationMock get() = dependency("entra-id-token-verification-mock")
    val entraIdTokenFetcher get() = dependency("entra-id-token-fetcher")
    val userLoginRoutes get() = dependency("user-login-routes")
    val userTokenVerification get() = dependency("user-token-verification")
    val userTokenVerificationMock get() = dependency("user-token-verification-mock")
    val userTokenExchange get() = dependency("user-token-exchange")
}
