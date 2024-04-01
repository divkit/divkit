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

    /// A function was called with the wrong number of arguments (arity)
    case arityMismatch(Symbol)

    case escaping

    var description: String {
      switch self {
      case let .message(message),
           let .shortMessage(message):
        return message
      case let .unexpectedToken(string):
        return "Unexpected token: \(string)"
      case let .missingDelimiter(string):
        return "Missing delimiter: \(string)"
      case let .undefinedSymbol(symbol):
        return "Undefined symbol: \(symbol)"
      case let .arityMismatch(symbol):
        let arity: Arity = switch symbol {
        case let .function(_, requiredArity):
          requiredArity
        case .infix("?:"):
          3
        case .infix:
          2
        case .postfix, .prefix:
          1
        case .variable:
          0
        }
        return "\(symbol.description) expects \(arity)"
      case .escaping:
        return "Incorrect string escape"
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
