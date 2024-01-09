import Foundation

import CommonCorePublic

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
      return "[\(path.joined(separator: "/"))]: \(error)," +
        "    caused by    " +
        "\(causes.map { $0.getDescription(path: path) }.joined(separator: ";    "))"
    case let .nestedObjectError(field, error):
      return error.getDescription(path: path + [field])
    default:
      return "[\(path.joined(separator: "/"))]: \(errorMessage)"
    }
  }

  public var errorMessage: String {
    switch self {
    case .generic:
      return "Deserialization error"
    case .nonUTF8String:
      return "Non-UTF8 string"
    case .invalidJSONData:
      return "Invalid JSON"
    case let .missingType(representation):
      return "Missing type: \(representation)"
    case let .unknownType(type):
      return "Unknown type: \(type)"
    case let .invalidFieldRepresentation(field, representation):
      return "Invalid '\(field)' value: \(dbgStrLimited(representation))"
    case let .typeMismatch(expected, representation):
      return "Type mismatch: \(dbgStrLimited(representation)), but '\(expected)' expected"
    case let .invalidValue(result, value):
      return "Invalid value: '\(dbgStrLimited(result))', from '\(dbgStrLimited(value))'"
    case let .requiredFieldIsMissing(field):
      return "Required field is missing: \(field)"
    case let .nestedObjectError(_, error):
      return error.errorMessage
    case .noData:
      return "No data"
    case let .composite(error, _):
      return "\(error)"
    case let .unexpectedError(message):
      return "Unexpected error: \(message)"
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
      return causes.flatMap(\.rootCauses)
    case let .nestedObjectError(field, error):
      return error.rootCauses.map { .nestedObjectError(field: field, error: $0) }
    default:
      return [self]
    }
  }

  private var subkind: String {
    switch self {
    case .generic: return "generic"
    case .invalidFieldRepresentation: return "invalidFieldRepresentation"
    case .invalidJSONData: return "invalidJSONData"
    case .invalidValue: return "invalidValue"
    case .missingType: return "missingType"
    case let .nestedObjectError(_, error): return error.subkind
    case .noData: return "noData"
    case .nonUTF8String: return "nonUTF8String"
    case .requiredFieldIsMissing: return "requiredFieldIsMissing"
    case .typeMismatch: return "typeMismatch"
    case .unexpectedError: return "unexpectedError"
    case .unknownType: return "unknownType"
    case .composite: return "composite"
    }
  }

  public enum DerivedError: Error, CustomStringConvertible {
    case invalidValue(result: Any?, from: Any?)

    public var description: String {
      switch self {
      case let .invalidValue(result, value):
        return "Invalid value: '\(dbgStrLimited(result))', from: '\(dbgStrLimited(value))'"
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
      return self
    } else {
      return "\(self.prefix(Int(limit)))..."
    }
  }
}
