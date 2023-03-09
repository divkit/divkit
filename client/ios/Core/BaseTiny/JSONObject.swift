// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

public typealias JSONDictionary = JSONObject.Object

public enum JSONObject: Codable, Equatable {
  public enum Error: Swift.Error {
    case invalidPathComponent(String)
    case noValueFound(subpath: String)
    case typeMismatch(subpath: String, expected: String, received: String)
  }

  public typealias Array = [JSONObject]
  public typealias Object = [String: JSONObject]

  case bool(Bool)
  case number(Double)
  case string(String)
  case null
  indirect case array(Array)
  indirect case object(JSONDictionary)

  public static let `true` = JSONObject.bool(true)
  public static let `false` = JSONObject.bool(false)

  @inlinable
  public static func number<IntType: BinaryInteger>(_ integer: IntType) -> Self {
    .number(Double(integer))
  }

  private init?(untypedScalar: Any?) {
    if let number = untypedScalar as? NSNumber {
      if number === kCFBooleanTrue {
        self = .bool(true)
      } else if number === kCFBooleanFalse {
        self = .bool(false)
      } else {
        self = .number(number.doubleValue)
      }
    } else if untypedScalar is NSNull {
      self = .null
    } else if let string = untypedScalar as? String {
      self = .string(string)
    } else if let json = untypedScalar as? JSONObject {
      self = json
    } else {
      return nil
    }
  }

  private init?(singleValueContainer: SingleValueDecodingContainer) {
    if singleValueContainer.decodeNil() {
      self = .null
      return
    }

    let value: Any? = (try? singleValueContainer.decode(Double.self))
      ?? (try? singleValueContainer.decode(Bool.self))
      ?? (try? singleValueContainer.decode(String.self))

    if let obj = JSONObject(untypedScalar: value) {
      self = obj
    } else {
      return nil
    }
  }

  public init(from decoder: Decoder) throws {
    if let container = try? decoder.singleValueContainer(),
       let result = JSONObject(singleValueContainer: container) {
      self = result
    } else if var array = try? decoder.unkeyedContainer() {
      var result: [JSONObject] = []
      while !array.isAtEnd {
        result.append(try array.decode(JSONObject.self))
      }
      self = .array(result)
    } else if let obj = try? decoder.container(keyedBy: StringCodingKey.self) {
      var result: [String: JSONObject] = [:]
      try obj.allKeys.forEach {
        result[$0.stringValue] = try obj.decode(JSONObject.self, forKey: $0)
      }
      self = .object(result)
    } else {
      throw DecodingError
        .dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: ""))
    }
  }

  public func encode(to encoder: Encoder) throws {
    switch self {
    case let .bool(bool):
      var container = encoder.singleValueContainer()
      try container.encode(bool)
    case let .number(number):
      var container = encoder.singleValueContainer()
      try container.encode(number)
    case let .string(string):
      var container = encoder.singleValueContainer()
      try container.encode(string)
    case let .array(array):
      var container = encoder.unkeyedContainer()
      try array.forEach { try container.encode($0) }
    case let .object(object):
      var container = encoder.container(keyedBy: StringCodingKey.self)
      try object.forEach {
        try container.encode(
          $0.value,
          forKey: StringCodingKey(stringValue: $0.key)
        )
      }
    case .null:
      var container = encoder.singleValueContainer()
      try container.encodeNil()
    }
  }
}

extension JSONObject {
  private var shortDescription: String {
    switch self {
    case let .bool(bool):
      return "bool: \(bool)"
    case let .number(number):
      return "number: \(number)"
    case let .string(string):
      return "string: \(string)"
    case .array:
      return "array"
    case .object:
      return "object"
    case .null:
      return "null"
    }
  }

  private func value<C>(
    atPath path: C,
    subpath: String = ""
  ) throws -> JSONObject
    where C: Collection, C.Element == Path.Component {
    guard !path.isEmpty else {
      return self
    }

    let currentComponent = path.first!
    let nextSubpath = subpath + currentComponent.stringValue
    switch (self, currentComponent) {
    case let (.object(dict), .key(key)):
      guard let enclosedValue = dict[key] else {
        throw Error.noValueFound(subpath: nextSubpath)
      }
      return try enclosedValue.value(
        atPath: Swift.Array(path.dropFirst()),
        subpath: nextSubpath
      )
    case let (.array(array), .index(index)):
      guard index < array.count else {
        throw Error.noValueFound(subpath: nextSubpath)
      }
      return try array[index].value(
        atPath: Swift.Array(path.dropFirst()),
        subpath: nextSubpath
      )
    case (.object, _),
         (.array, _),
         (.bool, _),
         (.number, _),
         (.string, _),
         (.null, _):
      throw Error.typeMismatch(
        subpath: nextSubpath,
        expected: currentComponent.expectedType,
        received: shortDescription
      )
    }
  }

  public func value(atPath path: Path) throws -> JSONObject {
    try value(atPath: path.components)
  }

  public func value(atPath path: String) throws -> JSONObject {
    try value(atPath: Path(string: path))
  }

  public func getString(atPath path: String) throws -> String {
    let val = try value(atPath: Path.Component.components(from: path))
    guard case let .string(string) = val else {
      throw Error.typeMismatch(
        subpath: path,
        expected: "String",
        received: val.shortDescription
      )
    }
    return string
  }
}

extension JSONObject {
  public var untypedValue: Any {
    switch self {
    case let .bool(value):
      return NSNumber(value: value)
    case let .number(value):
      return value as NSNumber
    case let .string(value):
      return value
    case let .array(array):
      return array.map { $0.untypedValue }
    case let .object(dict):
      return dict.mapValues {
        $0.untypedValue
      }
    case .null:
      return NSNull()
    }
  }

  public func makeDictionary() -> [String: Any]? {
    untypedValue as? [String: Any]
  }

  fileprivate init?(
    value: Any,
    fallback: (Any) -> JSONObject?,
    errorHandler: (Any) throws -> Void
  ) rethrows {
    if let result = JSONObject(untypedScalar: value) {
      self = result
    } else if let array = value as? [Any] {
      self = try JSONObject(array: array, fallback: fallback, errorHandler: errorHandler)
    } else if let dict = value as? [String: Any] {
      self = try JSONObject(
        dictionary: dict,
        fallback: fallback,
        errorHandler: errorHandler
      )
    } else if let fallbackResult = fallback(value) {
      self = fallbackResult
    } else {
      try errorHandler(value)
      return nil
    }
  }

  private init(
    array: [Any],
    fallback: (Any) -> JSONObject?,
    errorHandler: (Any) throws -> Void
  ) rethrows {
    self = .array(try array.compactMap {
      try JSONObject(value: $0, fallback: fallback, errorHandler: errorHandler)
    })
  }

  public static func from(untypedValue: Any) -> JSONObject? {
    from(untypedValue: untypedValue, fallback: { _ in nil })
  }

  public static func from(
    untypedValue: Any,
    fallback: (Any) -> JSONObject?
  ) -> JSONObject? {
    (try? JSONObject(
      value: untypedValue,
      fallback: fallback,
      errorHandler: {
        throw Error.typeMismatch(
          subpath: "N/A",
          expected: "Valid JSON value",
          received: "\($0)"
        )
      }
    )).flatMap { $0 }
  }

  public init(
    dictionary: [String: Any],
    fallback: (Any) -> JSONObject? = { _ in nil },
    errorHandler: (Any) throws -> Void = { _ in }
  ) rethrows {
    self = .object(try dictionary.typedJSON(fallback: fallback, errorHandler: errorHandler))
  }
}

extension JSONObject: CustomStringConvertible {
  private func indentedDescription(indent: String) -> String {
    switch self {
    case let .bool(value):
      return "\(value)"
    case let .number(num):
      return "\(num)"
    case let .string(string):
      return string
    case let .array(array):
      let contents = array
        .map { $0.indentedDescription(indent: indent + "  ") }
        .joined(separator: ",\n")
      return "\n\(indent)[\n" + contents + "\n\(indent)]"
    case let .object(dict):
      let contents = dict
        .map { "\(indent + "  ")\($0.key): \($0.value.indentedDescription(indent: indent + "  "))" }
        .joined(separator: ",\n")
      return "\(indent){\n" + contents + "\n\(indent)}"
    case .null:
      return indent + "null"
    }
  }

  public var description: String {
    indentedDescription(indent: "")
  }
}

extension Dictionary where Key == String {
  public func typedJSON(fallback: (Any) -> JSONObject? = { _ in nil }) -> [String: JSONObject] {
    typedJSON(fallback: fallback, errorHandler: { _ in })
  }

  public func typedJSON(
    fallback: (Any) -> JSONObject?,
    errorHandler: (Any) throws -> Void
  ) rethrows
    -> [String: JSONObject] {
    let keyValueArray: [(String, JSONObject)] = try compactMap {
      let key = $0.key
      return try JSONObject(
        value: $0.value,
        fallback: fallback,
        errorHandler: errorHandler
      ).map { (key, $0) }
    }

    return [String: JSONObject](keyValueArray, uniquingKeysWith: { $1 })
  }
}

extension Dictionary where Key == String, Value == JSONObject {
  public func untypedJSON() -> [String: Any] {
    mapValues {
      $0.untypedValue
    }
  }
}

extension Dictionary where Key == String, Value == JSONObject {
  public func value(atPath path: JSONObject.Path) throws -> JSONObject {
    try JSONObject.object(self).value(atPath: path)
  }
}

extension JSONObject {
  public struct Path: Codable, CustomDebugStringConvertible, ExpressibleByStringLiteral, Hashable {
    public typealias Component = JSONPathComponent

    public private(set) var components: [Component]
    public private(set) var asString: String

    private var asConcatinationSuffix: String {
      switch components.first {
      case .none:
        return ""
      case .key:
        return "." + asString
      case .index:
        return asString
      }
    }

    public var isEmpty: Bool {
      components.isEmpty
    }

    private init(
      components: [Component],
      string: String
    ) {
      self.components = components
      self.asString = string
    }

    public init(component: Component) {
      self.components = [component]
      asString = component.stringValue
    }

    public init(components: [Component]) {
      self.components = components
      asString = Self.stringify(components: components)
    }

    public init(string: String) throws {
      components = try Component.components(from: string)
      asString = string
    }

    public init(stringLiteral value: String) {
      try! self.init(string: value)
    }

    public init(from decoder: Decoder) throws {
      try self.init(string: try String(from: decoder))
    }

    public static let empty = Self(components: [], string: "")

    public func encode(to encoder: Encoder) throws {
      try asString.encode(to: encoder)
    }

    public func appending(path: Path) -> Path {
      if components.isEmpty {
        return path
      }
      return Path(
        components: components + path.components,
        string: asString + path.asConcatinationSuffix
      )
    }

    public mutating func append(path: Path) {
      if components.isEmpty {
        components = path.components
        asString = path.asString
      } else {
        asString.append(path.asConcatinationSuffix)
        components.append(contentsOf: path.components)
      }
    }

    public func appending(component: Component) -> Path {
      var p = self
      p.append(component: component)
      return p
    }

    public mutating func append(component: Component) {
      if components.isEmpty {
        components.append(component)
        asString = component.stringValue
      } else {
        switch component {
        case let .key(key):
          asString.append(".")
          asString.append(key)
        case let .index(index):
          asString.append("[")
          asString.append(String(index))
          asString.append("]")
        }
        components.append(component)
      }
    }

    private static func stringify(components: [Component]) -> String {
      components
        .reduce(into: [String]()) { partialResult, component in
          switch component {
          case let .index(value):
            partialResult.append("[\(value)]")
          case let .key(value):
            let prefix = partialResult.isEmpty ? "" : "."
            partialResult.append("\(prefix)\(value)")
          }
        }
        .joined()
    }

    public var debugDescription: String { asString }
  }
}

extension Data {
  public func decodeJSON() throws -> JSONObject {
    try JSONDecoder().decode(JSONObject.self, from: self)
  }
}

private struct StringCodingKey: CodingKey {
  let stringValue: String

  init(stringValue: String) {
    self.stringValue = stringValue
  }

  var intValue: Int? { nil }
  public init?(intValue _: Int) { nil }
}

public enum JSONPathComponent: Hashable {
  case key(String)
  case index(Int)

  var stringValue: String {
    switch self {
    case let .index(index):
      return "[\(index)]"
    case let .key(key):
      return key
    }
  }

  var expectedType: String {
    switch self {
    case .key:
      return "Object"
    case .index:
      return "Array"
    }
  }

  static func components(from string: String) throws -> [Self] {
    if string.isEmpty {
      return []
    }

    var result: [Self] = []
    try string.components(separatedBy: ".").forEach { component in
      let error = JSONObject.Error.invalidPathComponent(component)
      let subscriptComponents = component.components(separatedBy: "]")
      if subscriptComponents.count > 1 {
        for `subscript` in subscriptComponents.filter({ !$0.isEmpty }) {
          let components = `subscript`.components(separatedBy: "[")
          guard components.count == 2,
                let int = Int(components[1]) else {
            throw error
          }

          if !components[0].isEmpty {
            result.append(.key(components[0]))
          }
          result.append(.index(int))
        }
      } else {
        guard !component.isEmpty else {
          throw error
        }
        result.append(.key(component))
      }
    }

    return result
  }
}
