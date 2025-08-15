import Foundation

public protocol Deserializable {
  init(dictionary: [String: Any]) throws
}

extension Deserializable {
  public init(JSONString: String) throws {
    guard let data = JSONString.data(using: .utf8) else {
      throw DeserializationError.nonUTF8String(string: JSONString)
    }

    try self.init(JSONData: data)
  }

  public init(JSONData: Data) throws {
    guard let dict = try JSONSerialization.jsonObject(
      with: JSONData, options: []
    ) as? [String: Any] else {
      throw DeserializationError.invalidJSONData(data: JSONData)
    }

    try self.init(dictionary: dict)
  }
}
