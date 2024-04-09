import Foundation

import CommonCorePublic

enum DatetimeFunctions: String, CaseIterable {
  case parseUnixTime
  case parseUnixTimeAsLocal
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
  case formatDateAsLocal
  case formatDateAsLocalWithLocale
  case formatDateAsUTC
  case formatDateAsUTCWithLocale

  var function: Function {
    switch self {
    case .parseUnixTime:
      FunctionUnary(impl: _parseUnixTime)
    case .parseUnixTimeAsLocal:
      FunctionUnary(impl: _parseUnixTimeAsLocal)
    case .nowLocal:
      FunctionNullary(impl: _nowLocal)
    case .addMillis:
      FunctionBinary(impl: _addMillis)
    case .setYear:
      FunctionBinary(impl: _setYear)
    case .setMonth:
      FunctionBinary(impl: _setMonth)
    case .setDay:
      FunctionBinary(impl: _setDay)
    case .setHours:
      FunctionBinary(impl: _setHours)
    case .setMinutes:
      FunctionBinary(impl: _setMinutes)
    case .setSeconds:
      FunctionBinary(impl: _setSeconds)
    case .setMillis:
      FunctionBinary(impl: _setMillis)
    case .getYear:
      FunctionUnary(impl: _getYear)
    case .getMonth:
      FunctionUnary(impl: _getMonth)
    case .getDay:
      FunctionUnary(impl: _getDay)
    case .getDayOfWeek:
      FunctionUnary(impl: _getDayOfWeek)
    case .getHours:
      FunctionUnary(impl: _getHours)
    case .getMinutes:
      FunctionUnary(impl: _getMinutes)
    case .getSeconds:
      FunctionUnary(impl: _getSeconds)
    case .getMillis:
      FunctionUnary(impl: _getMillis)
    case .formatDateAsLocal:
      FunctionBinary(impl: _formatDateAsLocal)
    case .formatDateAsLocalWithLocale:
      FunctionTernary(impl: _formatDateAsLocalWithLocale)
    case .formatDateAsUTC:
      FunctionBinary(impl: _formatDateAsUTC)
    case .formatDateAsUTCWithLocale:
      FunctionTernary(impl: _formatDateAsUTCWithLocale)
    }
  }
}

private func _parseUnixTime(_ value: Int) -> Date {
  Date(timeIntervalSince1970: Double(value))
}

private func _parseUnixTimeAsLocal(_ value: Int) -> Date {
  Date(timeIntervalSince1970: Double(value)).local
}

private func _nowLocal() -> Date {
  Date().local
}

private func _addMillis(_ date: Date, _ offsetInMillis: Int) -> Date {
  let newTimeInterval = date.timeIntervalSince1970 + offsetInMillis.toSeconds
  return Date(timeIntervalSince1970: newTimeInterval)
}

private func _setYear(date: Date, newYear: Int) throws -> Date {
  var components = date.components
  components.year = newYear
  guard let result = calendar.date(from: components) else {
    throw componentsError()
  }
  return result
}

private func _setMonth(date: Date, newMonth: Int) throws -> Date {
  guard newMonth >= 1, newMonth <= 12 else {
    throw CalcExpression.Error.message("Expecting month in [1..12], instead got \(newMonth).")
  }
  var components = date.components
  components.month = newMonth
  guard let result = calendar.date(from: components) else {
    throw componentsError()
  }
  return result
}

private func _setDay(date: Date, newDay: Int) throws -> Date {
  guard let range = calendar.range(of: .day, in: .month, for: date) else {
    throw componentsError()
  }
  var components = date.components
  switch newDay {
  case range:
    components.day = newDay
  case -1:
    components.day = 0
  default:
    throw CalcExpression.Error.message(
      "Unable to set day \(newDay) for date \(date.formatString)."
    )
  }
  guard let result = calendar.date(from: components) else {
    throw componentsError()
  }
  return result
}

private func _setHours(date: Date, newHour: Int) throws -> Date {
  guard newHour >= 0, newHour <= 23 else {
    throw CalcExpression.Error.message("Expecting hours in [0..23], instead got \(newHour).")
  }
  var components = date.components
  components.hour = newHour
  guard let result = calendar.date(from: components) else {
    throw componentsError()
  }
  return result
}

private func _setMinutes(date: Date, newMinutes: Int) throws -> Date {
  guard newMinutes >= 0, newMinutes <= 59 else {
    throw CalcExpression.Error.message("Expecting minutes in [0..59], instead got \(newMinutes).")
  }
  var components = date.components
  components.minute = newMinutes
  guard let result = calendar.date(from: components) else {
    throw componentsError()
  }
  return result
}

private func _setSeconds(date: Date, newSeconds: Int) throws -> Date {
  guard newSeconds >= 0, newSeconds <= 59 else {
    throw CalcExpression.Error.message("Expecting seconds in [0..59], instead got \(newSeconds).")
  }
  var components = date.components
  components.second = newSeconds
  guard let result = calendar.date(from: components) else {
    throw componentsError()
  }
  return result
}

private func _setMillis(date: Date, newMillis: Int) throws -> Date {
  guard newMillis >= 0, newMillis <= 999 else {
    throw CalcExpression.Error.message("Expecting millis in [0..999], instead got \(newMillis).")
  }
  let newTimeInterval = round(date.timeIntervalSince1970) + newMillis.toSeconds
  return Date(timeIntervalSince1970: newTimeInterval)
}

private func _getYear(_ value: Date) throws -> Int {
  guard let year = value.components.year else {
    throw componentError("year")
  }
  return year
}

private func _getMonth(_ value: Date) throws -> Int {
  guard let month = value.components.month else {
    throw componentError("month")
  }
  return month
}

private func _getDay(_ value: Date) throws -> Int {
  guard let day = value.components.day else {
    throw componentError("day")
  }
  return day
}

private func _getDayOfWeek(_ value: Date) throws -> Int {
  guard let weekday = value.components.weekday else {
    throw componentError("weekday")
  }
  return weekday == 1 ? 7 : weekday - 1
}

private func _getHours(_ value: Date) throws -> Int {
  guard let hour = value.components.hour else {
    throw componentError("hour")
  }
  return hour
}

private func _getMinutes(_ value: Date) throws -> Int {
  guard let minute = value.components.minute else {
    throw componentError("minute")
  }
  return minute
}

private func _getSeconds(_ value: Date) throws -> Int {
  guard let second = value.components.second else {
    throw componentError("second")
  }
  return second
}

private func _getMillis(_ value: Date) -> Int {
  Int(value.timeIntervalSince1970.truncatingRemainder(dividingBy: 1) * 1000)
}

private func _formatDateAsLocal(_ value: Date, _ format: String) throws -> String {
  try formatDate(value, format)
}

private func _formatDateAsLocalWithLocale(
  value: Date,
  format: String,
  locale: String
) throws -> String {
  try formatDate(value, format, locale: locale)
}

private func _formatDateAsUTC(_ value: Date, _ format: String) throws -> String {
  try formatDate(value, format, isUTC: true)
}

private func _formatDateAsUTCWithLocale(
  value: Date,
  format: String,
  locale: String
) throws -> String {
  try formatDate(value, format, isUTC: true, locale: locale)
}

private func formatDate(
  _ value: Date,
  _ format: String,
  isUTC: Bool = false,
  locale: String? = nil
) throws -> String {
  guard !format.contains("Z"), !format.contains("z") else {
    throw CalcExpression.Error.message("z/Z not supported in [\(format)].")
  }
  return makeDateFormatter(format, isUTC: isUTC, locale: locale).string(from: value)
}

private let dateFormat = "yyyy-MM-dd HH:mm:ss"
private let timeZone = "UTC"

extension String {
  func toDatetime() -> Date? {
    makeDateFormatter(dateFormat, isUTC: true).date(from: self)
  }
}

extension Date {
  var formatString: String {
    makeDateFormatter(dateFormat, isUTC: true).string(from: self)
  }

  fileprivate var components: DateComponents {
    calendar.dateComponents(
      [.year, .month, .day, .weekday, .hour, .minute, .second, .nanosecond],
      from: self
    )
  }

  fileprivate var local: Date {
    let timeZoneOffset = Double(TimeZone.current.secondsFromGMT(for: self))
    guard let localDate = Calendar.current.date(
      byAdding: .second,
      value: Int(timeZoneOffset),
      to: self
    ) else {
      return self
    }
    return localDate
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

private func makeDateFormatter(
  _ format: String,
  isUTC: Bool = false,
  locale: String? = nil
) -> DateFormatter {
  let dateFormatter = DateFormatter()
  dateFormatter.dateFormat = format.replacingOccurrences(of: "u", with: "e")
  if isUTC {
    dateFormatter.timeZone = TimeZone(abbreviation: timeZone)
  }
  if let locale {
    dateFormatter.locale = Locale(identifier: locale)
  }
  return dateFormatter
}

private func componentsError() -> CalcExpression.Error {
  .message("Date components not found.")
}

private func componentError(_ name: String) -> CalcExpression.Error {
  .message("Component '\(name)' not found.")
}
