package webapp

import com.rafag.model.*
import com.rafag.repository.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.locations.get
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val ABOUT = "about"

@Location(ABOUT)
class About

fun Route.about(db: Repository) {
    get<About> {
        val user = call.sessions.get<EpSession>()?.let { db.user(it.userId) }
        call.respond(FreeMarkerContent("about.ftl", mapOf("user" to user)))
    }
}