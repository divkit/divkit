import Foundation

extension CalcExpression {
  enum Error: Swift.Error, LocalizedError, Equatable {
    /// An application-specific error
    case message(String)

    /// The parser encountered a sequence of characters it didn't recognize
    case unexpectedToken(String)

    /// The parser expected to find a delimiter (e.g. closing paren) but didn't
    case missingDelimiter(String)

    case noMatchingSignature

    case integerOverflow

    var errorDescription: String? {
      switch self {
      case let .message(message):
        message
      case let .unexpectedToken(string):
        "Unexpected token: \(string)"
      case let .missingDelimiter(string):
        "Missing delimiter: \(string)"
      case .noMatchingSignature:
        "No matching function."
      case .integerOverflow:
        "Integer overflow."
      }
    }
  }
}

extension String {
  fileprivate var escaped: String {
    replacingOccurrences(of: "\\", with: "\\\\")
  }
}
