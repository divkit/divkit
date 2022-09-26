import Foundation

import DivKit
import Serialization
import TemplatesSupport

public struct DivJson: Deserializable {
  public let templates: [String: Any]
  public let cards: [[String: Any]]

  public init(dictionary: [String: Any]) throws {
    templates = try dictionary.getOptionalField("templates") ?? [:]
    cards = try dictionary.getArray("cards")
  }

  static func loadCards() throws -> [DivData] {
    let url = Bundle.main.url(forResource: "div_json", withExtension: "json")!
    let data = try Data(contentsOf: url)
    let divJson = try DivJson(JSONData: data)
    return divJson.cards.compactMap {
      DivData.resolve(
        card: $0,
        templates: divJson.templates
      ).value
    }
  }
}
