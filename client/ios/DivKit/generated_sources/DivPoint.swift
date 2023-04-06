// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivPoint {
  public let x: DivDimension
  public let y: DivDimension

  init(
    x: DivDimension,
    y: DivDimension
  ) {
    self.x = x
    self.y = y
  }
}

#if DEBUG
extension DivPoint: Equatable {
  public static func ==(lhs: DivPoint, rhs: DivPoint) -> Bool {
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

extension DivPoint: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["x"] = x.toDictionary()
    result["y"] = y.toDictionary()
    return result
  }
}
