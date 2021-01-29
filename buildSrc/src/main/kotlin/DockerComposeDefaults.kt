object DockerComposeDefaults {

    val environomentVariables : Map<String, String> = mutableMapOf(
        "CORS_ALLOWED_ORIGINS" to "localhost:9002",

        "OIDC_ISSUER" to "http://localhost:9000",
        "OIDC_DISCOVERY_URL" to "http://localhost:9000/.well-known/openid-configuration",
        "OIDC_ACCEPTED_AUDIENCE" to "stubOidcClient",
        "LOGINSERVICE_IDPORTEN_DISCOVERY_URL" to "http://localhost:9000/.well-known/openid-configuration",
        "LOGINSERVICE_IDPORTEN_AUDIENCE" to "stubOidcClient",
        "OIDC_CLAIM_CONTAINING_THE_IDENTITY" to "pid",

        "NAIS_CLUSTER_NAME" to "dev-sbs",
        "NAIS_NAMESPACE" to "q1",
        "SENSU_HOST" to "stub",
        "SENSU_PORT" to "",
        "PRODUCER_ALIASES" to ""
    )

}
