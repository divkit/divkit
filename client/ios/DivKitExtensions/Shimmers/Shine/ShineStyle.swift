import UIKit

import DivKit
import VGSL

struct ShineStyle: Equatable {
  var isEnabled: Bool
  var repetitions: CGFloat
  var interval: CGFloat
  var beginAfter: CGFloat
  var duration: CGFloat
  var angle: CGFloat
  var colorsAndLocations: [ColorAndLocation]
}

extension ShineStyle {
  static let `default` = ShineStyle(
    isEnabled: defaultIsEnabled,
    repetitions: defaultRepetitions,
    interval: defaultInterval,
    beginAfter: defaultBeginAfter,
    duration: defaultDuration,
    angle: defaultAngle,
    colorsAndLocations: defaultColorsAndLocations
  )
}

extension ShineStyle {
  init(
    dictionary: [String: Any],
    expressionResolver: ExpressionResolver
  ) throws {
    self.isEnabled = try dictionary.getOptionalBool(
      "is_enabled",
      expressionResolver: expressionResolver
    ) ?? defaultIsEnabled

    self.repetitions = try dictionary.getOptionalFloat(
      "cycle_count",
      expressionResolver: expressionResolver
    ) ?? defaultRepetitions

    self.interval = try dictionary.getOptionalFloat(
      "interval",
      expressionResolver: expressionResolver
    )?.msToSeconds ?? defaultInterval

    self.beginAfter = try dictionary.getOptionalFloat(
      "delay",
      expressionResolver: expressionResolver
    )?.msToSeconds ?? defaultBeginAfter

    self.duration = try dictionary.getOptionalFloat(
      "duration",
      expressionResolver: expressionResolver
    )?.msToSeconds ?? defaultDuration

    self.angle = try dictionary.getOptionalFloat(
      "angle",
      expressionResolver: expressionResolver
    ) ?? defaultAngle

    self.colorsAndLocations = try dictionary.getOptionalColorsAndLocations(
      colorsKey: "colors",
      locationsKey: "locations",
      expressionResolver: expressionResolver
    ) ?? defaultColorsAndLocations
  }
}

extension CGFloat {
  fileprivate var msToSeconds: CGFloat {
    self / 1000
  }
}

private let defaultIsEnabled: Bool = true
private let defaultRepetitions: CGFloat = 0
private let defaultInterval: CGFloat = 0
private let defaultBeginAfter: CGFloat = 0
private let defaultDuration: CGFloat = 2
private let defaultAngle: CGFloat = 45
private let defaultColorsAndLocations: [ColorAndLocation] = [
  ColorAndLocation(color: fromColor, location: 0.25),
  ColorAndLocation(color: toColor, location: 0.5),
  ColorAndLocation(color: fromColor, location: 0.75),
]

private let fromColor: Color = RGBAColor.clear
private let toColor: Color = RGBAColor.white
