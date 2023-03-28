import Foundation
import UIKit
import BaseTinyPublic

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

extension ShimmerStyle: Deserializable {
  public init(dictionary: [String: Any]) throws {
    self.angle = try dictionary.getOptionalField("angle") ?? defaultAngle
    self.duration = try dictionary.getOptionalField("duration") ?? defaultDuration
    let colors: [Color] = try dictionary.getOptionalArray(
      "colors",
      transform: { RGBAColor.color(withHexString: $0) }
    ) ?? defaultColors
    let locations: [CGFloat] = try dictionary.getOptionalArray("locations")
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


private let fromColor: Color = RGBAColor.colorWithHexCode(0xE9E9E9FF)
private let toColor: Color = RGBAColor.colorWithHexCode(0xFBFBFBFF)

private enum ShimmerSerializationError: Error {
  case error
}
