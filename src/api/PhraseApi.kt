package com.rafag.api

import com.rafag.*
import com.rafag.repository.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASE_API_ENDPOINT = "$API_VERSION/phrases"

@Location(PHRASE_API_ENDPOINT)
class PhrasesApi

fun Route.phrasesApi(db: Repository) {

    authenticate("jwt") {
        get<PhrasesApi> {
            call.respond(db.phrases())
        }
    }
}