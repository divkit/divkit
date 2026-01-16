// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTranslationTransformation: Sendable {
  public static let type: String = "translation"
  public let x: DivTranslation?
  public let y: DivTranslation?

  init(
    x: DivTranslation? = nil,
    y: DivTranslation? = nil
  ) {
    self.x = x
    self.y = y
  }
}

#if DEBUG
extension DivTranslationTransformation: Equatable {
  public static func ==(lhs: DivTranslationTransformation, rhs: DivTranslationTransformation) -> Bool {
    guard
      lhs.x == rhs.x,
      lhs.y == rhs.y
    else {
      return false
    }
    return true
  }
}
#endif

extension DivTranslationTransformation: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["x"] = x?.toDictionary()
    result["y"] = y?.toDictionary()
    return result
  }
}
