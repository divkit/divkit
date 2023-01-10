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

  var declaration: [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] {
    [.function(rawValue, arity: function.arity): function.symbolEvaluator]
  }

  var function: Function {
    switch self {
    case .getIntervalSeconds:
      return FunctionUnary(impl: _getIntervalSeconds)
    case .getIntervalTotalSeconds:
      return FunctionUnary(impl: _getIntervalTotalSeconds)
    case .getIntervalMinutes:
      return FunctionUnary(impl: _getIntervalMinutes)
    case .getIntervalTotalMinutes:
      return FunctionUnary(impl: _getIntervalTotalMinutes)
    case .getIntervalHours:
      return FunctionUnary(impl: _getIntervalHours)
    case .getIntervalTotalHours:
      return FunctionUnary(impl: _getIntervalTotalHours)
    case .getIntervalTotalDays:
      return FunctionUnary(impl: _getIntervalTotalDays)
    case .getIntervalTotalWeeks:
      return FunctionUnary(impl: _getIntervalTotalWeeks)
    }
  }

  private func _getIntervalSeconds(_ milliseconds: Int) throws -> Int {
    try getDuration(
      funcName: "getIntervalSeconds",
      milliseconds: milliseconds,
      divider: MS_IN_SECOND,
      whole: SECONDS_IN_MINUTE
    )
  }

  private func _getIntervalTotalSeconds(_ milliseconds: Int) throws -> Int {
    try getDuration(
      funcName: "getIntervalTotalSeconds",
      milliseconds: milliseconds,
      divider: MS_IN_SECOND
    )
  }

  private func _getIntervalMinutes(_ milliseconds: Int) throws -> Int {
    try getDuration(
      funcName: "getIntervalMinutes",
      milliseconds: milliseconds,
      divider: MS_IN_MINUTE,
      whole: MINUTES_IN_HOUR
    )
  }

  private func _getIntervalTotalMinutes(_ milliseconds: Int) throws -> Int {
    try getDuration(
      funcName: "getIntervalTotalMinutes",
      milliseconds: milliseconds,
      divider: MS_IN_MINUTE
    )
  }

  private func _getIntervalHours(_ milliseconds: Int) throws -> Int {
    try getDuration(
      funcName: "getIntervalHours",
      milliseconds: milliseconds,
      divider: MS_IN_HOUR,
      whole: HOURS_IN_DAY
    )
  }

  private func _getIntervalTotalHours(_ milliseconds: Int) throws -> Int {
    try getDuration(
      funcName: "getIntervalTotalHours",
      milliseconds: milliseconds,
      divider: MS_IN_HOUR
    )
  }

  private func _getIntervalTotalDays(_ milliseconds: Int) throws -> Int {
    try getDuration(
      funcName: "getIntervalTotalDays",
      milliseconds: milliseconds,
      divider: MS_IN_DAY
    )
  }

  private func _getIntervalTotalWeeks(_ milliseconds: Int) throws -> Int {
    try getDuration(
      funcName: "getIntervalTotalWeeks",
      milliseconds: milliseconds,
      divider: MS_IN_WEEK
    )
  }

  private func getDuration(
    funcName: String,
    milliseconds: Int,
    divider: Int,
    whole: Int? = nil
  ) throws -> Int {
    guard milliseconds >= 0 else {
      throw AnyCalcExpression.Error.incorrectMilliseconds("\(funcName)(\(milliseconds))")
    }
    var val = milliseconds / divider
    if let whole = whole {
      val = val % whole
    }
    return val
  }
}

extension AnyCalcExpression.Error {
  fileprivate static func incorrectMilliseconds(_ expression: String) -> AnyCalcExpression.Error {
    .message(
      "Failed to evaluate [\(expression)]. Expecting non-negative number of milliseconds."
    )
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
