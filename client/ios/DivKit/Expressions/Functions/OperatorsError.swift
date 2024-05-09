import Foundation

enum OperatorsError {
  static func unsupportedType(symbol: String, args: [Any]) -> CalcExpression.Error {
    let lhs = args[0]
    let rhs = args[1]
    let message =
      "Failed to evaluate [\(formatArgForError(lhs)) \(symbol) \(formatArgForError(rhs))]."
    let lhsType = formatTypeForError(lhs)
    let rhsType = formatTypeForError(rhs)
    if lhsType == rhsType {
      return .message(
        "\(message) Operator '\(symbol)' cannot be applied to \(lhsType) type."
      )
    }
    return .message(
      "\(message) Operator '\(symbol)' cannot be applied to different types: \(lhsType) and \(rhsType)."
    )
  }
}
