package no.nav.personbruker.minesaker.api.saf.fullmakt

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.util.*
import no.nav.tms.token.support.idporten.sidecar.authentication.IdPortenTokenPrincipal

fun Route.enableFullmakt(
    build: Route.() -> Unit
) {
    val authenticatedRoute = createChild(FullmaktRouteSelector())

    authenticatedRoute.install(FullmaktInterceptor)
    authenticatedRoute.build()
}

class FullmaktConfig {
    lateinit var sessionStore: FullmaktSessionStore
}

class Fullmakt(val config: FullmaktConfig) {

    companion object : BaseApplicationPlugin<Application, FullmaktConfig, Fullmakt> {

        override val key: AttributeKey<Fullmakt> = AttributeKey("FullmaktHolder")
        override fun install(pipeline: Application, configure: FullmaktConfig.() -> Unit): Fullmakt {
            requireNotNull(pipeline.pluginOrNull(Authentication)) { "Fullmaktinterceptor must be installed after Authentication" }
            val config = FullmaktConfig().apply(configure)
            return Fullmakt(config)
        }
    }
}

val FullmaktAttribute = AttributeKey<FullmaktGiver>("fullmakt_attribute")

private val FullmaktInterceptor = createRouteScopedPlugin(name = "fullmakt-interceptor") {
    val config = application.plugin(Fullmakt).config
    val redisService = config.sessionStore
    val log = KotlinLogging.logger { }

    on(AuthenticationChecked) { call ->

        val principal = call.principal<IdPortenTokenPrincipal>()

        if (principal != null) {

            val ident = principal.ident

            try {
                redisService.getCurrentFullmaktGiver(ident)?.let { fullmaktGiver ->
                    call.attributes.put(FullmaktAttribute, fullmaktGiver)
                }
            } catch (e: Exception) {
                log.warn(e) { "Fullmektig-feil" }
            }
        }
    }
}

private val IdPortenTokenPrincipal.ident get() = accessToken.getClaim("pid").asString()

private class FullmaktRouteSelector : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Transparent
    }
}
