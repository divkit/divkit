import Foundation
import VGSL

extension [String: Function] {
  mutating func addDateTimeFunctions() {
    addFunction("parseUnixTime", parseUnixTime)
    addFunction("parseUnixTimeAsLocal", parseUnixTimeAsLocal)
    addFunction("nowLocal", nowLocal)

    addFunction("addMillis", addMillis)

    addFunction("setYear", setYear)
    addFunction("setMonth", setMonth)
    addFunction("setDay", setDay)
    addFunction("setHours", setHours)
    addFunction("setMinutes", setMinutes)
    addFunction("setSeconds", setSeconds)
    addFunction("setMillis", setMillis)

    addFunction("getYear", getYear)
    addFunction("getMonth", getMonth)
    addFunction("getDay", getDay)
    addFunction("getDayOfWeek", getDayOfWeek)
    addFunction("getHours", getHours)
    addFunction("getMinutes", getMinutes)
    addFunction("getSeconds", getSeconds)
    addFunction("getMillis", getMillis)

    addFunction("formatDateAsLocal", formatDateAsLocal)
    addFunction("formatDateAsLocalWithLocale", formatDateAsLocalWithLocale)
    addFunction("formatDateAsUTC", formatDateAsUTC)
    addFunction("formatDateAsUTCWithLocale", formatDateAsUTCWithLocale)
  }
}

private let parseUnixTime = FunctionUnary<Int, Date> {
  Date(timeIntervalSince1970: Double($0))
}

private let parseUnixTimeAsLocal = FunctionUnary<Int, Date> {
  Date(timeIntervalSince1970: Double($0)).local
}

private let nowLocal = FunctionNullary<Date> {
  Date().local
}

private let addMillis = FunctionBinary<Date, Int, Date> { date, offsetInMillis in
  let newTimeInterval = date.timeIntervalSince1970 + offsetInMillis.toSeconds
  return Date(timeIntervalSince1970: newTimeInterval)
}

private let setYear = FunctionBinary<Date, Int, Date> { date, newYear in
  var components = date.components
  components.year = newYear
  guard let result = calendar.date(from: components) else {
    throw componentsError()
  }
  return result
}

private let setMonth = FunctionBinary<Date, Int, Date> { date, newMonth in
  guard newMonth >= 1, newMonth <= 12 else {
    throw ExpressionError("Expecting month in [1..12], instead got \(newMonth).")
  }
  var components = date.components
  components.month = newMonth
  guard let result = calendar.date(from: components) else {
    throw componentsError()
  }
  return result
}

private let setDay = FunctionBinary<Date, Int, Date> { date, newDay in
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
    throw ExpressionError("Unable to set day \(newDay) for date \(date.formatString).")
  }
  guard let result = calendar.date(from: components) else {
    throw componentsError()
  }
  return result
}

private let setHours = FunctionBinary<Date, Int, Date> { date, newHour in
  guard newHour >= 0, newHour <= 23 else {
    throw ExpressionError("Expecting hours in [0..23], instead got \(newHour).")
  }
  var components = date.components
  components.hour = newHour
  guard let result = calendar.date(from: components) else {
    throw componentsError()
  }
  return result
}

private let setMinutes = FunctionBinary<Date, Int, Date> { date, newMinutes in
  guard newMinutes >= 0, newMinutes <= 59 else {
    throw ExpressionError("Expecting minutes in [0..59], instead got \(newMinutes).")
  }
  var components = date.components
  components.minute = newMinutes
  guard let result = calendar.date(from: components) else {
    throw componentsError()
  }
  return result
}

private let setSeconds = FunctionBinary<Date, Int, Date> { date, newSeconds in
  guard newSeconds >= 0, newSeconds <= 59 else {
    throw ExpressionError("Expecting seconds in [0..59], instead got \(newSeconds).")
  }
  var components = date.components
  components.second = newSeconds
  guard let result = calendar.date(from: components) else {
    throw componentsError()
  }
  return result
}

private let setMillis = FunctionBinary<Date, Int, Date> { date, newMillis in
  guard newMillis >= 0, newMillis <= 999 else {
    throw ExpressionError("Expecting millis in [0..999], instead got \(newMillis).")
  }
  let newTimeInterval = round(date.timeIntervalSince1970) + newMillis.toSeconds
  return Date(timeIntervalSince1970: newTimeInterval)
}

private let getYear = FunctionUnary<Date, Int> {
  guard let year = $0.components.year else {
    throw componentError("year")
  }
  return year
}

private let getMonth = FunctionUnary<Date, Int> {
  guard let month = $0.components.month else {
    throw componentError("month")
  }
  return month
}

private let getDay = FunctionUnary<Date, Int> {
  guard let day = $0.components.day else {
    throw componentError("day")
  }
  return day
}

private let getDayOfWeek = FunctionUnary<Date, Int> {
  guard let weekday = $0.components.weekday else {
    throw componentError("weekday")
  }
  return weekday == 1 ? 7 : weekday - 1
}

private let getHours = FunctionUnary<Date, Int> {
  guard let hour = $0.components.hour else {
    throw componentError("hour")
  }
  return hour
}

private let getMinutes = FunctionUnary<Date, Int> {
  guard let minute = $0.components.minute else {
    throw componentError("minute")
  }
  return minute
}

private let getSeconds = FunctionUnary<Date, Int> {
  guard let second = $0.components.second else {
    throw componentError("second")
  }
  return second
}

private let getMillis = FunctionUnary<Date, Int> {
  Int($0.timeIntervalSince1970.truncatingRemainder(dividingBy: 1) * 1000)
}

private let formatDateAsLocal = FunctionBinary<Date, String, String> {
  makeDateFormatter($1, isUTC: false).string(from: $0)
}

private let formatDateAsLocalWithLocale = FunctionTernary<Date, String, String, String> {
  makeDateFormatter($1, isUTC: false, locale: $2).string(from: $0)
}

private let formatDateAsUTC = FunctionBinary<Date, String, String> {
  makeDateFormatter($1, isUTC: true).string(from: $0)
}

private let formatDateAsUTCWithLocale = FunctionTernary<Date, String, String, String> {
  makeDateFormatter($1, isUTC: true, locale: $2).string(from: $0)
}

private let dateFormat = "yyyy-MM-dd HH:mm:ss"
private let utcTimeZone = TimeZone(abbreviation: "UTC")!

extension String {
  func toDate() -> Date? {
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
  calendar.timeZone = utcTimeZone
  return calendar
}()

private func makeDateFormatter(
  _ format: String,
  isUTC: Bool,
  locale: String? = nil
) -> DateFormatter {
  let formatter = DateFormatter()
  formatter.dateFormat = format
  if isUTC {
    formatter.timeZone = utcTimeZone
  }
  if let locale {
    formatter.locale = Locale(identifier: locale)
  }
  return formatter
}

private func componentsError() -> Error {
  ExpressionError("Date components not found.")
}

private func componentError(_ name: String) -> Error {
  ExpressionError("Component '\(name)' not found.")
}
