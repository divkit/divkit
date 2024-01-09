import Foundation
import UIKit

import BaseTinyPublic
import DivKit
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
    self.angle = try dictionary.getOptionalFloat(
      "angle",
      expressionResolver: expressionResolver
    ) ?? defaultAngle

    self.duration = try dictionary.getOptionalFloat(
      "duration",
      expressionResolver: expressionResolver
    ) ?? defaultDuration

    let colors: [Color] = try dictionary.getOptionalArray(
      "colors",
      transform: { expressionResolver.resolveColor($0) }
    ) ?? defaultColors

    let locations: [CGFloat] = try dictionary.getOptionalFloatArray(
      "locations",
      expressionResolver: expressionResolver
    ) ?? defaultLocations

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
  fileprivate func getOptionalFloat(
    _ key: Key,
    expressionResolver: ExpressionResolver
  ) throws -> CGFloat? {
    let result: Double?
    if let value = self[key] as? Double {
      result = value
    } else if let expression: String = try getOptionalField(key) {
      result = expressionResolver.resolveNumeric(expression)
    } else {
      return nil
    }
    return result.map { CGFloat($0) }
  }

  fileprivate func getOptionalFloatArray(
    _ key: Key,
    expressionResolver: ExpressionResolver
  ) throws -> [CGFloat]? {
    let result: [Double]? = try getOptionalArray(
      key,
      transform: { (obj: Any) -> Double? in
        if let expression = obj as? String {
          return expressionResolver.resolveNumeric(expression)
        }
        return obj as? Double
      }
    )
    return result?.map { CGFloat($0) }
  }
}
