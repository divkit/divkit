import Foundation
import VGSL

@frozen
public indirect enum DeserializationError: Error, CustomStringConvertible {
  case generic
  case nonUTF8String(string: String)
  case invalidJSONData(data: Data)
  case missingType(representation: [String: Any])
  case unknownType(type: String)
  case invalidFieldRepresentation(field: String, representation: Any?)
  case typeMismatch(expected: String, representation: Any)
  case invalidValue(result: Any?, value: Any?)
  case requiredFieldIsMissing(field: String)
  case nestedObjectError(field: String, error: DeserializationError)
  case noData
  case composite(error: DerivedError, causes: NonEmptyArray<DeserializationError>)
  case unexpectedError(message: String)

  public var description: String {
    getDescription(path: [])
  }

  private func getDescription(path: [String]) -> String {
    switch self {
    case let .composite(error, causes):
      "[\(path.joined(separator: "/"))]: \(error)," +
        "    caused by    " +
        "\(causes.map { $0.getDescription(path: path) }.joined(separator: ";    "))"
    case let .nestedObjectError(field, error):
      error.getDescription(path: path + [field])
    default:
      "[\(path.joined(separator: "/"))]: \(errorMessage)"
    }
  }

  public var errorMessage: String {
    switch self {
    case .generic:
      "Deserialization error"
    case .nonUTF8String:
      "Non-UTF8 string"
    case .invalidJSONData:
      "Invalid JSON"
    case let .missingType(representation):
      "Missing type: \(representation)"
    case let .unknownType(type):
      "Unknown type: \(type)"
    case let .invalidFieldRepresentation(field, representation):
      "Invalid '\(field)' value: \(dbgStrLimited(representation))"
    case let .typeMismatch(expected, representation):
      "Type mismatch: \(dbgStrLimited(representation)), but '\(expected)' expected"
    case let .invalidValue(result, value):
      "Invalid value: '\(dbgStrLimited(result))', from '\(dbgStrLimited(value))'"
    case let .requiredFieldIsMissing(field):
      "Required field is missing: \(field)"
    case let .nestedObjectError(_, error):
      error.errorMessage
    case .noData:
      "No data"
    case let .composite(error, _):
      "\(error)"
    case let .unexpectedError(message):
      "Unexpected error: \(message)"
    }
  }

  public var userInfo: [String: String] {
    getUserInfo([])
  }

  private func getUserInfo(_ path: [String]) -> [String: String] {
    var userInfo: [String: String] = [:]

    switch self {
    case let .nestedObjectError(field, error):
      return error.getUserInfo(path + [field])
    case .composite:
      userInfo["root_causes"] =
        "\(rootCauses.map { $0.getDescription(path: path) }.joined(separator: ";   "))"
    default:
      break
    }

    return userInfo + [
      "subkind": subkind,
      "path": path.joined(separator: "/"),
      "description": errorMessage,
    ]
  }

  public var rootCauses: [DeserializationError] {
    switch self {
    case let .composite(_, causes):
      causes.flatMap(\.rootCauses)
    case let .nestedObjectError(field, error):
      error.rootCauses.map { .nestedObjectError(field: field, error: $0) }
    default:
      [self]
    }
  }

  private var subkind: String {
    switch self {
    case .generic: "generic"
    case .invalidFieldRepresentation: "invalidFieldRepresentation"
    case .invalidJSONData: "invalidJSONData"
    case .invalidValue: "invalidValue"
    case .missingType: "missingType"
    case let .nestedObjectError(_, error): error.subkind
    case .noData: "noData"
    case .nonUTF8String: "nonUTF8String"
    case .requiredFieldIsMissing: "requiredFieldIsMissing"
    case .typeMismatch: "typeMismatch"
    case .unexpectedError: "unexpectedError"
    case .unknownType: "unknownType"
    case .composite: "composite"
    }
  }

  public enum DerivedError: Error, CustomStringConvertible {
    case invalidValue(result: Any?, from: Any?)

    public var description: String {
      switch self {
      case let .invalidValue(result, value):
        "Invalid value: '\(dbgStrLimited(result))', from: '\(dbgStrLimited(value))'"
      }
    }
  }
}

fileprivate func dbgStrLimited(_ val: (some Any)?, limit: UInt = 100) -> String {
  val.map { "\($0)".crop(limit) } ?? "nil"
}

extension String {
  fileprivate func crop(_ limit: UInt) -> String {
    if self.count <= limit {
      self
    } else {
      "\(self.prefix(Int(limit)))..."
    }
  }
}
