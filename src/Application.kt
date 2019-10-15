package com.rafag

import com.rafag.api.*
import com.rafag.model.*
import com.rafag.repository.*
import com.rafag.webapp.*
import com.ryanharter.ktor.moshi.*
import freemarker.cache.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import webapp.*
import java.net.*
import java.util.concurrent.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val hashFunction = { s: String -> hash(s) }
    DatabaseFactory.init()
    val repository = EmojiPhraseRepository()
    installFeatures(repository)
    routing {
        static("/static") {
            resources("images")
        }
        home(repository)
        about(repository)
        phrases(repository, hashFunction)
        signIn(repository, hashFunction)
        signOut()
        signUp(repository, hashFunction)

        //Api
        phrase(repository)
        login(repository, JwtService)
    }
}

private fun Application.installFeatures(db: Repository) {
    install(DefaultHeaders)
    install(StatusPages) {
        exception<Throwable> { exception ->
            call.respondText(
                exception.localizedMessage,
                ContentType.Text.Plain,
                HttpStatusCode.InternalServerError
            )
        }
    }
    install(ContentNegotiation) {
        moshi()
    }

    install(Locations)

    install(Sessions) {
        cookie<EpSession>("SESSION") {
            transform(SessionTransportTransformerMessageAuthentication(hashKey))
        }
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Authentication) {
        jwt("jwt") {
            verifier(JwtService.verifier)
            realm = "emojiphrases app"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asString()
                val user = db.userById(claimString)
                user
            }
        }
    }
}

const val API_VERSION = "/api/v1"

suspend fun ApplicationCall.redirect(location: Any) {
    respondRedirect(application.locations.href(location))
}

fun ApplicationCall.refererHost() = request.header(HttpHeaders.Referrer)?.let {
    URI.create(it).host
}

fun ApplicationCall.securityCode(date: Long, user: User, hashFunction: (String) -> String) =
    hashFunction("$date:${user.userId}:${request.host()}:${refererHost()}")

fun ApplicationCall.verifyCode(date: Long, user: User, code: String, hashFunction: (String) -> String) =
    securityCode(date, user, hashFunction) == code &&
            (System.currentTimeMillis() - date).let { it > 0 && it < TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS) }