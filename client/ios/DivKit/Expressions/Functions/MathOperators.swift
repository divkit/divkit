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

  private func makeError(args: [Argument]) -> CalcExpression.Error {
    OperatorsError.unsupportedType(symbol: symbol.name, args: args).message
  }

  private func makeUnaryError(args: [Argument]) -> CalcExpression.Error {
    Error.unaryUnsupportedType(symbol: symbol.name, symbolName: rawValue, arg: args[0]).message
  }
}

private func _sumInt(lhs: Int, rhs: Int) throws -> Int {
  try lhs.checkingOverflow(rhs, operation: lhs.addingReportingOverflow)
}

private func _subInt(lhs: Int, rhs: Int) throws -> Int {
  try lhs.checkingOverflow(rhs, operation: lhs.subtractingReportingOverflow)
}

private func _mulInt(lhs: Int, rhs: Int) throws -> Int {
  try lhs.checkingOverflow(rhs, operation: lhs.multipliedReportingOverflow)
}

private func _divInt(lhs: Int, rhs: Int) throws -> Int {
  guard rhs != 0 else {
    throw Error.divisionByZero("/", lhs, rhs).message
  }
  return lhs / rhs
}

private func _modInt(lhs: Int, rhs: Int) throws -> Int {
  guard rhs != 0 else {
    throw Error.divisionByZero("%", lhs, rhs).message
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
    throw Error.divisionByZero("/", lhs, rhs).message
  }
  return lhs / rhs
}

private func _modDouble(lhs: Double, rhs: Double) throws -> Double {
  guard !rhs.isApproximatelyEqualTo(0) else {
    throw Error.divisionByZero("/", lhs, rhs).message
  }
  return fmod(lhs, rhs)
}

private func _sumString(lhs: String, rhs: String) throws -> String {
  lhs + rhs
}

extension Int {
  func checkingOverflow(
    _ other: Int,
    operation: (Int) -> (partialValue: Int, overflow: Bool)
  ) throws -> Int {
    let result = operation(other)
    if !result.overflow {
      return result.partialValue
    } else {
      throw CalcExpression.Value.integerOverflow()
    }
  }
}

private enum Error {
  case divisionByZero(String, Any, Any)
  case unaryUnsupportedType(symbol: String, symbolName: String, arg: Argument)

  private var description: String {
    switch self {
    case let .divisionByZero(symbol, lhs, rhs):
      "Failed to evaluate [\(lhs) \(symbol) \(rhs)]. Division by zero is not supported."
    case let .unaryUnsupportedType(symbol, symbolName, arg):
      "Failed to evaluate [\(symbol)\(arg.formattedValue)]. A Number is expected after a unary \(symbolName)."
    }
  }

  var message: CalcExpression.Error {
    CalcExpression.Error.message(description)
  }
}
