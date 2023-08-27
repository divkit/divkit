import BasePublic
import Serialization
import LayoutKitInterface
import LayoutKit

public protocol DivError: CustomStringConvertible {
  var kind: DivErrorKind { get }
  var message: String { get }
  var path: UIElementPath { get }
  var causes: [DivError] { get }
  var level: DivErrorLevel { get }
}

public enum DivErrorKind: String {
  case deserialization = "deserialization"
  case blockModeling = "divModeling"
  case expression = "expression"
  case unknown = "unknown"
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
      : "    caused by    \(rootCauses.map { $0.description }.joined(separator: ";   "))"
    )
  }
}

extension DivError {
  public var prettyMessage: String {
    return "\(message)" +
    "\nKind: \(kind)" +
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
    case .nestedObjectError(let field, let error):
      return error.getPath(parent: parent.map {$0 + field} ?? UIElementPath(field))
    default:
      return parent
    }
  }

  public var message: String { errorMessage }
  public var path: UIElementPath { getPath(parent: nil) ?? UIElementPath("") }
  public var kind: DivErrorKind { .deserialization }
  public var level: DivErrorLevel { .error }
}

