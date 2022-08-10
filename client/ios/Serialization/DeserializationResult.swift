import Foundation

import CommonCore

extension Either where T == DeserializationError, U == FieldError {
  public func asError(
    deserializing fieldName: String,
    level: FieldError.Level
  ) -> FieldError {
    FieldError(
      fieldName: fieldName,
      level: level,
      error: self
    )
  }
}

public class FieldError: Error, CustomStringConvertible {
  public enum Level: String {
    case warning
    case error
  }

  public let fieldName: String
  public let level: Level
  public let error: Either<DeserializationError, FieldError>

  public convenience init(
    fieldName: String,
    level: Level,
    error: DeserializationError
  ) {
    self.init(fieldName: fieldName, level: level, error: .left(error))
  }

  public convenience init(
    fieldName: String,
    level: Level,
    error: FieldError
  ) {
    self.init(fieldName: fieldName, level: level, error: .right(error))
  }

  fileprivate init(
    fieldName: String,
    level: Level,
    error: Either<DeserializationError, FieldError>
  ) {
    self.fieldName = fieldName
    self.level = level
    self.error = error
  }

  public var description: String {
    "Failed to read field '\(fieldName)':\n\(error.anyError)"
  }
}

extension NonEmptyArray where C: RangeReplaceableCollection, Element == Either<
  DeserializationError,
  FieldError
> {
  public init(_ error: DeserializationError) {
    self.init(.left(error))
  }

  public init(_ error: FieldError) {
    self.init(.right(error))
  }
}

public indirect enum DeserializationResult<T> {
  public typealias Error = Either<DeserializationError, FieldError>
  public typealias Errors = NonEmptyArray<Error>

  case success(T)
  case partialSuccess(T, warnings: Errors)
  case failure(Errors)
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

  public var warnings: Errors? {
    switch self {
    case let .partialSuccess(_, warnings):
      return warnings
    case .success, .failure, .noValue:
      return nil
    }
  }

  public var errors: Errors? {
    switch self {
    case let .failure(errors):
      return errors
    case .partialSuccess, .success, .noValue:
      return nil
    }
  }

  public var errorsOrWarnings: Errors? {
    switch self {
    case let .partialSuccess(_, errors), let .failure(errors):
      return errors
    case .success, .noValue:
      return nil
    }
  }

  public func getErrorsOrWarnings() -> [Swift.Error] {
    errorsOrWarnings?.compactMap { $0.anyError } ?? []
  }

  public func unwrap() throws -> T {
    switch self {
    case let .success(divData):
      return divData
    case let .partialSuccess(divData, _):
      return divData
    case let .failure(errors):
      throw errors.first.anyError
    case .noValue:
      throw DeserializationError.noData
    }
  }
}

extension Either where T == DeserializationError, U == FieldError {
  public var anyError: Swift.Error {
    switch self {
    case let .left(deserializationError): return deserializationError
    case let .right(fieldError): return fieldError
    }
  }
}

extension Either where T == DeserializationError, U == FieldError {
  public var userInfo: [String: String] {
    getUserInfo(path: "")
  }

  fileprivate func getUserInfo(path: String) -> [String: String] {
    switch self {
    case let .left(deserializationError):
      return deserializationError.getUserInfo(path: path)
    case let .right(fieldError):
      return fieldError.getUserInfo(path: path)
    }
  }
}

extension FieldError {
  fileprivate func getUserInfo(path: String) -> [String: String] {
    error.getUserInfo(path: path + "/" + fieldName)
  }
}

extension DeserializationError {
  private var subkind: String {
    switch self {
    case .generic: return "generic"
    case .nonUTF8String: return "nonUTF8String"
    case .invalidJSONData: return "invalidJSONData"
    case .invalidDictionary: return "invalidDictionary"
    case .missingType: return "missingType"
    case .unknownType: return "unknownType"
    case .invalidFieldRepresentation: return "invalidFieldRepresentation"
    case .typeMismatch: return "typeMismatch"
    case .invalidValue: return "invalidValue"
    case .requiredFieldIsMissing: return "requiredFieldIsMissing"
    case .optionalFieldIsMissing: return "optionalFieldIsMissing"
    case .noData: return "noData"
    case .unexpectedError: return "unexpectedError"
    }
  }

  fileprivate func getUserInfo(path: String) -> [String: String] {
    var userInfo = ["path": path, "subkind": subkind]

    switch self {
    case .generic, .requiredFieldIsMissing, .optionalFieldIsMissing, .noData:
      break
    case let .nonUTF8String(string):
      userInfo["string"] = string
    case let .invalidJSONData(data):
      userInfo["data"] = "\(data)"
    case let .invalidDictionary(JSON):
      userInfo["json"] = "\(JSON)"
    case let .missingType(representation):
      userInfo["representation"] = "\(representation)"
    case let .unknownType(type):
      userInfo["type"] = "\(type)"
    case let .invalidFieldRepresentation(field, representation):
      userInfo["field"] = "\(field)"
      userInfo["representation"] = dbgStr(representation)
    case let .typeMismatch(expected, representation):
      userInfo["expected"] = "\(expected)"
      userInfo["representation"] = "\(representation)"
    case let .invalidValue(result, value):
      userInfo["result"] = dbgStr(result)
      userInfo["value"] = dbgStr(value)
    case let .unexpectedError(error):
      userInfo["error"] = "\(error)"
    }

    return userInfo
  }
}

extension DeserializationResult {
  public func merged(with other: DeserializationResult<T>?) -> DeserializationResult<T> {
    let mergedValue = value ?? other?.value
    let mergedErrors: Errors? =
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
  _ errors: NonEmptyArray<Either<DeserializationError, FieldError>>?...
) -> [Either<DeserializationError, FieldError>] {
  var resultingErrors: [Either<DeserializationError, FieldError>] = []
  errors.forEach {
    resultingErrors.append(contentsOf: $0?.asArray() ?? [])
  }
  return resultingErrors
}

extension Either where T == DeserializationError, U == FieldError {
  private var fieldError: FieldError? {
    switch self {
    case .left:
      return nil
    case let .right(error):
      return error
    }
  }

  public var traceback: NonEmptyArray<String> {
    var result = [String]()
    var currentError = self
    while let error = currentError.fieldError {
      result.append("\(error.level.rawValue) in \(error.fieldName)")
      currentError = error.error
    }
    if case let .left(deserializationError) = currentError {
      result.append("DeserializationError: \(deserializationError)")
    }
    return NonEmptyArray(result)!
  }

  #if INTERNAL_BUILD
  public var debugDescription: String {
    (["Traceback (most recent error last):"] + traceback).joined(separator: "\n")
  }
  #endif
}
