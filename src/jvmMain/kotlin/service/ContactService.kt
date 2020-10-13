package service

import ContactItem
import model.Contacts
import org.jetbrains.exposed.sql.*
import service.DBFactory.dbQuery

class ContactService {

    private fun toContact(row: ResultRow): ContactItem {
        val contactItem = ContactItem (
                name = row[Contacts.name],
                email = row[Contacts.email]
        )
        contactItem.id = row[Contacts.id]
        return contactItem
    }

    suspend fun getContacts(from: Long, limit: Long): List<ContactItem> = dbQuery {
        Contacts.selectAll().limit(limit.toInt(), from).map {
            toContact(it)
        }
    }

    suspend fun addContact(newContact: ContactItem) {
        dbQuery {
            Contacts.insert {
                it[name] = newContact.name
                it[email] = newContact.email
            }
        }
    }

    suspend fun deleteContact(id: Int) {
        return dbQuery {
            Contacts.deleteWhere { Contacts.id eq id } > 0
        }
    }

    suspend fun getContactById(id: Int): List<ContactItem> = dbQuery {
        Contacts.select {
            (Contacts.id eq id)
        }.map {
            toContact(it)
        }
    }

    suspend fun updateContact(id: Int, newContact: ContactItem) {
            dbQuery {
                Contacts.update({ Contacts.id eq id }) {
                    it[name] = newContact.name
                    it[email] = newContact.email
                }
            }
    }

    suspend fun searchByname(name: String, from:Long, limit: Long): List<ContactItem> = dbQuery {
        Contacts.select {
            (Contacts.name eq name)
        }.limit(limit.toInt(), from).map {
            toContact(it)
        }
    }

    suspend fun searchByemail(email: String): List<ContactItem> = dbQuery {
        Contacts.select {
            (Contacts.email eq email)
        }.map {
            toContact(it)
        }
    }
}