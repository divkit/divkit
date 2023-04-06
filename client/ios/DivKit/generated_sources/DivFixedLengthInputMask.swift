// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivFixedLengthInputMask {
  public final class PatternElement {
    public let key: Expression<String> // at least 1 char
    public let placeholder: Expression<String> // default value: _
    public let regex: Expression<String>? // at least 1 char

    public func resolveKey(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: key, initializer: { $0 })
    }

    public func resolvePlaceholder(_ resolver: ExpressionResolver) -> String {
      resolver.resolveStringBasedValue(expression: placeholder, initializer: { $0 }) ?? "_"
    }

    public func resolveRegex(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: regex, initializer: { $0 })
    }

    static let keyValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    static let placeholderValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    static let regexValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    init(
      key: Expression<String>,
      placeholder: Expression<String>? = nil,
      regex: Expression<String>? = nil
    ) {
      self.key = key
      self.placeholder = placeholder ?? .value("_")
      self.regex = regex
    }
  }

  public static let type: String = "fixed_length"
  public let alwaysVisible: Expression<Bool> // default value: false
  public let pattern: Expression<String> // at least 1 char
  public let patternElements: [PatternElement] // at least 1 elements

  public func resolveAlwaysVisible(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: alwaysVisible) ?? false
  }

  public func resolvePattern(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: pattern, initializer: { $0 })
  }

  static let alwaysVisibleValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let patternValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let patternElementsValidator: AnyArrayValueValidator<DivFixedLengthInputMask.PatternElement> =
    makeArrayValidator(minItems: 1)

  init(
    alwaysVisible: Expression<Bool>? = nil,
    pattern: Expression<String>,
    patternElements: [PatternElement]
  ) {
    self.alwaysVisible = alwaysVisible ?? .value(false)
    self.pattern = pattern
    self.patternElements = patternElements
  }
}

#if DEBUG
extension DivFixedLengthInputMask: Equatable {
  public static func ==(lhs: DivFixedLengthInputMask, rhs: DivFixedLengthInputMask) -> Bool {
    guard
      lhs.alwaysVisible == rhs.alwaysVisible,
      lhs.pattern == rhs.pattern,
      lhs.patternElements == rhs.patternElements
    else {
      return false
    }
    return true
  }
}
#endif

extension DivFixedLengthInputMask: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["always_visible"] = alwaysVisible.toValidSerializationValue()
    result["pattern"] = pattern.toValidSerializationValue()
    result["pattern_elements"] = patternElements.map { $0.toDictionary() }
    return result
  }
}

#if DEBUG
extension DivFixedLengthInputMask.PatternElement: Equatable {
  public static func ==(lhs: DivFixedLengthInputMask.PatternElement, rhs: DivFixedLengthInputMask.PatternElement) -> Bool {
    guard
      lhs.key == rhs.key,
      lhs.placeholder == rhs.placeholder,
      lhs.regex == rhs.regex
    else {
      return false
    }
    return true
  }
}
#endif

extension DivFixedLengthInputMask.PatternElement: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["key"] = key.toValidSerializationValue()
    result["placeholder"] = placeholder.toValidSerializationValue()
    result["regex"] = regex?.toValidSerializationValue()
    return result
  }
}
