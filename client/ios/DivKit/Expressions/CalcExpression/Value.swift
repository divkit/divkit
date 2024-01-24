import Foundation

extension CalcExpression {
  enum Value {
    case integer(Int)
    case number(Double)
    case string(String)
    case datetime(Date)
    case boolean(Bool)
    case error(Swift.Error)
  }
}

extension CalcExpression.Value {
  var value: Double {
    switch self {
    case let .integer(integer):
      return Double(integer)
    case let .number(number):
      return number
    case .string:
      return .nan
    case let .datetime(date):
      return date.timeIntervalSince1970
    case let .boolean(bool):
      return bool ? 1 : 0
    case .error:
      return .nan
    }
  }
}

extension CalcExpression.Value {
  static let maxInteger = Int.max
  static let minInteger = Int.min
  static func integerError(_ value: Any) -> AnyCalcExpression.Error {
    .message("Value \(value) can't be converted to Integer type.")
  }

  static func integerOverflow() -> AnyCalcExpression.Error {
    .shortMessage("Integer overflow.")
  }
}
