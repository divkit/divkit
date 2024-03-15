import Foundation

enum OperatorsError {
  case unsupportedType(symbol: String, args: [Argument])

  private var description: String {
    switch self {
    case let .unsupportedType(symbol, args):
      let lhs = args[0]
      let rhs = args[1]
      let message = "Failed to evaluate [\(lhs.formattedValue) \(symbol) \(rhs.formattedValue)]."
      if lhs.type == rhs.type {
        return "\(message) Operator '\(symbol)' cannot be applied to \(lhs.type.name) type."
      } else {
        return "\(message) Operator '\(symbol)' cannot be applied to different types: \(lhs.type.name) and \(rhs.type.name)."
      }
    }
  }

  var message: CalcExpression.Error {
    CalcExpression.Error.message(description)
  }
}
