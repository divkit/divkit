import Foundation

import CommonCorePublic

extension [String: Function] {
  mutating func addToStringFunctions() {
    self["toString"] = OverloadedFunction(functions: [
      FunctionUnary<[AnyHashable], String> { CalcExpression.stringify($0) },
      FunctionUnary<Bool, String> { $0.description },
      FunctionUnary<Color, String> { $0.argbString },
      FunctionUnary<[String: AnyHashable], String> { CalcExpression.stringify($0) },
      FunctionUnary<Double, String> { CalcExpression.stringify($0) },
      FunctionUnary<Int, String> { $0.description },
      FunctionUnary<String, String> { $0 },
      FunctionUnary<URL, String> { $0.description },
    ])
  }
}
