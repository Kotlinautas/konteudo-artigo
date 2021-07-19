package konteudo

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import konteudo.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        initDB()

        configureSerialization()
        configureRouting()
    }.start(wait = true)
}
