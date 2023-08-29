import Foundation

enum JSONStringDataExtractionError: Error {
  case unableToRetrieveData
}

extension JSONSerialization {
  static func jsonObject(jsonString: String) throws -> Any {
    guard let data = jsonString.data(using: .utf8) else {
      throw JSONStringDataExtractionError.unableToRetrieveData
    }
    return try Self.jsonObject(with: data)
  }
}
