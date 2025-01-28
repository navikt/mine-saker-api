package no.nav.tms.minesaker.api.fullmakt

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.util.*
import no.nav.tms.token.support.idporten.sidecar.IdPortenTokenPrincipal

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

class FullmaktSessions(val config: FullmaktConfig) {

    companion object : BaseApplicationPlugin<Application, FullmaktConfig, FullmaktSessions> {

        override val key: AttributeKey<FullmaktSessions> = AttributeKey("FullmaktHolder")
        override fun install(pipeline: Application, configure: FullmaktConfig.() -> Unit): FullmaktSessions {
            requireNotNull(pipeline.pluginOrNull(Authentication)) { "Fullmaktinterceptor must be installed after Authentication" }
            val config = FullmaktConfig().apply(configure)
            return FullmaktSessions(config)
        }
    }
}

val FullmaktAttribute = AttributeKey<FullmaktGiver>("fullmakt_attribute")

private val FullmaktInterceptor = createRouteScopedPlugin(name = "fullmakt-interceptor") {
    val config = application.plugin(FullmaktSessions).config
    val redisService = config.sessionStore

    val log = KotlinLogging.logger { }
    val secureLog = KotlinLogging.logger("secureLog")

    on(AuthenticationChecked) { call ->

        val principal = call.principal<IdPortenTokenPrincipal>()

        if (principal != null) {

            try {
                redisService.getCurrentFullmaktGiver(principal.ident())
                    ?.let { fullmaktGiver -> call.attributes.put(FullmaktAttribute, fullmaktGiver) }
            } catch (e: Exception) {
                log.warn { "Feil mot fullmakt-sessionstore." }
                secureLog.warn(e) { "Feil mot fullmakt-sessionstore." }
            }
        }
    }
}

private class FullmaktRouteSelector : RouteSelector() {
    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Transparent
    }

    override fun toString() = "" // Unngå at den havner som et ledd i api-metrikker
}
