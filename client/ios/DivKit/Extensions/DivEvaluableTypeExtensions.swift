import Foundation
import VGSL

extension DivEvaluableType {
  var systemType: Any.Type {
    switch self {
    case .integer: Int.self
    case .number: Double.self
    case .string: String.self
    case .boolean: Bool.self
    case .datetime: Date.self
    case .color: Color.self
    case .url: URL.self
    case .dict: DivDictionary.self
    case .array: DivArray.self
    }
  }
}
