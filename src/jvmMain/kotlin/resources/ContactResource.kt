package resources

import ContactItem
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import service.ContactService
import java.sql.SQLIntegrityConstraintViolationException

fun Route.contact(contactService: ContactService) {

    route(ContactItem.path) {
        authenticate("bookAuth") {
            get {
                val from = call.request.queryParameters["from"]?.toLong() ?: 0
                val limit = call.request.queryParameters["limit"]?.toLong() ?: 10
                try {
                    call.respond(contactService.getContacts(from, limit))
                }
                catch (e: Exception) {
                    error("Invalid Request")
                }
            }
            get("/searchbyname/{name}") {
                val name = call.parameters["name"]
                val from = call.request.queryParameters["from"]?.toLong() ?: 0
                val limit = call.request.queryParameters["limit"]?.toLong() ?: 10
                if(name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                }
                else {
                    try {
                        call.respond(contactService.searchByname(name, from, limit))
                    }
                    catch (e: Exception) {
                        error("Invalid Request")
                    }
                }
            }
            get("/searchbyemail/{email}") {
                val email = call.parameters["email"]
                if(email == null) {
                    call.respond(HttpStatusCode.BadRequest)
                }
                else {
                    try {
                        call.respond(contactService.searchByemail(email))
                    }
                    catch (e: Exception) {
                        error("Invalid Request")
                    }
                }
            }
            post {
                try {
                    val contactItem = call.receive<ContactItem>()
                    if(contactItem.name.trim().isEmpty() || contactItem.email.trim().isEmpty())
                        call.respond(HttpStatusCode.BadRequest)
                    else {
                        contactService.addContact(contactItem)
                        call.respond(HttpStatusCode.OK)
                    }
                }
                catch (e: Exception) {
                    val type = (e as? ExposedSQLException)?.cause
                    when(type) {
                        is SQLIntegrityConstraintViolationException ->
                            call.respond(HttpStatusCode.MethodNotAllowed, "emailId already exist")
                        else ->
                            error("Invalid Request")
                    }
                }
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                val contacts = contactService.getContactById(id)
                if(contacts.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound, "this contact doesn't exists")
                }
                else {
                    try {
                        contactService.deleteContact(id)
                        call.respond(HttpStatusCode.OK)
                    }
                    catch (e: Exception) {
                        error("Invalid Request")
                    }
                }
            }
            put("/{id}") {
                val id = call.parameters["id"]?.toInt() ?: error("Invalid Update request")
                val contacts = contactService.getContactById(id)
                if(contacts.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound, "this contact doesn't exists")
                }
                else {
                    try {
                        contactService.updateContact(id,
                                call.receive<ContactItem>())
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception) {
                        val type = (e as? ExposedSQLException)?.cause
                        when (type) {
                            is SQLIntegrityConstraintViolationException ->
                                call.respond(HttpStatusCode.MethodNotAllowed, "emailId already exist")
                            else ->
                                error("Invalid Request")
                        }
                    }
                }
            }
        }
    }
}