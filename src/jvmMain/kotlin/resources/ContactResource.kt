package resources

import ContactItem
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import service.ContactService

fun Route.contact(contactService: ContactService) {

    route(ContactItem.path) {
        authenticate("bookAuth") {
            get("/{from}") {
                val from = call.parameters["from"]?.toLong() ?: error("Invalid search Request")
                call.respond(ContactService().getContacts(from))
            }
            get("/searchbyname/{name}/{from}") {
                val name = call.parameters["name"] ?: error("Invalid search request")
                val from = call.parameters["from"]?.toLong() ?: error("Invalid search Request")
                call.respond(ContactService().searchByname(name, from))
                call.respond(HttpStatusCode.OK)
            }
            get("/searchbyemail/{email}") {
                val email = call.parameters["email"] ?: error("Invalid search request")
                call.respond(ContactService().searchByemail(email))
                call.respond(HttpStatusCode.OK)
            }
            post {
                try {
                    ContactService().addContact(call.receive<ContactItem>())
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    error("Invalid Request")
                }
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                ContactService().deleteContact(id)
                call.respond(HttpStatusCode.OK)
            }
            put("/{id}") {
                val id = call.parameters["id"]?.toInt() ?: error("Invalid Update request")
                try {
                    ContactService().updateContact(id,
                            call.receive<ContactItem>())
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    error("Invalid Request")
                }
            }
        }
    }
}