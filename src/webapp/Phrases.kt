package com.rafag.webapp

import com.rafag.repository.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASES = "phrases"

fun Route.phrases(repository: Repository) {
    get(PHRASES) {
        val phrases = repository.phrases()
        call.respond(FreeMarkerContent("phrases.ftl", mapOf("phrases" to phrases)))
    }
}
