import Foundation

import CommonCore

public indirect enum DeserializationError: Error {
  case generic
  case nonUTF8String(string: String)
  case invalidJSONData(data: Data)
  case invalidDictionary(JSON: Any)
  case missingType(representation: [String: Any])
  case unknownType(type: String)
  case invalidFieldRepresentation(field: String, representation: Any?)
  case typeMismatch(expected: String, representation: Any)
  case invalidValue(result: Any?, value: Any?)
  case requiredFieldIsMissing
  case optionalFieldIsMissing
  case noData
  case unexpectedError(additional: Any)
}

extension DeserializationError {
  public var localizedDescription: String {
    switch self {
    case .generic:
      return "generic error"
    case let .nonUTF8String(string):
      return "non-UTF8 string - \(string)"
    case .invalidJSONData:
      return "invalid JSON data"
    case let .invalidDictionary(dict):
      return "invalid dictionary - \(dict)"
    case let .missingType(representation):
      return "missing type - \(representation)"
    case let .unknownType(type):
      return "unknown type - \(type)"
    case let .invalidFieldRepresentation(field, representation):
      return "invalid field representation - [\(field): \(dbgStr(representation))]"
    case let .typeMismatch(expected, representation):
      return "type mismatch - expected: \(expected), \(dbgStr(representation))"
    case let .invalidValue(result, value):
      return "invalid value - \(dbgStr(result)), \(dbgStr(value))"
    case .requiredFieldIsMissing:
      return "required field is missing"
    case .optionalFieldIsMissing:
      return "optional field is missing"
    case .noData:
      return "no data"
    case let .unexpectedError(additional):
      return "unexpected error - \(dbgStr(additional))"
    }
  }
}
