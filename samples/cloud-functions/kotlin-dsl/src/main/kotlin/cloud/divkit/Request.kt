package cloud.divkit

data class Request(
    val httpMethod: String?,
    val headers: Map<String, String> = mapOf(),
    val body: String = ""
)
