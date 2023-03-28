import Foundation

import CommonCorePublic

enum DatetimeFunctions: String, CaseIterable {
  enum Error {
    case month(Date, Int)
    case day(Date, Int)
    case hours(Date, Int)
    case minutes(Date, Int)
    case seconds(Date, Int)
    case millis(Date, Int)
    case components(String)
    case component(String, String)

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
      case let .components(funcName):
        return "Failed to evaluate [\(funcName)]. Date components not found."
      case let .component(funcName, component):
        return "Failed to evaluate [\(funcName)]. Component '\(component)' not found."
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
  case getYear
  case getMonth
  case getDay
  case getDayOfWeek
  case getHours
  case getMinutes
  case getSeconds
  case getMillis

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
    case .getYear:
      return FunctionUnary(impl: _getYear)
    case .getMonth:
      return FunctionUnary(impl: _getMonth)
    case .getDay:
      return FunctionUnary(impl: _getDay)
    case .getDayOfWeek:
      return FunctionUnary(impl: _getDayOfWeek)
    case .getHours:
      return FunctionUnary(impl: _getHours)
    case .getMinutes:
      return FunctionUnary(impl: _getMinutes)
    case .getSeconds:
      return FunctionUnary(impl: _getSeconds)
    case .getMillis:
      return FunctionUnary(impl: _getMillis)
    }
  }
}

private func _parseUnixTime(_ value: Int) -> Date {
  Date(timeIntervalSince1970: Double(value))
}

private func _nowLocal() -> Date {
  let dateUTC = Date()
  let timeZoneOffset = Double(TimeZone.current.secondsFromGMT(for: dateUTC))
  guard let localDate = Calendar.current.date(
    byAdding: .second,
    value: Int(timeZoneOffset),
    to: dateUTC
  ) else {
    return dateUTC
  }
  return localDate
}

private func _addMillis(_ date: Date, _ offsetInMillis: Int) -> Date {
  let newTimeInterval = date.timeIntervalSince1970 + offsetInMillis.toSeconds
  return Date(timeIntervalSince1970: newTimeInterval)
}

private func _setYear(date: Date, newYear: Int) throws -> Date {
  var components = date.components
  components.year = newYear
  guard let result = calendar.date(from: components) else {
    throw DatetimeFunctions.Error.components(makeFuncName(#function, date, newYear)).message
  }
  return result
}

private func _setMonth(date: Date, newMonth: Int) throws -> Date {
  guard newMonth >= 1,
        newMonth <= 12 else { throw DatetimeFunctions.Error.month(date, newMonth).message }
  var components = date.components
  components.month = newMonth
  guard let result = calendar.date(from: components) else {
    throw DatetimeFunctions.Error.components(makeFuncName(#function, date, newMonth)).message
  }
  return result
}

private func _setDay(date: Date, newDay: Int) throws -> Date {
  guard let range = calendar.range(of: .day, in: .month, for: date) else {
    throw DatetimeFunctions.Error.components(makeFuncName(#function, date, newDay)).message
  }
  var components = date.components
  switch newDay {
  case range:
    components.day = newDay
  case -1:
    components.day = 0
  default:
    throw DatetimeFunctions.Error.day(date, newDay).message
  }
  guard let result = calendar.date(from: components) else {
    throw DatetimeFunctions.Error.components(makeFuncName(#function, date, newDay)).message
  }
  return result
}

private func _setHours(date: Date, newHour: Int) throws -> Date {
  guard newHour >= 0,
        newHour <= 23 else { throw DatetimeFunctions.Error.hours(date, newHour).message }
  var components = date.components
  components.hour = newHour
  guard let result = calendar.date(from: components) else {
    throw DatetimeFunctions.Error.components(makeFuncName(#function, date, newHour)).message
  }
  return result
}

private func _setMinutes(date: Date, newMinutes: Int) throws -> Date {
  guard newMinutes >= 0,
        newMinutes <= 59 else { throw DatetimeFunctions.Error.minutes(date, newMinutes).message }
  var components = date.components
  components.minute = newMinutes
  guard let result = calendar.date(from: components) else {
    throw DatetimeFunctions.Error.components(makeFuncName(#function, date, newMinutes)).message
  }
  return result
}

private func _setSeconds(date: Date, newSeconds: Int) throws -> Date {
  guard newSeconds >= 0,
        newSeconds <= 59 else { throw DatetimeFunctions.Error.seconds(date, newSeconds).message }
  var components = date.components
  components.second = newSeconds
  guard let result = calendar.date(from: components) else {
    throw DatetimeFunctions.Error.components(makeFuncName(#function, date, newSeconds)).message
  }
  return result
}

private func _setMillis(date: Date, newMillis: Int) throws -> Date {
  guard newMillis >= 0,
        newMillis <= 999 else { throw DatetimeFunctions.Error.millis(date, newMillis).message }
  let newTimeInterval = round(date.timeIntervalSince1970) + newMillis.toSeconds
  return Date(timeIntervalSince1970: newTimeInterval)
}

private func _getYear(_ value: Date) throws -> Int {
  guard let year = value.components.year else {
    throw DatetimeFunctions.Error.component(makeFuncName(#function, value), "year").message
  }
  return year
}

private func _getMonth(_ value: Date) throws -> Int {
  guard let month = value.components.month else {
    throw DatetimeFunctions.Error.component(makeFuncName(#function, value), "month").message
  }
  return month
}

private func _getDay(_ value: Date) throws -> Int {
  guard let day = value.components.day else {
    throw DatetimeFunctions.Error.component(makeFuncName(#function, value), "day").message
  }
  return day
}

private func _getDayOfWeek(_ value: Date) throws -> Int {
  guard let weekday = value.components.weekday else {
    throw DatetimeFunctions.Error.component(makeFuncName(#function, value), "weekday").message
  }
  if weekday == 1 {
    return 7
  } else {
    return weekday - 1
  }
}

private func _getHours(_ value: Date) throws -> Int {
  guard let hour = value.components.hour else {
    throw DatetimeFunctions.Error.component(makeFuncName(#function, value), "hour").message
  }
  return hour
}

private func _getMinutes(_ value: Date) throws -> Int {
  guard let minute = value.components.minute else {
    throw DatetimeFunctions.Error.component(makeFuncName(#function, value), "minute").message
  }
  return minute
}

private func _getSeconds(_ value: Date) throws -> Int {
  guard let second = value.components.second else {
    throw DatetimeFunctions.Error.component(makeFuncName(#function, value), "second").message
  }
  return second
}

private func _getMillis(_ value: Date) -> Int {
  Int(value.timeIntervalSince1970.truncatingRemainder(dividingBy: 1) * 1000)
}

private func makeFuncName(_ funcName: String, _ date: Date, _ value: Int) -> String {
  "\(getFuncName(funcName))('\(date.formatString)', \(value))"
}

private func makeFuncName(_ funcName: String, _ date: Date) -> String {
  "\(getFuncName(funcName))('\(date.formatString)')"
}

private func getFuncName(_ funcName: String) -> String {
  guard funcName.count > 1 else { return funcName }
  let startIndex = funcName.index(after: funcName.startIndex)
  guard let endIndex = funcName.firstIndex(of: "(") else { return funcName }
  return String(funcName[startIndex...funcName.index(before: endIndex)])
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
    calendar.dateComponents(
      [.year, .month, .day, .weekday, .hour, .minute, .second, .nanosecond],
      from: self
    )
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
