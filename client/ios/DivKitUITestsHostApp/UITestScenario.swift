import DivKit
import Foundation
import Serialization
import VGSL

struct UITestScenario {
  struct Configuration {
    static let defaultConfiguration = Self(
      layoutDirection: .leftToRight,
      failOnParsingError: true
    )

    let layoutDirection: UserInterfaceLayoutDirection
    let failOnParsingError: Bool
  }

  private struct ConfigurationContainer: Decodable {
    let configuration: Configuration
  }

  let rawCard: RawDivData
  let configuration: Configuration

  init(data: Data) throws {
    guard let json = try JSONSerialization.jsonObject(with: data) as? [String: Any] else {
      throw DeserializationError.invalidJSONData(data: data)
    }
    rawCard = try json.getField("div_data")
    let container = try? JSONDecoder().decode(ConfigurationContainer.self, from: data)
    configuration = container?.configuration ?? .defaultConfiguration
  }
}

extension UITestScenario.Configuration: Decodable {
  private enum CodingKeys: String, CodingKey {
    case layoutDirection = "layout_direction"
    case failOnParsingError = "fail_on_parsing_error"
  }

  private enum LayoutDirection: String, Decodable {
    case ltr
    case rtl
  }

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    let direction = try? container.decode(LayoutDirection.self, forKey: .layoutDirection)
    layoutDirection = direction.map { $0 == .rtl ? .rightToLeft : .leftToRight }
      ?? Self.defaultConfiguration.layoutDirection
    failOnParsingError = (try? container.decode(Bool.self, forKey: .failOnParsingError))
      ?? Self.defaultConfiguration.failOnParsingError
  }
}
