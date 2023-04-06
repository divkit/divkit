// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivWrapContentSize {
  public final class ConstraintSize {
    public let unit: Expression<DivSizeUnit> // default value: dp
    public let value: Expression<Int> // constraint: number >= 0

    public func resolveUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
      resolver.resolveStringBasedValue(expression: unit, initializer: DivSizeUnit.init(rawValue:)) ?? DivSizeUnit.dp
    }

    public func resolveValue(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumericValue(expression: value)
    }

    static let unitValidator: AnyValueValidator<DivSizeUnit> =
      makeNoOpValueValidator()

    static let valueValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    init(
      unit: Expression<DivSizeUnit>? = nil,
      value: Expression<Int>
    ) {
      self.unit = unit ?? .value(.dp)
      self.value = value
    }
  }

  public static let type: String = "wrap_content"
  public let constrained: Expression<Bool>?
  public let maxSize: ConstraintSize?
  public let minSize: ConstraintSize?

  public func resolveConstrained(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumericValue(expression: constrained)
  }

  static let constrainedValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let maxSizeValidator: AnyValueValidator<DivWrapContentSize.ConstraintSize> =
    makeNoOpValueValidator()

  static let minSizeValidator: AnyValueValidator<DivWrapContentSize.ConstraintSize> =
    makeNoOpValueValidator()

  init(
    constrained: Expression<Bool>? = nil,
    maxSize: ConstraintSize? = nil,
    minSize: ConstraintSize? = nil
  ) {
    self.constrained = constrained
    self.maxSize = maxSize
    self.minSize = minSize
  }
}

#if DEBUG
extension DivWrapContentSize: Equatable {
  public static func ==(lhs: DivWrapContentSize, rhs: DivWrapContentSize) -> Bool {
    guard
      lhs.constrained == rhs.constrained,
      lhs.maxSize == rhs.maxSize,
      lhs.minSize == rhs.minSize
    else {
      return false
    }
    return true
  }
}
#endif

extension DivWrapContentSize: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["constrained"] = constrained?.toValidSerializationValue()
    result["max_size"] = maxSize?.toDictionary()
    result["min_size"] = minSize?.toDictionary()
    return result
  }
}

#if DEBUG
extension DivWrapContentSize.ConstraintSize: Equatable {
  public static func ==(lhs: DivWrapContentSize.ConstraintSize, rhs: DivWrapContentSize.ConstraintSize) -> Bool {
    guard
      lhs.unit == rhs.unit,
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension DivWrapContentSize.ConstraintSize: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["unit"] = unit.toValidSerializationValue()
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
