import Serialization

public struct RawDivData: Deserializable, @unchecked Sendable {
  public let card: [String: Any]
  public let templates: [String: Any]

  public init(dictionary: [String: Any]) throws {
    card = try dictionary.getField("card")
    templates = try dictionary.getOptionalField("templates") ?? [:]
  }
}
