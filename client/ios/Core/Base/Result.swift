// Copyright 2015 Yandex LLC. All rights reserved.

import Foundation

public let ResultErrorDomain = "ResultErrorDomain"
public let ResultErrorTransformFailureErrorCode = 0

extension Result {
  @inlinable
  public func unwrap() -> Success {
    switch self {
    case let .success(value):
      return value
    case .failure:
      fatalError("unexpectedly found Failure while unwrapping Result")
    }
  }

  @inlinable
  public func value() -> Success? {
    if case let .success(value) = self {
      return value
    }
    return nil
  }

  @inlinable
  public func error() -> Failure? {
    guard case let .failure(error) = self else { return nil }
    return error
  }

  public var isSuccess: Bool {
    if case .success = self {
      return true
    }

    return false
  }
}

extension Result where Failure: NSError {
  @inlinable
  public func applyTransform<U>(_ transform: (Success) -> U?) -> Result<U, NSError> {
    switch self {
    case let .success(value):
      if let newValue = transform(value) {
        return .success(newValue)
      } else {
        return .failure(NSError(
          domain: ResultErrorDomain,
          code: ResultErrorTransformFailureErrorCode,
          userInfo: nil
        ))
      }
    case let .failure(error):
      return Result<U, NSError>.failure(error)
    }
  }
}

@inlinable
public func resultsAreEqual<T>(
  _ a: Result<T, NSError>,
  _ b: Result<T, NSError>,
  _ equalityTest: (T, T) -> Bool
) -> Bool {
  switch (a, b) {
  case let (.success(value1), .success(value2)):
    return equalityTest(value1, value2)
  case let (.failure(error1), .failure(error2)):
    return error1 == error2
  case (.success, _),
       (.failure, _):
    return false
  }
}
