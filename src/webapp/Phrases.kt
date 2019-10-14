package com.rafag.webapp

import com.rafag.*
import com.rafag.model.*
import com.rafag.repository.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASES = "phrases"

@Location(PHRASES)
class Phrases

fun Route.phrases(repository: Repository) {
    get<Phrases> {
        val user = call.authentication.principal as User
        val phrases = repository.phrases()
        call.respond(
            FreeMarkerContent(
                "phrases.ftl",
                mapOf("phrases" to phrases, "displayName" to user.displayName)
            )
        )
    }

    post<Phrases> {
        val params = call.receiveParameters()
        val action = params["action"] ?: throw IllegalArgumentException("Missing action")
        when (action) {
            "delete" -> {
                val id = params["id"] ?: throw java.lang.IllegalArgumentException("Missing id")
                repository.remove(id)
            }
            "add" -> {
                val emoji = params["emoji"] ?: throw IllegalArgumentException("Missing argument: emoji")
                val phrase = params["phrase"] ?: throw IllegalArgumentException("Missing argument: phrase")
                repository.add("", emoji, phrase)
            }
        }

        call.redirect(Phrases())
    }

}
