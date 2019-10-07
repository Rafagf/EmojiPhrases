package com.rafag.api

import com.rafag.*
import com.rafag.model.*
import com.rafag.repository.*
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASE = "phrase"
const val PHRASE_ENDPOINT = "$API_VERSION/$PHRASE"

fun Route.phrase(repository: Repository) {
    post(PHRASE_ENDPOINT) {
        val request = call.receive<Request>()
        val phrase = repository.add(EmojiPhrase(request.emoji, request.phrase))
        call.respond(phrase)
    }
}