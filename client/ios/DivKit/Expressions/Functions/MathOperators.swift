import Foundation

enum MathOperators: String, CaseIterable {
  case sum = "+"
  case sub = "-"
  case mul = "*"
  case div = "/"
  case mod = "%"
  case plus
  case minus

  var symbol: CalcExpression.Symbol {
    switch self {
    case .sum, .sub, .mul, .div, .mod:
      .infix(rawValue)
    case .plus:
      .prefix("+")
    case .minus:
      .prefix("-")
    }
  }

  var function: Function {
    switch self {
    case .sum:
      OverloadedFunction(
        functions: [
          FunctionBinary(impl: _sumInt),
          FunctionBinary(impl: _sumDouble),
          FunctionBinary(impl: _sumString),
        ],
        makeError: makeError
      )
    case .sub:
      OverloadedFunction(
        functions: [
          FunctionBinary(impl: _subInt),
          FunctionBinary(impl: _subDouble),
        ],
        makeError: makeError
      )
    case .mul:
      OverloadedFunction(
        functions: [
          FunctionBinary(impl: _mulInt),
          FunctionBinary(impl: _mulDouble),
        ],
        makeError: makeError
      )
    case .div:
      OverloadedFunction(
        functions: [
          FunctionBinary(impl: _divInt),
          FunctionBinary(impl: _divDouble),
        ],
        makeError: makeError
      )
    case .mod:
      OverloadedFunction(
        functions: [
          FunctionBinary(impl: _modInt),
          FunctionBinary(impl: _modDouble),
        ],
        makeError: makeError
      )
    case .plus:
      OverloadedFunction(
        functions: [
          FunctionUnary<Int, Int> { $0 },
          FunctionUnary<Double, Double> { $0 },
        ],
        makeError: makeUnaryError
      )
    case .minus:
      OverloadedFunction(
        functions: [
          FunctionUnary<Int, Int> { -$0 },
          FunctionUnary<Double, Double> { -$0 },
        ],
        makeError: makeUnaryError
      )
    }
  }

  private func makeError(args: [Any]) -> Error {
    ExpressionError.unsupportedType(op: symbol.name, args: args)
  }

  private func makeUnaryError(args: [Any]) -> Error {
    ExpressionError(
      "Failed to evaluate [\(symbol.formatExpression(args))]. A Number is expected after a unary \(rawValue)."
    )
  }
}

private func _sumInt(lhs: Int, rhs: Int) throws -> Int {
  try lhs.checkingOverflow("+", rhs, operation: lhs.addingReportingOverflow)
}

private func _subInt(lhs: Int, rhs: Int) throws -> Int {
  try lhs.checkingOverflow("-", rhs, operation: lhs.subtractingReportingOverflow)
}

private func _mulInt(lhs: Int, rhs: Int) throws -> Int {
  try lhs.checkingOverflow("*", rhs, operation: lhs.multipliedReportingOverflow)
}

private func _divInt(lhs: Int, rhs: Int) throws -> Int {
  guard rhs != 0 else {
    throw divisionByZeroError("/", lhs, rhs)
  }
  return lhs / rhs
}

private func _modInt(lhs: Int, rhs: Int) throws -> Int {
  guard rhs != 0 else {
    throw divisionByZeroError("%", lhs, rhs)
  }
  return lhs % rhs
}

private func _sumDouble(lhs: Double, rhs: Double) -> Double {
  lhs + rhs
}

private func _subDouble(lhs: Double, rhs: Double) -> Double {
  lhs - rhs
}

private func _mulDouble(lhs: Double, rhs: Double) -> Double {
  lhs * rhs
}

private func _divDouble(lhs: Double, rhs: Double) throws -> Double {
  guard !rhs.isApproximatelyEqualTo(0) else {
    throw divisionByZeroError("/", lhs, rhs)
  }
  return lhs / rhs
}

private func _modDouble(lhs: Double, rhs: Double) throws -> Double {
  guard !rhs.isApproximatelyEqualTo(0) else {
    throw divisionByZeroError("/", lhs, rhs)
  }
  return fmod(lhs, rhs)
}

private func _sumString(lhs: String, rhs: String) throws -> String {
  lhs + rhs
}

extension Int {
  fileprivate func checkingOverflow(
    _ symbol: String,
    _ other: Int,
    operation: (Int) -> (partialValue: Int, overflow: Bool)
  ) throws -> Int {
    let result = operation(other)
    if !result.overflow {
      return result.partialValue
    }
    throw ExpressionError(
      "Failed to evaluate [\(self) \(symbol) \(other)]. Integer overflow."
    )
  }
}

private func divisionByZeroError(
  _ symbol: String,
  _ lhs: Any,
  _ rhs: Any
) -> Error {
  ExpressionError(
    "Failed to evaluate [\(lhs) \(symbol) \(rhs)]. Division by zero is not supported."
  )
}
