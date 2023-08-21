import CoreFoundation
import Foundation

public class AnyValueValidator<T> {
  private let validate: (T) -> Bool

  public init(_ isValid: @escaping (T) -> Bool) {
    validate = isValid
  }

  public func isValid(_ value: T) -> Bool {
    validate(value)
  }
}

public final class AnyArrayValueValidator<U>: AnyValueValidator<[U]> {
  public let isPartialDeserializationAllowed: Bool

  public init(
    arrayValidator: @escaping ([U]) -> Bool,
    isPartialDeserializationAllowed: Bool
  ) {
    self.isPartialDeserializationAllowed = isPartialDeserializationAllowed
    super.init(arrayValidator)
  }
}

public func makeCFStringValidator(minLength: Int) -> AnyValueValidator<CFString> {
  AnyValueValidator { CFStringGetLength($0) >= minLength }
}

public func makeStringValidator(
  minLength: Int = 0,
  regex: String? = nil
) -> AnyValueValidator<String> {
  AnyValueValidator { str in
    if str.index(str.startIndex, offsetBy: minLength, limitedBy: str.endIndex) == nil {
      return false
    }
    return regex.map { (try? str.matchesPattern($0)) ?? true } ?? true
  }
}

public func makeURLValidator(schemes: [String]) -> AnyValueValidator<URL> {
  AnyValueValidator { $0.scheme.map(schemes.contains) ?? false }
}

@inlinable
public func makeValueValidator<T>(
  valueValidator: @escaping (T) -> Bool
) -> AnyValueValidator<T> {
  AnyValueValidator(valueValidator)
}

@inlinable
public func makeNoOpValueValidator<T>() -> AnyValueValidator<T> {
  AnyValueValidator { _ in true }
}

@inlinable
public func makeArrayValidator<T>(minItems: Int) -> AnyArrayValueValidator<T> {
  AnyArrayValueValidator(
    arrayValidator: { $0.count >= minItems },
    isPartialDeserializationAllowed: true
  )
}

@inlinable
public func makeStrictArrayValidator<T>(minItems: Int) -> AnyArrayValueValidator<T> {
  AnyArrayValueValidator(
    arrayValidator: { $0.count >= minItems },
    isPartialDeserializationAllowed: false
  )
}
