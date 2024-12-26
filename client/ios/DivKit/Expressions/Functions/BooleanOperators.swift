import Foundation

extension [CalcExpression.Symbol: Function] {
  mutating func addBooleanOperators() {
    let notSymbol = CalcExpression.Symbol.prefix("!")
    self[notSymbol] = FunctionUnary<Any, Bool> {
      if let value = $0 as? Bool {
        return !value
      }
      throw ExpressionError(
        "Failed to evaluate [\(notSymbol.formatExpression([$0]))]. A Boolean is expected after a unary not."
      )
    }

    self[.infix("&&")] = andOperator
    self[.infix("||")] = orOperator
    self[.ternary] = ternaryOperator
  }
}

private let andOperator = LazyFunction { args, evaluators in
  let arg0 = try args[0].evaluate(evaluators)
  guard let lhs = arg0 as? Bool else {
    throw invalidArgsError(lhs: arg0, op: "&&")
  }
  if !lhs {
    return false
  }

  let arg1 = try args[1].evaluate(evaluators)
  guard let rhs = arg1 as? Bool else {
    throw invalidArgsError(lhs: arg0, rhs: arg1, op: "&&")
  }
  return rhs
}

private let orOperator = LazyFunction { args, evaluators in
  let arg0 = try args[0].evaluate(evaluators)
  guard let lhs = arg0 as? Bool else {
    throw invalidArgsError(lhs: arg0, op: "||")
  }
  if lhs {
    return true
  }

  let arg1 = try args[1].evaluate(evaluators)
  guard let rhs = arg1 as? Bool else {
    throw invalidArgsError(lhs: arg0, rhs: arg1, op: "||")
  }
  return rhs
}

private let ternaryOperator = LazyFunction { args, evaluators in
  let arg0 = try args[0].evaluate(evaluators)
  if let condition = arg0 as? Bool {
    return condition ? try args[1].evaluate(evaluators) : try args[2].evaluate(evaluators)
  }

  let arg1 = try args[1].evaluate(evaluators)
  let arg2 = try args[2].evaluate(evaluators)
  throw ExpressionError(
    "Failed to evaluate [\(formatArgForError(arg0)) ? \(formatArgForError(arg1)) : \(formatArgForError(arg2))]. Ternary must be called with a Boolean value as a condition."
  )
}

private func invalidArgsError(lhs: Any, op: String) -> Error {
  ExpressionError(
    "Failed to evaluate [\(formatArgForError(lhs)) \(op) ...]. '\(op)' must be called with boolean operands."
  )
}

private func invalidArgsError(lhs: Any, rhs: Any, op: String) -> Error {
  ExpressionError(
    "Failed to evaluate [\(formatArgForError(lhs)) \(op) \(formatArgForError(rhs))]. Operator '\(op)' cannot be applied to different types: \(formatTypeForError(lhs)) and \(formatTypeForError(rhs))."
  )
}
