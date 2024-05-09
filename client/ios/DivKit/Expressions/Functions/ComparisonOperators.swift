
import Foundation

enum ComparisonOperators: String, CaseIterable {
  case greater = ">"
  case greaterOrEqual = ">="
  case less = "<"
  case lessOrEqual = "<="

  var function: Function {
    OverloadedFunction(
      functions: [
        makeFunction() as FunctionBinary<Int, Int, Bool>,
        makeFunction() as FunctionBinary<Double, Double, Bool>,
        makeFunction() as FunctionBinary<Date, Date, Bool>,
      ],
      makeError: {
        OperatorsError.unsupportedType(symbol: rawValue, args: $0)
      }
    )
  }

  private func makeFunction<T: Comparable>() -> FunctionBinary<T, T, Bool> {
    FunctionBinary {
      switch self {
      case .greater:
        $0 > $1
      case .greaterOrEqual:
        $0 >= $1
      case .less:
        $0 < $1
      case .lessOrEqual:
        $0 <= $1
      }
    }
  }
}
