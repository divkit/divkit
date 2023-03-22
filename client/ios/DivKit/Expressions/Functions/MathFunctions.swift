import Foundation

enum MathFunctions: String, CaseIterable {
  case div
  case mod
  case mul
  case sub
  case sum
  case maxInteger
  case minInteger
  case maxNumber
  case minNumber
  case max
  case min
  case abs
  case signum
  case round
  case floor
  case ceil
  case copySign

  var declaration: [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] {
    [.function(rawValue, arity: function.arity): function.symbolEvaluator]
  }

  var function: Function {
    switch self {
    case .div:
      return OverloadedFunction(
        functions: [
          FunctionBinary(impl: _divInt),
          FunctionBinary(impl: _divDouble),
        ]
      )
    case .mod:
      return OverloadedFunction(
        functions: [
          FunctionBinary(impl: _modInt),
          FunctionBinary(impl: _modDouble),
        ]
      )
    case .mul:
      return OverloadedFunction(
        functions: [
          FunctionVarUnary(impl: _mulInt),
          FunctionVarUnary(impl: _mulDouble),
        ]
      )
    case .sub:
      return OverloadedFunction(
        functions: [
          FunctionVarUnary(impl: _subInt),
          FunctionVarUnary(impl: _subDouble),
        ]
      )
    case .sum:
      return OverloadedFunction(
        functions: [
          FunctionVarUnary(impl: _sumInt),
          FunctionVarUnary(impl: _sumDouble),
        ]
      )
    case .maxInteger:
      return FunctionNullary(impl: _maxInteger)
    case .minInteger:
      return FunctionNullary(impl: _minInteger)
    case .maxNumber:
      return FunctionNullary(impl: _maxNumber)
    case .minNumber:
      return FunctionNullary(impl: _minNumber)
    case .max:
      return OverloadedFunction(
        functions: [
          FunctionVarUnary(impl: _maxInt),
          FunctionVarUnary(impl: _maxDouble),
        ]
      )
    case .min:
      return OverloadedFunction(
        functions: [
          FunctionVarUnary(impl: _minInt),
          FunctionVarUnary(impl: _minDouble),
        ]
      )
    case .abs:
      return OverloadedFunction(
        functions: [
          FunctionUnary(impl: _absInt),
          FunctionUnary(impl: _absDouble),
        ]
      )
    case .signum:
      return OverloadedFunction(
        functions: [
          FunctionUnary(impl: _signumInt),
          FunctionUnary(impl: _signumDouble),
        ]
      )
    case .round:
      return FunctionUnary(impl: _round)
    case .floor:
      return FunctionUnary(impl: _floor)
    case .ceil:
      return FunctionUnary(impl: _ceil)
    case .copySign:
      return OverloadedFunction(
        functions: [
          FunctionBinary(impl: _copySignInt),
          FunctionBinary(impl: _copySignDouble),
        ]
      )
    }
  }
}

private func _divInt(lhs: Int, rhs: Int) throws -> Int {
  guard rhs != 0 else {
    throw Error.divisionByZero("div", lhs, rhs).message
  }
  return lhs / rhs
}

private func _divDouble(lhs: Double, rhs: Double) throws -> Double {
  guard !rhs.isApproximatelyEqualTo(0) else {
    throw Error.divisionByZero("div", lhs, rhs).message
  }
  return lhs / rhs
}

private func _modInt(lhs: Int, rhs: Int) throws -> Int {
  guard rhs != 0 else {
    throw Error.divisionByZero("mod", lhs, rhs).message
  }
  return lhs % rhs
}

private func _modDouble(lhs: Double, rhs: Double) throws -> Double {
  guard !rhs.isApproximatelyEqualTo(0) else {
    throw Error.divisionByZero("mod", lhs, rhs).message
  }
  return fmod(lhs, rhs)
}

private func _mulInt(args: [Int]) throws -> Int {
  try args.reduce(1) {
    if case let (result, overflow) = $0.multipliedReportingOverflow(by: $1), !overflow {
      return result
    } else {
      throw CalcExpression.Value.integerOverflow()
    }
  }
}

private func _mulDouble(args: [Double]) throws -> Double {
  args.reduce(1) { $0 * $1 }
}

private func _subInt(args: [Int]) throws -> Int {
  try args.dropFirst().reduce(args[0]) {
    if case let (result, overflow) = $0.subtractingReportingOverflow($1), !overflow {
      return result
    } else {
      throw CalcExpression.Value.integerOverflow()
    }
  }
}

private func _subDouble(args: [Double]) throws -> Double {
  args.dropFirst().reduce(args[0]) { $0 - $1 }
}

private func _sumInt(args: [Int]) throws -> Int {
  try args.reduce(0) {
    if case let (result, overflow) = $0.addingReportingOverflow($1), !overflow {
      return result
    } else {
      throw CalcExpression.Value.integerOverflow()
    }
  }
}

private func _sumDouble(args: [Double]) throws -> Double {
  args.reduce(0) { $0 + $1 }
}

private func _maxInteger() -> Int {
  Int.max
}

private func _minInteger() -> Int {
  Int.min
}

private func _maxNumber() -> Double {
  Double.greatestFiniteMagnitude
}

private func _minNumber() -> Double {
  Double.leastNonzeroMagnitude
}

private func _maxInt(args: [Int]) -> Int {
  args.reduce(args[0]) { max($0, $1) }
}

private func _maxDouble(args: [Double]) -> Double {
  args.reduce(args[0]) { max($0, $1) }
}

private func _minInt(args: [Int]) -> Int {
  args.reduce(args[0]) { min($0, $1) }
}

private func _minDouble(args: [Double]) -> Double {
  args.reduce(args[0]) { min($0, $1) }
}

private func _absInt(value: Int) throws -> Int {
  guard value >= -Int.max else {
    throw CalcExpression.Value.integerOverflow()
  }
  return abs(value)
}

private func _absDouble(value: Double) -> Double {
  abs(value)
}

private func _signumInt(value: Int) -> Int {
  value > 0 ? 1 : (value < 0 ? -1 : 0)
}

private func _signumDouble(value: Double) -> Double {
  value.isApproximatelyGreaterThan(0) ? 1 : (value.isApproximatelyLessThan(0) ? -1 : 0)
}

private func _round(value: Double) -> Double {
  round(value)
}

private func _floor(value: Double) -> Double {
  floor(value)
}

private func _ceil(value: Double) -> Double {
  ceil(value)
}

private func _copySignInt(lhs: Int, rhs: Int) throws -> Int {
  guard lhs >= -Int.max && rhs >= 0 || lhs >= Int.min && rhs < 0 else {
    throw CalcExpression.Value.integerOverflow()
  }
  guard rhs != 0 else {
    return lhs
  }
  return Int(copysign(Double(lhs), Double(rhs)))
}

private func _copySignDouble(lhs: Double, rhs: Double) -> Double {
  copysign(lhs, rhs)
}

private enum Error {
  case typeMismatch(args: [Any])
  case divisionByZero(String, Any, Any)

  private var description: String {
    switch self {
    case let .typeMismatch(args):
      return "Type mismatch \(args.map { "\($0)" }.joined(separator: " "))"
    case let .divisionByZero(function, lhs, rhs):
      return "Failed to evaluate [\(function)(\(lhs), \(rhs))]. Division by zero is not supported."
    }
  }

  var message: AnyCalcExpression.Error {
    AnyCalcExpression.Error.message(description)
  }
}
