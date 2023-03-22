import Foundation

extension CalcExpression {
  enum Value {
    case integer(Int)
    case number(Double)
    case string(String)
    case datetime(Date)
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

extension CalcExpression.Value {
  static func +(lhs: Self, rhs: Self) throws -> Self {
    switch (lhs, rhs) {
    case let (.integer(lValue), .integer(rValue)):
      if case let (result, overflow) = lValue.addingReportingOverflow(rValue), !overflow {
        return .integer(result)
      } else {
        throw CalcExpression.Value.integerOverflow()
      }
    case let (.number(lValue), .number(rValue)):
      return .number(lValue + rValue)
    case let (.string(lValue), .string(rValue)):
      return .string(lValue + rValue)
    case let (.datetime(lValue), .datetime(rValue)):
      throw CalcExpression.Error
        .message(
          "Failed to evaluate [\(lValue.formatString) + \(rValue.formatString)]. Operator '+' cannot be applied to DateTime type."
        )
    default:
      throw CalcExpression.Error.message("Type mismatch \(lhs) \(rhs)")
    }
  }

  static func -(lhs: Self, rhs: Self) throws -> Self {
    switch (lhs, rhs) {
    case let (.integer(lValue), .integer(rValue)):
      if case let (result, overflow) = lValue.subtractingReportingOverflow(rValue), !overflow {
        return .integer(result)
      } else {
        throw CalcExpression.Value.integerOverflow()
      }
    case let (.number(lValue), .number(rValue)):
      return .number(lValue - rValue)
    case let (.datetime(lValue), .datetime(rValue)):
      throw CalcExpression.Error
        .message(
          "Failed to evaluate [\(lValue.formatString) - \(rValue.formatString)]. Operator '-' cannot be applied to DateTime type."
        )
    default:
      throw CalcExpression.Error.message("Type mismatch \(lhs) \(rhs)")
    }
  }

  static func *(lhs: Self, rhs: Self) throws -> Self {
    switch (lhs, rhs) {
    case let (.integer(lValue), .integer(rValue)):
      if case let (result, overflow) = lValue.multipliedReportingOverflow(by: rValue), !overflow {
        return .integer(result)
      } else {
        throw CalcExpression.Value.integerOverflow()
      }
    case let (.number(lValue), .number(rValue)):
      return .number(lValue * rValue)
    case let (.datetime(lValue), .datetime(rValue)):
      throw CalcExpression.Error
        .message(
          "Failed to evaluate [\(lValue.formatString) * \(rValue.formatString)]. Operator '*' cannot be applied to DateTime type."
        )
    default:
      throw CalcExpression.Error.message("Type mismatch \(lhs) \(rhs)")
    }
  }

  static func /(lhs: Self, rhs: Self) throws -> Self {
    switch (lhs, rhs) {
    case let (.integer(lValue), .integer(rValue)):
      guard rValue != 0 else {
        throw CalcExpression.Error.message(
          "Failed to evaluate [\(lValue) / \(rValue)]. Division by zero is not supported."
        )
      }
      return .integer(lValue / rValue)
    case let (.number(lValue), .number(rValue)):
      guard !rValue.isApproximatelyEqualTo(0) else {
        throw CalcExpression.Error.message(
          "Failed to evaluate [\(lValue) / \(rValue)]. Division by zero is not supported."
        )
      }
      return .number(lValue / rValue)
    case let (.datetime(lValue), .datetime(rValue)):
      throw CalcExpression.Error
        .message(
          "Failed to evaluate [\(lValue.formatString) / \(rValue.formatString)]. Operator '/' cannot be applied to DateTime type."
        )
    default:
      throw CalcExpression.Error.message("Type mismatch \(lhs) \(rhs)")
    }
  }

  static func %(lhs: Self, rhs: Self) throws -> Self {
    switch (lhs, rhs) {
    case let (.integer(lValue), .integer(rValue)):
      guard rValue != 0 else {
        throw CalcExpression.Error.message(
          "Failed to evaluate [\(lValue) % \(rValue)]. Division by zero is not supported."
        )
      }
      return .integer(lValue % rValue)
    case let (.number(lValue), .number(rValue)):
      guard !rValue.isApproximatelyEqualTo(0) else {
        throw CalcExpression.Error.message(
          "Failed to evaluate [\(lValue) % \(rValue)]. Division by zero is not supported."
        )
      }
      return .number(fmod(lValue, rValue))
    case let (.datetime(lValue), .datetime(rValue)):
      throw CalcExpression.Error
        .message(
          "Failed to evaluate [\(lValue.formatString) % \(rValue.formatString)]. Operator '%' cannot be applied to DateTime type."
        )
    default:
      throw CalcExpression.Error.message("Type mismatch \(lhs) \(rhs)")
    }
  }

  static prefix func -(value: Self) throws -> Self {
    switch value {
    case let .integer(value):
      return .integer(-value)
    case let .number(value):
      return .number(-value)
    case .string:
      throw CalcExpression.Error.message("Unary minus operator is not supported for string values.")
    case let .datetime(value):
      throw CalcExpression.Error
        .message(
          "Failed to evaluate [-\(value.formatString)]. A Number is expected after a unary minus."
        )
    }
  }

  static prefix func +(value: Self) throws -> Self {
    switch value {
    case let .integer(value):
      return .integer(value)
    case let .number(value):
      return .number(value)
    case .string:
      throw CalcExpression.Error.message("Unary plus operator is not supported for string values.")
    case let .datetime(value):
      throw CalcExpression.Error
        .message(
          "Failed to evaluate [+\(value.formatString)]. A Number is expected after a unary plus."
        )
    }
  }
}
