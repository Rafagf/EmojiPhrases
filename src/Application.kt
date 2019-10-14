package com.rafag

import com.rafag.api.*
import com.rafag.model.*
import com.rafag.repository.*
import com.rafag.webapp.*
import com.ryanharter.ktor.moshi.*
import freemarker.cache.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import webapp.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    installFeatures()
    val hashFunction = { s: String -> hash(s) }
    DatabaseFactory.init()
    val repository = EmojiPhraseRepository()
    routing {
        static("/static") {
            resources("images")
        }
        home()
        about()
        phrases(repository)
        signIn(repository, hashFunction)
        signOut()
        signUp(repository, hashFunction)

        //Api
        phrase(repository)
    }
}

private fun Application.installFeatures() {
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
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
}

const val API_VERSION = "/api/v1"

suspend fun ApplicationCall.redirect(location: Any) {
    respondRedirect(application.locations.href(location))
}