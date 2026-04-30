import Foundation

public enum DivStoredValueScope: String, Hashable, Sendable, Decodable {
  case global
  case card
}

struct DivStoredValue: Hashable {
  enum ValueType: String, Codable {
    case string
    case number
    case integer
    case boolean
    case bool // invalid value, used for backward compatibility
    case color
    case url
    case array
    case dict
  }

  let name: String
  let value: String
  let type: ValueType
  let lifetimeInSec: Int
}
