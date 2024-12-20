import DivKit
import UIKit
import VGSL

public struct ShimmerImagePreviewStyle: Equatable {
  public let colorsAndLocations: [ColorAndLocation]
  public let angle: CGFloat
  public let duration: CGFloat

  public init(
    colorsAndLocations: [ColorAndLocation],
    angle: CGFloat,
    duration: CGFloat
  ) {
    self.colorsAndLocations = colorsAndLocations
    self.angle = angle
    self.duration = duration
  }
}

extension ShimmerImagePreviewStyle {
  public static let `default` = ShimmerImagePreviewStyle(
    colorsAndLocations: defaultColorsAndLocations,
    angle: defaultAngle,
    duration: defaultDuration
  )
}

extension ShimmerImagePreviewStyle {
  init(dictionary: [String: Any], expressionResolver: ExpressionResolver) throws {
    self.angle = try dictionary.getOptionalFloat(
      "angle",
      expressionResolver: expressionResolver
    ) ?? defaultAngle

    self.duration = try dictionary.getOptionalFloat(
      "duration",
      expressionResolver: expressionResolver
    ) ?? defaultDuration

    self.colorsAndLocations = try dictionary.getOptionalColorsAndLocations(
      colorsKey: "colors",
      locationsKey: "locations",
      expressionResolver: expressionResolver
    ) ?? defaultColorsAndLocations
  }
}

private let defaultColorsAndLocations: [ColorAndLocation] = [
  ColorAndLocation(color: fromColor, location: 0),
  ColorAndLocation(color: toColor, location: 0.5),
  ColorAndLocation(color: fromColor, location: 1),
]
private let defaultAngle: CGFloat = 0
private let defaultDuration: CGFloat = 1

private let fromColor: Color = RGBAColor.colorWithHexCode(0xE9_E9_E9_FF)
private let toColor: Color = RGBAColor.colorWithHexCode(0xFB_FB_FB_FF)
