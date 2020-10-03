import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

import io.ktor.auth.*
import io.ktor.request.*
import service.ContactService
import service.DBFactory

fun main() {
    val port = System.getenv("PORT")?.toInt()?:9090
    DBFactory.init()
    embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Put)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        install(Authentication) {
            basic("bookAuth") {
                realm = "ktor"
                validate { credentials ->
                    if (credentials.password == "kapil")
                        UserIdPrincipal(credentials.name)
                    else
                        null
                }
            }
        }
        routing {
                get("/") {
                    call.respondText(
                            this::class.java.classLoader.getResource("index.html")!!.readText(),
                            ContentType.Text.Html
                    )
                }
                static("/") {
                    resources("")
                }
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
    }.start(wait = true)
}