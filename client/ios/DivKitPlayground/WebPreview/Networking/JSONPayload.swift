import Serialization

struct JSONPayload: Deserializable {
  enum `Type`: String {
    case json
  }

  struct Message: Deserializable {
    let json: [String: Any]

    init(dictionary: [String: Any]) throws {
      json = try dictionary.getField("json")
    }
  }

  let type: `Type`
  let message: Message

  init(dictionary: [String: Any]) throws {
    type = try dictionary.getField("type")
    message = try dictionary.getField("message")
  }
}
