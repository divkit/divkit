import Foundation

extension CalcExpression {
  enum StandartSymbols {
    static let symbols: [Symbol: SymbolEvaluator] = mathSymbols.merging(
      boolSymbols,
      uniquingKeysWith: { $1 }
    )

    static let mathSymbols: [Symbol: SymbolEvaluator] = [
      .infix("+"): { try $0[0] + $0[1] },
      .infix("-"): { try $0[0] - $0[1] },
      .infix("*"): { try $0[0] * $0[1] },
      .infix("/"): { try $0[0] / $0[1] },
      .infix("%"): { try $0[0] % $0[1] },
      .prefix("-"): { try -$0[0] },
      .prefix("+"): { try +$0[0] },
    ]

    static let boolSymbols: [Symbol: SymbolEvaluator] = [
      .infix("=="): { (args: [Value]) -> Value in
        args[0].value.isApproximatelyEqualTo(args[1].value) ? .number(1) : .number(0)
      },
      .infix("!="): { (args: [Value]) -> Value in
        args[0].value.isApproximatelyNotEqualTo(args[1].value) ? .number(1) : .number(0)
      },
      .infix(">"): { (args: [Value]) -> Value in
        args[0].value > args[1].value ? .number(1) : .number(0)
      },
      .infix(">="): { (args: [Value]) -> Value in
        args[0].value >= args[1].value ? .number(1) : .number(0)
      },
      .infix("<"): { (args: [Value]) -> Value in
        args[0].value < args[1].value ? .number(1) : .number(0)
      },
      .infix("<="): { (args: [Value]) -> Value in
        args[0].value <= args[1].value ? .number(1) : .number(0)
      },
      .infix("&&"): { (args: [Value]) -> Value in
        args[0].value != 0 && args[1].value != 0 ? .number(1) : .number(0)
      },
      .infix("||"): { (args: [Value]) -> Value in
        args[0].value != 0 || args[1].value != 0 ? .number(1) : .number(0)
      },
      .prefix("!"): { (args: [Value]) -> Value in
        args[0].value == 0 ? .number(1) : .number(0)
      },
      .infix("?:"): { (args: [Value]) -> Value in
        if args.count == 3 {
          return args[0].value != 0 ? args[1] : args[2]
        }
        return args[0].value != 0 ? args[0] : args[1]
      },
    ]
  }
}
