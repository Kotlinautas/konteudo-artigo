package konteudo

import konteudo.models.Users
import konteudo.models.Articles
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun initDB(){
    Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver")

    transaction {
        SchemaUtils.create(Users)
        SchemaUtils.create(Articles)
    }
}
