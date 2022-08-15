import Serialization

public struct RawDivData: Deserializable {
  public let card: [String: Any]
  public let templates: [String: Any]

  public init(dictionary: [String: Any]) throws {
    card = try dictionary.getField("card")
    templates = try dictionary.getOptionalField("templates") ?? [:]
  }

  public func resolve() -> DeserializationResult<DivData> {
    DivData.resolve(card: card, templates: templates)
  }
}
