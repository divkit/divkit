import Foundation

import CommonCorePublic

extension [String: Function] {
  mutating func addToStringFunctions() {
    self["toString"] = OverloadedFunction(functions: [
      arrayFunction,
      boolFunction,
      colorFunction,
      dictFunction,
      doubleFunction,
      intFunction,
      stringFunction,
      urlFunction,
    ])
  }
}

private let arrayFunction = FunctionUnary<[AnyHashable], String> { CalcExpression.stringify($0) }

private let boolFunction = FunctionUnary<Bool, String> { $0.description }

private let colorFunction = FunctionUnary<Color, String> { $0.argbString }

private let dictFunction = FunctionUnary<[String: AnyHashable], String> { CalcExpression.stringify($0) }

private let doubleFunction = FunctionUnary<Double, String> {
  let formatter = NumberFormatter()
  formatter.minimumFractionDigits = 0
  formatter.maximumFractionDigits = 14
  formatter.decimalSeparator = "."
  guard let string = formatter.string(from: NSNumber(value: $0)) else {
    throw CalcExpression.Error.message("Unable to convert value to String.")
  }
  return string
}

private let intFunction = FunctionUnary<Int, String> { $0.description }

private let stringFunction = FunctionUnary<String, String> { $0 }

private let urlFunction = FunctionUnary<URL, String> { $0.description }
