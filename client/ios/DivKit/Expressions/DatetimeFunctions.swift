import Foundation

import CommonCore

enum DatetimeFunctions: String, CaseIterable {
  enum Error {
    case month(Date, Int)
    case day(Date, Int)
    case hours(Date, Int)
    case minutes(Date, Int)
    case seconds(Date, Int)
    case millis(Date, Int)

    var message: AnyCalcExpression.Error {
      AnyCalcExpression.Error.message(description)
    }

    private var description: String {
      switch self {
      case let .month(date, value):
        return "Failed to evaluate [setMonth(\(date.formatString), \(value))]. Expecting month in [1..12], instead got \(value)."
      case let .day(date, value):
        return "Failed to evaluate [setDay(\(date.formatString), \(value))]. Unable to set day \(value) for date \(date.formatString)."
      case let .hours(date, value):
        return "Failed to evaluate [setHours(\(date.formatString), \(value))]. Expecting hours in [0..23], instead got \(value)."
      case let .minutes(date, value):
        return "Failed to evaluate [setMinutes(\(date.formatString), \(value))]. Expecting minutes in [0..59], instead got \(value)."
      case let .seconds(date, value):
        return "Failed to evaluate [setSeconds(\(date.formatString), \(value))]. Expecting seconds in [0..59], instead got \(value)."
      case let .millis(date, value):
        return "Failed to evaluate [setMillis(\(date.formatString), \(value))]. Expecting millis in [0..999], instead got \(value)."
      }
    }
  }

  case parseUnixTime
  case nowLocal
  case addMillis
  case setYear
  case setMonth
  case setDay
  case setHours
  case setMinutes
  case setSeconds
  case setMillis

  var declaration: [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] {
    [.function(rawValue, arity: function.arity): function.symbolEvaluator]
  }

  var function: Function {
    switch self {
    case .parseUnixTime:
      return FunctionUnary(impl: _parseUnixTime)
    case .nowLocal:
      return FunctionNullary(impl: _nowLocal)
    case .addMillis:
      return FunctionBinary(impl: _addMillis)
    case .setYear:
      return FunctionBinary(impl: _setYear)
    case .setMonth:
      return FunctionBinary(impl: _setMonth)
    case .setDay:
      return FunctionBinary(impl: _setDay)
    case .setHours:
      return FunctionBinary(impl: _setHours)
    case .setMinutes:
      return FunctionBinary(impl: _setMinutes)
    case .setSeconds:
      return FunctionBinary(impl: _setSeconds)
    case .setMillis:
      return FunctionBinary(impl: _setMillis)
    }
  }
}

private func _parseUnixTime(_ value: Int) -> Date {
  Date(timeIntervalSince1970: Double(value))
}

private func _nowLocal() -> Date {
  Date()
}

private func _addMillis(_ date: Date, _ offsetInMillis: Int) -> Date {
  let newTimeInterval = date.timeIntervalSince1970 + offsetInMillis.toSeconds
  return Date(timeIntervalSince1970: newTimeInterval)
}

private func _setYear(date: Date, newYear: Int) -> Date {
  var components = date.components
  components.year = newYear
  return calendar.date(from: components)!
}

private func _setMonth(date: Date, newMonth: Int) throws -> Date {
  guard newMonth >= 1,
        newMonth <= 12 else { throw DatetimeFunctions.Error.month(date, newMonth).message }
  var components = date.components
  components.month = newMonth
  return calendar.date(from: components)!
}

private func _setDay(date: Date, newDay: Int) throws -> Date {
  let range = calendar.range(of: .day, in: .month, for: date)!
  var components = date.components
  switch newDay {
  case range:
    components.day = newDay
  case -1:
    components.day = 0
  default:
    throw DatetimeFunctions.Error.day(date, newDay).message
  }
  return calendar.date(from: components)!
}

private func _setHours(date: Date, newHour: Int) throws -> Date {
  guard newHour >= 0,
        newHour <= 23 else { throw DatetimeFunctions.Error.hours(date, newHour).message }
  var components = date.components
  components.hour = newHour
  return calendar.date(from: components)!
}

private func _setMinutes(date: Date, newMinutes: Int) throws -> Date {
  guard newMinutes >= 0,
        newMinutes <= 59 else { throw DatetimeFunctions.Error.minutes(date, newMinutes).message }
  var components = date.components
  components.minute = newMinutes
  return calendar.date(from: components)!
}

private func _setSeconds(date: Date, newSeconds: Int) throws -> Date {
  guard newSeconds >= 0,
        newSeconds <= 59 else { throw DatetimeFunctions.Error.seconds(date, newSeconds).message }
  var components = date.components
  components.second = newSeconds
  return calendar.date(from: components)!
}

private func _setMillis(date: Date, newMillis: Int) throws -> Date {
  guard newMillis >= 0,
        newMillis <= 999 else { throw DatetimeFunctions.Error.millis(date, newMillis).message }
  let newTimeInterval = round(date.timeIntervalSince1970) + newMillis.toSeconds
  return Date(timeIntervalSince1970: newTimeInterval)
}

private let dateFormat = "yyyy-MM-dd HH:mm:ss"
private let timeZone = "UTC"

extension String {
  func toDatetime() -> Date? {
    dateFormatter.date(from: self)
  }
}

extension Date {
  var formatString: String {
    dateFormatter.string(from: self)
  }

  fileprivate var components: DateComponents {
    calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: self)
  }
}

extension Int {
  fileprivate var toSeconds: TimeInterval {
    Double(self) / 1000
  }
}

private let calendar: Calendar = {
  var calendar = Calendar.current
  calendar.timeZone = TimeZone(abbreviation: timeZone)!
  return calendar
}()

private let dateFormatter: DateFormatter = {
  let dateFormatter = DateFormatter()
  dateFormatter.dateFormat = dateFormat
  dateFormatter.timeZone = TimeZone(abbreviation: timeZone)
  return dateFormatter
}()

private func castToDate(_ value: Any) -> Date? {
  value as? Date
}

private func castToInt(_ value: Any) -> Int? {
  value as? Int
}
