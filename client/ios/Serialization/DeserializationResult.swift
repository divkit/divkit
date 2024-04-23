import Foundation

import CommonCorePublic

@frozen
public indirect enum DeserializationResult<T> {
  case success(T)
  case partialSuccess(T, warnings: NonEmptyArray<DeserializationError>)
  case failure(NonEmptyArray<DeserializationError>)
  case noValue
}

extension DeserializationResult {
  public var value: T? {
    switch self {
    case let .success(value), let .partialSuccess(value, _):
      value
    case .failure, .noValue:
      nil
    }
  }

  private var hasValue: Bool { value != nil }

  public var warnings: NonEmptyArray<DeserializationError>? {
    switch self {
    case let .partialSuccess(_, warnings):
      warnings
    case .success, .failure, .noValue:
      nil
    }
  }

  public var errors: NonEmptyArray<DeserializationError>? {
    switch self {
    case let .failure(errors):
      errors
    case .partialSuccess, .success, .noValue:
      nil
    }
  }

  public var errorsOrWarnings: NonEmptyArray<DeserializationError>? {
    switch self {
    case let .partialSuccess(_, errors), let .failure(errors):
      errors
    case .success, .noValue:
      nil
    }
  }

  public func unwrap() throws -> T {
    switch self {
    case let .success(divData):
      return divData
    case let .partialSuccess(divData, _):
      return divData
    case let .failure(errors):
      throw errors.first
    case .noValue:
      throw DeserializationError.noData
    }
  }

  public func merged(with other: () -> DeserializationResult<T>?) -> DeserializationResult<T> {
    if hasValue {
      return self
    } else if let other = other() {
      if other.hasValue {
        return other
      } else if let mergedErrors =
        NonEmptyArray(mergeErrors(errorsOrWarnings, other.errorsOrWarnings)) {
        return .failure(mergedErrors)
      }
    }
    return .noValue
  }

  public func merged(with other: DeserializationResult<T>?) -> DeserializationResult<T> {
    if hasValue {
      self
    } else if let other, other.hasValue {
      other
    } else if let mergedErrors =
      NonEmptyArray(mergeErrors(errorsOrWarnings, other?.errorsOrWarnings)) {
      .failure(mergedErrors)
    } else {
      .noValue
    }
  }
}

public func mergeErrors(
  _ errors: NonEmptyArray<DeserializationError>?...
) -> [DeserializationError] {
  var resultingErrors: [DeserializationError] = []
  for error in errors {
    if let item = error {
      resultingErrors.append(contentsOf: item)
    }
  }
  return resultingErrors
}
