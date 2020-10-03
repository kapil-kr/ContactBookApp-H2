import kotlinx.serialization.Serializable

@Serializable
data class ContactItem(val id: Int, val name: String, val email: String) {

    companion object {
        const val path = "/contactList"
    }
}