import Serialization

public struct JSONPayload: Deserializable {
  public enum `Type`: String {
    case json
  }

  public struct Message: Deserializable {
    public let json: [String: Any]

    public init(dictionary: [String: Any]) throws {
      json = try dictionary.getField("json")
    }
  }

  public let type: `Type`
  public let message: Message

  public init(dictionary: [String: Any]) throws {
    type = try dictionary.getField("type")
    message = try dictionary.getField("message")
  }
}
