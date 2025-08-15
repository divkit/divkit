import Foundation

extension [String: Function] {
  mutating func addIntervalFunctions() {
    addFunction("getIntervalSeconds", _getIntervalSeconds)
    addFunction("getIntervalTotalSeconds", _getIntervalTotalSeconds)
    addFunction("getIntervalMinutes", _getIntervalMinutes)
    addFunction("getIntervalTotalMinutes", _getIntervalTotalMinutes)
    addFunction("getIntervalHours", _getIntervalHours)
    addFunction("getIntervalTotalHours", _getIntervalTotalHours)
    addFunction("getIntervalTotalDays", _getIntervalTotalDays)
    addFunction("getIntervalTotalWeeks", _getIntervalTotalWeeks)
  }
}

private let _getIntervalSeconds = FunctionUnary {
  try getDuration(milliseconds: $0, divider: MS_IN_SECOND, whole: SECONDS_IN_MINUTE)
}

private let _getIntervalTotalSeconds = FunctionUnary {
  try getDuration(milliseconds: $0, divider: MS_IN_SECOND)
}

private let _getIntervalMinutes = FunctionUnary {
  try getDuration(milliseconds: $0, divider: MS_IN_MINUTE, whole: MINUTES_IN_HOUR)
}

private let _getIntervalTotalMinutes = FunctionUnary {
  try getDuration(milliseconds: $0, divider: MS_IN_MINUTE)
}

private let _getIntervalHours = FunctionUnary {
  try getDuration(milliseconds: $0, divider: MS_IN_HOUR, whole: HOURS_IN_DAY)
}

private let _getIntervalTotalHours = FunctionUnary {
  try getDuration(milliseconds: $0, divider: MS_IN_HOUR)
}

private let _getIntervalTotalDays = FunctionUnary {
  try getDuration(milliseconds: $0, divider: MS_IN_DAY)
}

private let _getIntervalTotalWeeks = FunctionUnary {
  try getDuration(milliseconds: $0, divider: MS_IN_WEEK)
}

private func getDuration(
  milliseconds: Int,
  divider: Int,
  whole: Int? = nil
) throws -> Int {
  guard milliseconds >= 0 else {
    throw ExpressionError("Expecting non-negative number of milliseconds.")
  }
  var val = milliseconds / divider
  if let whole {
    val = val % whole
  }
  return val
}

private let MS_IN_SECOND = 1000
private let SECONDS_IN_MINUTE = 60
private let MS_IN_MINUTE = 1000 * 60
private let MINUTES_IN_HOUR = 60
private let MS_IN_HOUR = 1000 * 60 * 60
private let HOURS_IN_DAY = 24
private let MS_IN_DAY = 1000 * 60 * 60 * 24
private let MS_IN_WEEK = 1000 * 60 * 60 * 24 * 7
