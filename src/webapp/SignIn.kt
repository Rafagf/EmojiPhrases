package com.rafag.webapp

import com.rafag.repository.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

const val SIGN_IN = "/signin"

@Location(SIGN_IN)
data class SignIn(val userId: String = "", val error: String = "")

fun Route.signIn(db: Repository, hashFunction: (String) -> String) {
   get<SignIn> {
       call.respond(FreeMarkerContent("signin.ftl", null))
   }
}