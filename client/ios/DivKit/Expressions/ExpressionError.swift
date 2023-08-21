import Foundation

public struct ExpressionError: Error {
  let message: String
  let expression: String?

  init(
    _ message: String,
    expression: String?
  ) {
    self.message = message
    self.expression = expression
  }
}

extension ExpressionError: CustomStringConvertible {
  public var description: String {
    if let expression = expression {
      return "\(message). Expression: \(expression)"
    }
    return message
  }
}
