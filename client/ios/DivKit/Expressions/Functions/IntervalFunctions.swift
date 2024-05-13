import Foundation

enum IntervalFunctions: String, CaseIterable {
  case getIntervalSeconds
  case getIntervalTotalSeconds
  case getIntervalMinutes
  case getIntervalTotalMinutes
  case getIntervalHours
  case getIntervalTotalHours
  case getIntervalTotalDays
  case getIntervalTotalWeeks

  var function: Function {
    switch self {
    case .getIntervalSeconds:
      FunctionUnary(impl: _getIntervalSeconds)
    case .getIntervalTotalSeconds:
      FunctionUnary(impl: _getIntervalTotalSeconds)
    case .getIntervalMinutes:
      FunctionUnary(impl: _getIntervalMinutes)
    case .getIntervalTotalMinutes:
      FunctionUnary(impl: _getIntervalTotalMinutes)
    case .getIntervalHours:
      FunctionUnary(impl: _getIntervalHours)
    case .getIntervalTotalHours:
      FunctionUnary(impl: _getIntervalTotalHours)
    case .getIntervalTotalDays:
      FunctionUnary(impl: _getIntervalTotalDays)
    case .getIntervalTotalWeeks:
      FunctionUnary(impl: _getIntervalTotalWeeks)
    }
  }

  private func _getIntervalSeconds(_ milliseconds: Int) throws -> Int {
    try getDuration(
      milliseconds: milliseconds,
      divider: MS_IN_SECOND,
      whole: SECONDS_IN_MINUTE
    )
  }

  private func _getIntervalTotalSeconds(_ milliseconds: Int) throws -> Int {
    try getDuration(milliseconds: milliseconds, divider: MS_IN_SECOND)
  }

  private func _getIntervalMinutes(_ milliseconds: Int) throws -> Int {
    try getDuration(
      milliseconds: milliseconds,
      divider: MS_IN_MINUTE,
      whole: MINUTES_IN_HOUR
    )
  }

  private func _getIntervalTotalMinutes(_ milliseconds: Int) throws -> Int {
    try getDuration(milliseconds: milliseconds, divider: MS_IN_MINUTE)
  }

  private func _getIntervalHours(_ milliseconds: Int) throws -> Int {
    try getDuration(
      milliseconds: milliseconds,
      divider: MS_IN_HOUR,
      whole: HOURS_IN_DAY
    )
  }

  private func _getIntervalTotalHours(_ milliseconds: Int) throws -> Int {
    try getDuration(milliseconds: milliseconds, divider: MS_IN_HOUR)
  }

  private func _getIntervalTotalDays(_ milliseconds: Int) throws -> Int {
    try getDuration(milliseconds: milliseconds, divider: MS_IN_DAY)
  }

  private func _getIntervalTotalWeeks(_ milliseconds: Int) throws -> Int {
    try getDuration(milliseconds: milliseconds, divider: MS_IN_WEEK)
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
}

private let MS_IN_SECOND = 1000
private let SECONDS_IN_MINUTE = 60
private let MS_IN_MINUTE = 1000 * 60
private let MINUTES_IN_HOUR = 60
private let MS_IN_HOUR = 1000 * 60 * 60
private let HOURS_IN_DAY = 24
private let MS_IN_DAY = 1000 * 60 * 60 * 24
private let MS_IN_WEEK = 1000 * 60 * 60 * 24 * 7
