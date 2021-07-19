package konteudo.routes

import konteudo.models.Article
import konteudo.models.Articles
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID;

fun Route.articleRouting(){
    route("/article"){
        get {
            val articles = transaction {
                Articles.selectAll().map { Articles.toArticle(it) }
            }

            return@get call.respond(articles)
        }

        get("{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Article not found", status = HttpStatusCode.NotFound
            )

            val article: List<Article> = transaction { Articles.select { Articles.id eq id }.map { Articles.toArticle(it) }}

            if (article.isNotEmpty()) {
                return@get call.respond(article.first())
            }
            return@get call.respondText("Article not found", status = HttpStatusCode.NotFound)
        }

        post{
            val article = call.receive<Article>()

            article.id = UUID.randomUUID().toString()

            transaction {
                Articles.insert {
                    it[id] = article.id!!
                    it[title] = article.title
                    it[body] = article.body
                    it[author] = article.author
                }
            }

            call.respondText("Created", status = HttpStatusCode.Created)
        }

        delete("id"){
            val id = call.parameters["id"] ?: return@delete call.respondText(
                "Insert article ID to delete a article",
                        status = HttpStatusCode.BadRequest
            )

            val delete: Int = transaction {
                Articles.deleteWhere { Articles.id eq id }
            }

            if (delete == 1){
                return@delete call.respondText("Deleted", status = HttpStatusCode.OK)
            }
            return@delete call.respondText("Article not found", status = HttpStatusCode.NotFound)
        }
    }
}

fun Application.registerArticleRoutes(){
    routing {
        articleRouting()
    }
}