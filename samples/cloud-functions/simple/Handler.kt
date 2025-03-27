package cloud.divkit

data class Request(
    val httpMethod: String?,
    val headers: Map<String, String> = mapOf(),
    val body: String = ""
)

data class Response(
    val statusCode: Int,
    val body: String
)

fun handle(request: Request): Response {
    return Response(200, card)
}

private const val card = """
{
  "log_id": "cloud_divkit",
  "states": [
    {
      "state_id": 0,
      "div": {
        "type": "text",
        "text": "Hello, Username!"
      }
    }
  ]
}
"""
