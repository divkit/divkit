// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivDefaultIndicatorItemPlacement: Sendable {
  public static let type: String = "default"
  public let spaceBetweenCenters: DivFixedSize // default value: DivFixedSize(value: .value(15))

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      spaceBetweenCenters: try dictionary.getOptionalField("space_between_centers", transform: { (dict: [String: Any]) in try DivFixedSize(dictionary: dict, context: context) })
    )
  }

  init(
    spaceBetweenCenters: DivFixedSize? = nil
  ) {
    self.spaceBetweenCenters = spaceBetweenCenters ?? DivFixedSize(value: .value(15))
  }
}

#if DEBUG
extension DivDefaultIndicatorItemPlacement: Equatable {
  public static func ==(lhs: DivDefaultIndicatorItemPlacement, rhs: DivDefaultIndicatorItemPlacement) -> Bool {
    guard
      lhs.spaceBetweenCenters == rhs.spaceBetweenCenters
    else {
      return false
    }
    return true
  }
}
#endif

extension DivDefaultIndicatorItemPlacement: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["space_between_centers"] = spaceBetweenCenters.toDictionary()
    return result
  }
}
