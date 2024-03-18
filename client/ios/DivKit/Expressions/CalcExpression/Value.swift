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
  var value: Any {
    switch self {
    case let .integer(value):
      value
    case let .number(value):
      value
    case let .string(value):
      value
    case let .datetime(value):
      value
    case let .boolean(value):
      value
    case .error:
      "error"
    }
  }
}

extension CalcExpression.Value {
  static let maxInteger = Int.max
  static let minInteger = Int.min

  static func integerError(_ value: Any) -> CalcExpression.Error {
    .message("Value \(value) can't be converted to Integer type.")
  }

  static func integerOverflow() -> CalcExpression.Error {
    .shortMessage("Integer overflow.")
  }
}
