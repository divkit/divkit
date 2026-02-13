// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTextRangeBorder: Sendable {
  public let cornerRadius: Expression<Int>? // constraint: number >= 0
  public let stroke: DivStroke?

  public func resolveCornerRadius(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(cornerRadius)
  }

  static let cornerRadiusValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      cornerRadius: try dictionary.getOptionalExpressionField("corner_radius", validator: Self.cornerRadiusValidator, context: context),
      stroke: try dictionary.getOptionalField("stroke", transform: { (dict: [String: Any]) in try DivStroke(dictionary: dict, context: context) })
    )
  }

  init(
    cornerRadius: Expression<Int>? = nil,
    stroke: DivStroke? = nil
  ) {
    self.cornerRadius = cornerRadius
    self.stroke = stroke
  }
}

#if DEBUG
extension DivTextRangeBorder: Equatable {
  public static func ==(lhs: DivTextRangeBorder, rhs: DivTextRangeBorder) -> Bool {
    guard
      lhs.cornerRadius == rhs.cornerRadius,
      lhs.stroke == rhs.stroke
    else {
      return false
    }
    return true
  }
}
#endif

extension DivTextRangeBorder: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["corner_radius"] = cornerRadius?.toValidSerializationValue()
    result["stroke"] = stroke?.toDictionary()
    return result
  }
}
