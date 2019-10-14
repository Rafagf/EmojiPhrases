package com.rafag.webapp

import com.rafag.*
import com.rafag.model.*
import com.rafag.repository.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import webapp.*

const val SIGNIN = "/signin"

@Location(SIGNIN)
data class SignIn(val userId: String = "", val error: String = "")

fun Route.signIn(db: Repository, hashFunction: (String) -> String) {

    post<SignIn> {
        val signInParameters = call.receive<Parameters>()
        val userId = signInParameters["userId"] ?: return@post call.redirect(it)
        val password = signInParameters["password"] ?: return@post call.redirect(it)

        val signInError = SignIn(userId)

        val signin = when {
            userId.length < MIN_USER_ID_LENGTH -> null
            password.length < MIN_PASSWORD_LENGTH -> null
            !userNameValid(userId) -> null
            else -> db.user(userId, hashFunction(password))
        }

        if (signin == null) {
            call.redirect(signInError.copy(error = "Invalid username or password"))
        } else {
            call.sessions.set(EpSession(signin.userId))
            call.redirect(Phrases())
        }

    }

    get<SignIn> {
        val user = call.sessions.get<EpSession>()?.let { db.user(it.userId) }

        if (user != null) {
            call.redirect(Home())
        } else {
            call.respond(FreeMarkerContent("signin.ftl", mapOf("userId" to it.userId, "error" to it.error), ""))
        }
    }
}