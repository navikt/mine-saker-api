import default.GraphQLDefaults

object KtorTokenSupport201: default.TmsKtorTokenSupportDefaults {
    override val version = "2.0.1"
}

object GraphQL6: GraphQLDefaults {
    override val version = "6.3.5"
}

object KtorClientLogging: default.Ktor2Defaults.ClientDefaults {
    val logging = dependency("ktor-client-logging")
}
