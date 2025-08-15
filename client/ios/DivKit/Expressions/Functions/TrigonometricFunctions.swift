import Foundation

extension [String: Function] {
  mutating func addTrigonometricFunctions() {
    addFunction("pi", ConstantFunction(Double.pi))
    addFunction("sin", _sinFunction)
    addFunction("cos", _cosFunction)
    addFunction("tan", _tanFunction)
    addFunction("asin", _asinFunction)
    addFunction("acos", _acosFunction)
    addFunction("atan", _atanFunction)
    addFunction("toRadians", _toRadians)
    addFunction("toDegrees", _toDegrees)
    addFunction("cot", _cotFunction)
  }
}

private let _sinFunction = FunctionUnary { (radians: Double) in
  sin(radians)
}

private let _cosFunction = FunctionUnary { (radians: Double) in
  cos(radians)
}

private let _tanFunction = FunctionUnary { (radians: Double) in
  tan(radians)
}

private let _cotFunction = FunctionUnary { (radians: Double) in
  let result = tan(radians)
  if result.isAlmostZero() {
    throw ExpressionError("Cotangent is undefined for the given value.")
  }
  return 1 / result
}

private let _asinFunction = FunctionUnary { (value: Double) in
  let result = asin(value)
  if result.isNaN {
    throw ExpressionError("Arcsine is undefined for the given value.")
  }
  return result
}

private let _acosFunction = FunctionUnary { (value: Double) in
  let result = acos(value)
  if result.isNaN {
    throw ExpressionError("Arccosine is undefined for the given value.")
  }
  return result
}

private let _atanFunction = FunctionUnary { (value: Double) in
  atan(value)
}

private let _toRadians = FunctionUnary { (degrees: Double) in
  degrees * Double.pi / 180
}

private let _toDegrees = FunctionUnary { (radians: Double) in
  radians * 180 / Double.pi
}
