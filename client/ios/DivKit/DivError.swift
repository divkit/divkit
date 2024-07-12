import LayoutKit
import Serialization
import VGSL

public protocol DivError: CustomStringConvertible {
  var kind: DivErrorKind { get }
  var message: String { get }
  var path: UIElementPath { get }
  var causes: [DivError] { get }
  var level: DivErrorLevel { get }
}

public enum DivErrorKind: String {
  case deserialization
  case blockModeling = "divModeling"
  case expression
  case unknown
}

extension DivError {
  public var causes: [DivError] { [] }
}

extension DivError {
  public var rootCauses: [DivError] {
    var result = [DivError]()
    for error in causes {
      if error.causes.isEmpty {
        result.append(error)
      } else {
        result.append(contentsOf: error.rootCauses)
      }
    }
    return result
  }
}

extension DivError {
  public var description: String {
    "[\(path)]: \(message)" + (
      causes.isEmpty
        ? ""
        : "    caused by    \(rootCauses.map(\.description).joined(separator: ";   "))"
    )
  }
}

extension DivError {
  public var prettyMessage: String {
    "\(message)" +
      "\nLevel: \(level)" +
      "\nPath: \(path)" +
      (
        causes.isEmpty ? "" : "\nCauses:\n- " + rootCauses.map { "\($0)" }.joined(separator: "\n- ")
      )
  }
}

public enum DivErrorLevel {
  case warning
  case error
}

extension DeserializationError: DivError {
  private func getPath(parent: UIElementPath?) -> UIElementPath? {
    switch self {
    case let .nestedObjectError(field, error):
      error.getPath(parent: parent.map { $0 + field } ?? UIElementPath(field))
    default:
      parent
    }
  }

  public var message: String { errorMessage }
  public var path: UIElementPath { getPath(parent: nil) ?? UIElementPath("") }
  public var kind: DivErrorKind { .deserialization }
  public var level: DivErrorLevel { .error }
}

struct DivBlockModelingError: Error, DivError {
  public let kind: DivErrorKind = .blockModeling
  public let message: String
  public let path: UIElementPath
  public let causes: [DivError]
  public let level: DivErrorLevel = .error

  init(_ message: String, path: UIElementPath, causes: [DivError] = []) {
    self.message = message
    self.path = path
    self.causes = causes
    DivKitLogger.error(description)
  }
}

struct DivBlockModelingWarning: DivError {
  public let kind = DivErrorKind.blockModeling
  public let message: String
  public let path: UIElementPath
  public let level: DivErrorLevel = .warning

  init(_ message: String, path: UIElementPath) {
    self.message = message
    self.path = path
    DivKitLogger.warning(description)
  }
}

struct DivExpressionError: Error, DivError {
  public let kind = DivErrorKind.expression
  public let message: String
  public let path: UIElementPath
  public let level: DivErrorLevel = .error

  init(_ error: ExpressionError, path: UIElementPath) {
    self.message = error.description
    self.path = path
    DivKitLogger.error(description)
  }
}

struct DivUnknownError: Error, DivError {
  public let kind = DivErrorKind.unknown
  public let message: String
  public let path: UIElementPath
  public let level: DivErrorLevel = .error

  init(_ error: Error, path: UIElementPath) {
    self.message = (error as CustomStringConvertible).description
    self.path = path
    DivKitLogger.error(description)
  }
}
