import DivKit
import Foundation

extension Dictionary where Key == String {
  func getOptionalBool(
    _ key: Key,
    expressionResolver: ExpressionResolver
  ) throws -> Bool? {
    let result: Bool?
    if let value = self[key] as? Bool {
      result = value
    } else if let expression: String = try getOptionalField(key) {
      result = expressionResolver.resolve(expression) as? Bool
    } else {
      return nil
    }
    return result
  }

  func getOptionalFloat(
    _ key: Key,
    expressionResolver: ExpressionResolver
  ) throws -> CGFloat? {
    let result: Double?
    if let value = self[key] as? Double {
      result = value
    } else if let value = self[key] as? Int {
      result = Double(value)
    } else if let expression: String = try getOptionalField(key) {
      result = expressionResolver.resolveNumeric(expression)
    } else {
      return nil
    }
    return result.map { CGFloat($0) }
  }
}
