import Foundation

struct DivStoredValue {
  enum ValueType: String, Codable {
    case string
    case number
    case integer
    case bool
    case color
    case url
  }

  let name: String
  let value: String
  let type: ValueType
  let lifetimeInSec: Int
}
