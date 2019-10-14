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

const val SIGN_UP = "/signup"

@Location(SIGN_UP)
data class SignUp(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val error: String = ""
)

fun Route.signUp(db: Repository, hashFunction: (String) -> String) {
    post<SignUp> {
        val user = call.sessions.get<EpSession>()?.let {
            db.user(it.userId)
        }

        if (user != null) {
            return@post call.redirect(Phrases())
        }

        val signUpParameters = call.receive<Parameters>()
        val userId = signUpParameters["userId"] ?: return@post call.redirect(it)
        val password = signUpParameters["password"] ?: return@post call.redirect(it)
        val displayName = signUpParameters["displayName"] ?: return@post call.redirect(it)
        val email = signUpParameters["email"] ?: return@post call.redirect(it)

        val signUpError = SignUp(userId, password, displayName, email)

        when {
            userId.length < MIN_USER_ID_LENGTH -> call.redirect(signUpError.copy(error = "UserId should be at least $MIN_USER_ID_LENGTH long"))
            password.length < MIN_PASSWORD__LENGTH -> call.redirect(signUpError.copy(error = "Password should be at least $MIN_PASSWORD__LENGTH long"))
            !userNameValid(displayName) -> call.redirect(signUpError.copy(error = "Username should consist of digits, letters, dots or underscores"))
            db.user(userId) != null -> call.redirect(signUpError.copy("Username $userId is already registered"))
            else -> {
                val hash = hashFunction(password)
                val newUser = User(userId, email, displayName, hash)

                try {
                    db.createUser(newUser)
                } catch (e: Throwable) {
                    when {
                        db.user(userId) != null call.redirect(signUpError.copy("Username $userId is already registered"))
                        }
                    }
                }
            }
        }
    }

    get<SignUp> {
        call.respond(FreeMarkerContent("signup.ftl", null))
    }
}