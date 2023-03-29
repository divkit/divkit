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
  case unexpectedError(message: String)

  public var description: String {
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
      return "Invalid '\(field)' value: \(dbgStr(representation))"
    case let .typeMismatch(expected, representation):
      return "Type mismatch: \(dbgStr(representation)), but '\(expected)' expected"
    case let .invalidValue(result, value):
      return "Invalid value: \(dbgStr(result)), \(dbgStr(value))"
    case let .requiredFieldIsMissing(field):
      return "Required field is missing: \(field)"
    case let .nestedObjectError(field, error):
      return "Error in neseted object in field '\(field)': \(error)"
    case .noData:
      return "No data"
    case let .unexpectedError(message):
      return "Unexpected error: \(message)"
    }
  }

  public var userInfo: [String: String] {
    getUserInfo(path: "")
  }

  fileprivate func getUserInfo(path: String) -> [String: String] {
    var userInfo = ["path": path, "subkind": subkind]

    switch self {
    case .generic,
         .noData,
         .nonUTF8String:
      break
    case let .invalidFieldRepresentation(field, representation):
      userInfo["field"] = field
      userInfo["representation"] = dbgStr(representation)
    case let .invalidJSONData(data):
      userInfo["data"] = "\(data)"
    case let .invalidValue(result, value):
      userInfo["result"] = dbgStr(result)
      userInfo["value"] = dbgStr(value)
    case let .missingType(representation):
      userInfo["representation"] = "\(representation)"
    case let .nestedObjectError(field, error):
      return error.getUserInfo(path: path == "" ? field : path + "/" + field)
    case let .requiredFieldIsMissing(field):
      userInfo["field"] = field
    case let .typeMismatch(expected, representation):
      userInfo["expected"] = expected
      userInfo["representation"] = "\(representation)"
    case let .unexpectedError(message):
      userInfo["error"] = message
    case let .unknownType(type):
      userInfo["type"] = type
    }

    return userInfo
  }

  private var subkind: String {
    switch self {
    case .generic: return "generic"
    case .invalidFieldRepresentation: return "invalidFieldRepresentation"
    case .invalidJSONData: return "invalidJSONData"
    case .invalidValue: return "invalidValue"
    case .missingType: return "missingType"
    case .nestedObjectError: return "nestedObjectError"
    case .noData: return "noData"
    case .nonUTF8String: return "nonUTF8String"
    case .requiredFieldIsMissing: return "requiredFieldIsMissing"
    case .typeMismatch: return "typeMismatch"
    case .unexpectedError: return "unexpectedError"
    case .unknownType: return "unknownType"
    }
  }
}
