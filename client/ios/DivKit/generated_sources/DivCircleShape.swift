// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization
import TemplatesSupport

public final class DivCircleShape {
  public static let type: String = "circle"
  public let radius: DivFixedSize // default value: DivFixedSize(value: .value(10))

  static let radiusValidator: AnyValueValidator<DivFixedSize> =
    makeNoOpValueValidator()

  init(
    radius: DivFixedSize? = nil
  ) {
    self.radius = radius ?? DivFixedSize(value: .value(10))
  }
}

#if DEBUG
extension DivCircleShape: Equatable {
  public static func ==(lhs: DivCircleShape, rhs: DivCircleShape) -> Bool {
    guard
      lhs.radius == rhs.radius
    else {
      return false
    }
    return true
  }
}
#endif

extension DivCircleShape: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["radius"] = radius.toDictionary()
    return result
  }
}
