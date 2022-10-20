import Foundation

public enum ExpressionError: Error {
  case tokenizing(expression: String)
  case emptyValue
  case incorrectSingleItemExpression(expression: String, type: Any.Type)
  case initializingValue(
    expression: String,
    stringValue: String,
    type: Any.Type
  )
  case calculating(
    expression: String,
    scriptInject: String,
    description: String
  )
  case validating(
    expression: String,
    value: String,
    type: Any.Type
  )
  case escaping
  case unknown(error: Error)
}

extension ExpressionError: CustomStringConvertible {
  public var description: String {
    switch self {
    case let .tokenizing(expression):
      return "Error tokenizing '\(expression)'."
    case .emptyValue:
      return "Empty value"
    case let .incorrectSingleItemExpression(expression, type):
      return "Expression could not be resolved as single inject: type: \(type), expression: '\(expression)'"
    case let .calculating(_, _, calcExpressionError):
      return calcExpressionError.description
    case let .initializingValue(expression, stringValue, type):
      return "Error on initializing value '\(stringValue)' of type \(type), expression: '\(expression)'"
    case let .validating(expression, value, type):
      return "Value '\(value)' did not pass validation for type \(type), expression: '\(expression)'"
    case .escaping:
      return "Incorrect string escape"
    case let .unknown(error):
      return "Unknown error: \(error)"
    }
  }
}
