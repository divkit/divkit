import Foundation

enum BooleanOperators: String, CaseIterable {
  case and = "&&"
  case or = "||"
  case not = "!"
  case ternary = "?:"

  var symbol: AnyCalcExpression.Symbol {
    switch self {
    case .and, .or, .ternary:
      return .infix(rawValue)
    case .not:
      return .prefix(rawValue)
    }
  }

  var function: Function {
    OverloadedFunction(
      functions: [
        makeFunction()
      ],
      makeError: makeError
    )
  }

  private func makeFunction() -> SimpleFunction {
    switch self {
    case .and:
      return FunctionBinary { $0 && $1 }
    case .or:
      return FunctionBinary { $0 || $1 }
    case .not:
      return FunctionUnary { !$0 }
    case .ternary:
      return FunctionTernary<Bool, Any, Any, Any> { $0 ? $1 : $2 }
    }
  }

  private func makeError(args: [Argument]) -> AnyCalcExpression.Error {
    switch self {
    case .and, .or:
      if args[0].type == .boolean {
        return OperatorsError.unsupportedType(symbol: rawValue, args: args).message
      }
      return .message("Failed to evaluate [\(args[0].formattedValue) \(rawValue) ...]. '\(rawValue)' must be called with boolean operands.")
    case .not:
      return .message("Failed to evaluate [!\(args[0].formattedValue)]. A Boolean is expected after a unary not.")
    case .ternary:
      return .message("Failed to evaluate [\(args[0].formattedValue) ? \(args[1].formattedValue) : \(args[2].formattedValue)]. Ternary must be called with a Boolean value as a condition.")
    }
  }
}
