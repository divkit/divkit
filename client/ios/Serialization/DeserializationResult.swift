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
      return value
    case .failure, .noValue:
      return nil
    }
  }

  public var warnings: NonEmptyArray<DeserializationError>? {
    switch self {
    case let .partialSuccess(_, warnings):
      return warnings
    case .success, .failure, .noValue:
      return nil
    }
  }

  public var errors: NonEmptyArray<DeserializationError>? {
    switch self {
    case let .failure(errors):
      return errors
    case .partialSuccess, .success, .noValue:
      return nil
    }
  }

  public var errorsOrWarnings: NonEmptyArray<DeserializationError>? {
    switch self {
    case let .partialSuccess(_, errors), let .failure(errors):
      return errors
    case .success, .noValue:
      return nil
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

  public func merged(with other: DeserializationResult<T>?) -> DeserializationResult<T> {
    let mergedValue = value ?? other?.value
    let mergedErrors: NonEmptyArray<DeserializationError>? =
      NonEmptyArray(mergeErrors(errorsOrWarnings, other?.errorsOrWarnings))

    switch (mergedValue, mergedErrors) {
    case let (.some(value), .some(warnings)):
      return .partialSuccess(value, warnings: warnings)
    case let (.some(value), .none):
      return .success(value)
    case let (.none, .some(errors)):
      return .failure(errors)
    case (.none, .none):
      return .noValue
    }
  }
}

public func mergeErrors(
  _ errors: NonEmptyArray<DeserializationError>?...
) -> [DeserializationError] {
  var resultingErrors: [DeserializationError] = []
  errors.forEach {
    resultingErrors.append(contentsOf: $0?.asArray() ?? [])
  }
  return resultingErrors
}
