import Foundation

extension [String: Function] {
  mutating func addTrigonometricFunctions() {
    addFunction("pi", ConstantFunction(Double.pi))
    addFunction("sin", _sinFunction)
    addFunction("cos", _cosFunction)
    addFunction("toRadians", _toRadians)
    addFunction("toDegrees", _toDegrees)
  }
}

private let _sinFunction = FunctionUnary { (radians: Double) in
  sin(radians)
}

private let _cosFunction = FunctionUnary { (radians: Double) in
    cos(radians)
}

private let _toRadians = FunctionUnary { (degrees: Double) in
	degrees * Double.pi / 180
}

private let _toDegrees = FunctionUnary { (radians: Double) in
  radians * 180 / Double.pi
}
