package model

import org.jetbrains.exposed.sql.Table

object Contacts : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    override val primaryKey = PrimaryKey(email)
}