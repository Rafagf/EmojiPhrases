package com.rafag.webapp

import com.rafag.*
import com.rafag.repository.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

const val SIGN_OUT = "/signout"

@Location(SIGN_OUT)
class SignOut

fun Route.signOut() {
   get<SignOut> {
       call.redirect(SignIn())
   }
}