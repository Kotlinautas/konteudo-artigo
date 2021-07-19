package konteudo.routes

import konteudo.models.User
import konteudo.models.Users
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID;

fun Route.userRouting(){
    route("/user"){
        get {
            val users = transaction { Users.selectAll().map { Users.toUser(it) } }

            println(users)
            return@get call.respond(users)
        }

        get("{id}"){
            val id = call.parameters["id"] ?:return@get call.respondText(
                "User not found", status = HttpStatusCode.NotFound
            )

            val user: List<User> = transaction { Users.select { Users.id eq id }.map { Users.toUser(it) }}

            if (user.isNotEmpty()) {
                return@get call.respond(user.first())
            }
            return@get call.respondText("User not found", status = HttpStatusCode.NotFound)
        }

        post {
            val user = call.receive<User>()

            user.id = UUID.randomUUID().toString()

            println(user)

            transaction {
                Users.insert {
                    it[id] = user.id!!
                    it[name] = user.name
                }
            }

            call.respondText("Created", status = HttpStatusCode.Created)
        }

        delete("{id}"){
            val id = call.parameters["id"] ?: return@delete call.respondText(
                "Insert {id} to delete a user",
                status = HttpStatusCode.BadRequest
            )

            val delete: Int = transaction {
                Users.deleteWhere { Users.id eq id }
            }

            if (delete == 1){
                return@delete call.respondText("Deleted", status = HttpStatusCode.OK)
            }
            return@delete call.respondText("User not found", status = HttpStatusCode.NotFound)
        }
    }
}

fun Application.registerUserRoutes(){
    routing {
        userRouting()
    }
}
