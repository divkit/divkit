import Foundation

extension [CalcExpression.Symbol: Function] {
  mutating func addTryOperator() {
    self[.infix("!:")] = LazyFunction { args, evaluators in
      do {
        return try args[0].evaluate(evaluators)
      } catch {
        return try args[1].evaluate(evaluators)
      }
    }
  }
}
