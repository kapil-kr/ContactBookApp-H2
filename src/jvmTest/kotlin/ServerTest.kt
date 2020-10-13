import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.restassured.RestAssured
import io.restassured.RestAssured.*
import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.AuthenticationSpecification
import io.restassured.specification.RequestSpecification
import kotlinx.coroutines.runBlocking
import model.Contacts
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.junit.Assert.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

open class ServerTest {

    protected fun RequestSpecification.When(): RequestSpecification {
        return this.`when`()
    }

    protected inline fun <reified T> ResponseBodyExtractionOptions.to(): T {
        return this.`as`(T::class.java)
    }

    companion object {

        private var serverStarted = false
        private lateinit var server: ApplicationEngine

        @BeforeAll
        @JvmStatic
        fun startServer() {
            if(!serverStarted) {
                server = embeddedServer(Netty, 9090) {
                    serverConfig()
                }.start()
                serverStarted = true

                RestAssured.baseURI = "http://localhost"
                RestAssured.port = 9090
                RestAssured.authentication = basic("kapilTest", AppConfiguration.password)
                Runtime.getRuntime().addShutdownHook(Thread { server.stop(0, 0, TimeUnit.SECONDS) })
            }
        }
    }

    @BeforeEach
    fun before() = runBlocking {
        newSuspendedTransaction {
            Contacts.deleteAll()
            Unit
        }
    }
}