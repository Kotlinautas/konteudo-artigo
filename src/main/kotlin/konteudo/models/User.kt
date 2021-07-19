package konteudo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

object Users: Table(){
    val id: Column<String> = char("id", 36)
    val name: Column<String> = varchar("name", 50)
    override val primaryKey = PrimaryKey(id, name = "PK_Users_Id")

    fun toUser(row: ResultRow): User = User(
        id = row[Users.id],
        name = row[Users.name],
    )
}

@Serializable
data class User (var id: String? = null, val name: String)
