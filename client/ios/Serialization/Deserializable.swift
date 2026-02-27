import Foundation

public typealias TemplateResolver = ([String: Any]) -> [String: Any]

public final class ParsingContext: @unchecked Sendable {
  public private(set) var warnings: [DeserializationError] = []
  public private(set) var errors: [DeserializationError] = []
  public private(set) var path: [String] = []
  public let templateResolver: TemplateResolver?

  public init(templateResolver: TemplateResolver? = nil) {
    self.templateResolver = templateResolver
  }

  public func appendWarning(_ warning: DeserializationError) {
    warnings.append(warning)
  }

  public func appendError(_ error: DeserializationError) {
    errors.append(error)
  }

  public func appendWarnings(_ warnings: [DeserializationError]) {
    self.warnings.append(contentsOf: warnings)
  }

  public func appendErrors(_ errors: [DeserializationError]) {
    self.errors.append(contentsOf: errors)
  }

  public func append(result: DeserializationResult<some Any>) {
    if let warnings = result.warnings {
      appendWarnings(Array(warnings))
    }
    if let errors = result.errors {
      appendErrors(Array(errors))
    }
  }

  public func captureErrors(_ body: () -> Void) -> [DeserializationError] {
    let countBefore = errors.count
    body()
    let captured = Array(errors[countBefore...])
    errors.removeSubrange(countBefore...)
    return captured
  }

  @discardableResult
  public func withPath<T>(_ component: String, _ body: () throws -> T) rethrows -> T {
    path.append(component)
    defer { _ = path.popLast() }
    return try body()
  }
}

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
