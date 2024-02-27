import Foundation

import BaseTinyPublic

enum EqualityOperators: String, CaseIterable {
  case equals = "=="
  case notEquals = "!="

  var function: Function {
    OverloadedFunction(
      functions: [
        makeFunction() as FunctionBinary<String, String, Bool>,
        makeFunction() as FunctionBinary<Int, Int, Bool>,
        makeFunction() as FunctionBinary<Double, Double, Bool>,
        makeFunction() as FunctionBinary<Bool, Bool, Bool>,
        makeFunction() as FunctionBinary<Date, Date, Bool>,
        makeFunction() as FunctionBinary<RGBAColor, RGBAColor, Bool>,
        makeFunction() as FunctionBinary<URL, URL, Bool>,
        makeFunction() as FunctionBinary<[String: AnyHashable], [String: AnyHashable], Bool>,
        makeFunction() as FunctionBinary<[AnyHashable], [AnyHashable], Bool>,
      ],
      makeError: {
        OperatorsError.unsupportedType(symbol: rawValue, args: $0).message
      }
    )
  }

  private func makeFunction<T: Equatable>() -> FunctionBinary<T, T, Bool> {
    FunctionBinary {
      switch self {
      case .equals:
        $0 == $1
      case .notEquals:
        $0 != $1
      }
    }
  }
}
