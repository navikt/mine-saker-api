import default.*

object Caffeine: DependencyGroup {
    override val groupId = "com.github.ben-manes.caffeine"
    override val version = "3.2.3"

    val caffeine = dependency("caffeine")
}

object GraphQL: DependencyGroup {
    override val groupId get() = "com.expediagroup"
    override val version = "9.2.0"

    val pluginId get() = "com.expediagroup.graphql"

    val kotlinClient get() = dependency("graphql-kotlin-client")
    val kotlinKtorClient get() = dependency("graphql-kotlin-ktor-client")
}

object KtorClientLogging: KtorDefaults.ClientDefaults {
    val logging = dependency("ktor-client-logging")
}

object Valkey: DependencyGroup {
    override val groupId get() = "io.valkey"
    override val version get() = "5.5.0"

    val java = dependency("valkey-java")
}
