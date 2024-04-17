import Foundation

extension [CalcExpression.Symbol: Function] {
  mutating func addTryOperator() {
    self[.infix("!:")] = FunctionBinary<Any, Any, Any> {
      $0 is Error ? $1 : $0
    }
  }
}
