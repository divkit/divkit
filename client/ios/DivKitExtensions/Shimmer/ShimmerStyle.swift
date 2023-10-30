import BaseTinyPublic
import DivKit
import Foundation
import UIKit

import Serialization

public struct ShimmerStyle {
  public let colorsAndLocations: [(Color, CGFloat)]
  public let angle: CGFloat
  public let duration: CGFloat

  public init(colorsAndLocations: [(Color, CGFloat)], angle: CGFloat, duration: CGFloat) {
    self.colorsAndLocations = colorsAndLocations
    self.angle = angle
    self.duration = duration
  }
}

extension ShimmerStyle {
  init(dictionary: [String: Any], expressionResolver: ExpressionResolver) throws {
    self.angle = try dictionary.getOptionalFloatAsExpression(
      "angle",
      expressionResolver: expressionResolver
    ) ?? defaultAngle
    self.duration = try dictionary.getOptionalFloatAsExpression(
      "duration",
      expressionResolver: expressionResolver
    ) ?? defaultDuration
    let colors: [Color] = try dictionary.getOptionalArray(
      "colors",
      transform: { RGBAColor.color(withHexString: expressionResolver.resolveString(expression: $0))
      }
    ) ?? defaultColors
    let locations: [CGFloat] = try dictionary.getOptionalFloatArrayElementsAsExpression(
      "locations",
      expressionResolver: expressionResolver
    )
      ?? defaultLocations

    if colors.count != locations.count {
      throw ShimmerSerializationError.error
    }

    self.colorsAndLocations = Array(zip(colors, locations))
  }
}

extension ShimmerStyle {
  public static let `default` = ShimmerStyle(
    colorsAndLocations: Array(zip(defaultColors, defaultLocations)),
    angle: defaultAngle,
    duration: defaultDuration
  )
}

private let defaultColors = [fromColor, toColor, fromColor]
private let defaultLocations: [CGFloat] = [0, 0.5, 1]
private let defaultAngle: CGFloat = 0
private let defaultDuration: CGFloat = 1

private let fromColor: Color = RGBAColor.colorWithHexCode(0xE9_E9_E9_FF)
private let toColor: Color = RGBAColor.colorWithHexCode(0xFB_FB_FB_FF)

private enum ShimmerSerializationError: Error {
  case error
}

extension Dictionary where Key == String {
  fileprivate func getOptionalFloatAsExpression(
    _ key: Key,
    expressionResolver: ExpressionResolver
  ) throws -> CGFloat? {
    let result: Double? = try getOptionalFieldAsExpression(
      key,
      fromExpression: { Double(expressionResolver.resolveString(expression: $0)) }
    )

    return result.map { CGFloat($0) }
  }

  fileprivate func getOptionalFloatArrayElementsAsExpression(
    _ key: Key,
    expressionResolver: ExpressionResolver
  ) throws -> [CGFloat]? {
    try getOptionalArrayElementsAsExpression(
      key,
      fromExpression: { Double(expressionResolver.resolveString(expression: $0)) }
    )?.map { CGFloat($0) }
  }

  fileprivate func getOptionalFieldAsExpression<T: ValidSerializationValue>(
    _ key: Key,
    fromExpression: (String) -> T?
  ) throws -> T? {
    if let value = self[key] as? T {
      return value
    } else {
      let expression: String? = try getOptionalField(key)
      return expression.flatMap(fromExpression)
    }
  }

  fileprivate func getOptionalArrayElementsAsExpression<T: ValidSerializationValue>(
    _ key: Key,
    fromExpression: (String) -> T?
  ) throws -> [T]? {
    try getOptionalArray(
      key,
      transform: { (obj: Any) -> T? in
        if let expression = obj as? String {
          return fromExpression(expression)
        } else {
          return obj as? T
        }
      },
      validator: nil
    )
  }
}
