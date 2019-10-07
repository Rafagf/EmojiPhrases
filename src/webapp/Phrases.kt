package com.rafag.webapp

import com.rafag.model.*
import com.rafag.repository.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASES = "phrases"

fun Route.phrases(repository: Repository) {
    authenticate("auth") {
        get(PHRASES) {
            val user = call.authentication.principal as User
            val phrases = repository.phrases()
            call.respond(
                FreeMarkerContent(
                    "phrases.ftl",
                    mapOf("phrases" to phrases, "displayName" to user.displayName)
                )
            )
        }
    }
}
