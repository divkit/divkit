import Foundation

extension CalcExpression {
  enum Error: Swift.Error, CustomStringConvertible, Equatable {
    /// An application-specific error
    case message(String)

    /// An application-specific error without information about failed expression
    case shortMessage(String)

    /// The parser encountered a sequence of characters it didn't recognize
    case unexpectedToken(String)

    /// The parser expected to find a delimiter (e.g. closing paren) but didn't
    case missingDelimiter(String)

    /// The specified constant, operator or function was not recognized
    case undefinedSymbol(Symbol)

    case noMatchingSignature

    case escaping

    var description: String {
      switch self {
      case let .message(message),
           let .shortMessage(message):
        message
      case let .unexpectedToken(string):
        "Unexpected token: \(string)"
      case let .missingDelimiter(string):
        "Missing delimiter: \(string)"
      case let .undefinedSymbol(symbol):
        "Undefined symbol: \(symbol)"
      case .noMatchingSignature:
        "No matching function."
      case .escaping:
        "Incorrect string escape"
      }
    }

    func makeOutputMessage(for expression: String) -> String {
      switch self {
      case let .shortMessage(message):
        "Failed to evaluate [\(expression.escaped)]. \(message)"
      default:
        self.description
      }
    }
  }
}

extension String {
  fileprivate var escaped: String {
    replacingOccurrences(of: "\\", with: "\\\\")
  }
}
