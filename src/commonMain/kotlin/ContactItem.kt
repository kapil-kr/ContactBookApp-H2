import kotlinx.serialization.Serializable

@Serializable
data class ContactItem(val name: String, val email: String) {
    var id: Int = 0
    companion object {
        const val path = "/contactList"
    }
}