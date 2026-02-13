// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPoint: Sendable {
  public let x: DivDimension
  public let y: DivDimension

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      x: try dictionary.getField("x", transform: { (dict: [String: Any]) in try DivDimension(dictionary: dict, context: context) }),
      y: try dictionary.getField("y", transform: { (dict: [String: Any]) in try DivDimension(dictionary: dict, context: context) })
    )
  }

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["x"] = x.toDictionary()
    result["y"] = y.toDictionary()
    return result
  }
}
