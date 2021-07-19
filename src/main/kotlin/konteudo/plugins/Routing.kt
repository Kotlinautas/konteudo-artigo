package konteudo.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import konteudo.routes.registerUserRoutes
import konteudo.routes.registerArticleRoutes

fun Application.configureRouting() {

    routing {
        get("/") {
                call.respondText("Hello World!")
            }
    }
    registerUserRoutes()
    registerArticleRoutes()
}
