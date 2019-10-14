package com.rafag.webapp

import com.rafag.*
import com.rafag.model.*
import com.rafag.repository.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val PHRASES = "phrases"

@Location(PHRASES)
class Phrases

fun Route.phrases(db: Repository, hashFunction: (String) -> String) {
    get<Phrases> {
        val user = call.sessions.get<EpSession>()?.let { db.user(it.userId) }

        if (user == null) {
            call.redirect(SignIn())
        } else {
            val phrases = db.phrases(user.userId)
            val date = System.currentTimeMillis()
            val code = call.securityCode(date, user, hashFunction)

            call.respond(FreeMarkerContent("phrases.ftl", mapOf("phrases" to phrases, "user" to user, "date" to date, "code" to code), user.userId))

        }
    }

    post<Phrases> {
        val user = call.sessions.get<EpSession>()?.let { db.user(it.userId) }
        val params = call.receiveParameters()
        val date = params["date"]?.toLongOrNull() ?: return@post call.redirect(it)
        val code = params["code"] ?: return@post call.redirect(it)
        val action = params["action"] ?: throw IllegalArgumentException("Missing action")

        if (user == null || !call.verifyCode(date, user, code, hashFunction)) {
            call.redirect(SignIn())
        }

        when (action) {
            "delete" -> {
                val id = params["id"] ?: throw java.lang.IllegalArgumentException("Missing id")
                db.remove(id)
            }
            "add" -> {
                val emoji = params["emoji"] ?: throw IllegalArgumentException("Missing argument: emoji")
                val phrase = params["phrase"] ?: throw IllegalArgumentException("Missing argument: phrase")
                db.add(user!!.userId, emoji, phrase)
            }
        }

        call.redirect(Phrases())
    }

}
