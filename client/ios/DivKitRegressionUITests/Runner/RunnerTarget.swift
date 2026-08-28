import Foundation

enum RunnerTarget: Decodable, CustomStringConvertible {
  case divID(String)

  private enum CodingKeys: String, CodingKey {
    case type
  }

  private enum TargetType: String, Decodable {
    case divID = "div_id"
  }

  private struct DivIDPayload: Decodable {
    let id: String
  }

  var description: String {
    switch self {
    case let .divID(value):
      "div_id=\(value)"
    }
  }

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    let type = try container.decode(TargetType.self, forKey: .type)

    switch type {
    case .divID:
      let payload = try DivIDPayload(from: decoder)
      guard !payload.id.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
        throw DecodingError.dataCorrupted(
          .init(
            codingPath: decoder.codingPath,
            debugDescription: "target id must not be empty"
          )
        )
      }
      self = .divID(payload.id)
    }
  }
}
