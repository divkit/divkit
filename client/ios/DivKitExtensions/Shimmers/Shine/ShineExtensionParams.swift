import DivKit
import LayoutKitInterface
import UIKit
import VGSL

struct ShineExtensionParams: Equatable {
  var isEnabled: Bool
  var repetitions: CGFloat
  var interval: CGFloat
  var beginAfter: CGFloat
  var duration: CGFloat
  var angle: CGFloat
  var colorsAndLocations: [ColorAndLocation]
  var onCycleStartActions: [UserInterfaceAction]?
}

extension ShineExtensionParams {
  init(
    dictionary: [String: Any],
    context: DivBlockModelingContext
  ) throws {
    let expressionResolver = context.expressionResolver

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
    ) ?? defaultInterval

    self.beginAfter = try dictionary.getOptionalFloat(
      "delay",
      expressionResolver: expressionResolver
    ) ?? defaultBeginAfter

    self.duration = try dictionary.getOptionalFloat(
      "duration",
      expressionResolver: expressionResolver
    ) ?? defaultDuration

    self.angle = try dictionary.getOptionalFloat(
      "angle",
      expressionResolver: expressionResolver
    ) ?? defaultAngle

    self.colorsAndLocations = try dictionary.getOptionalColorsAndLocations(
      colorsKey: "colors",
      locationsKey: "locations",
      expressionResolver: expressionResolver
    ) ?? defaultColorsAndLocations

    self.onCycleStartActions = dictionary.makeDivActions(
      for: "on_cycle_start_actions"
    )?.uiActions(context: context)
  }
}

extension ShineExtensionParams {
  static let `default` = ShineExtensionParams(
    isEnabled: defaultIsEnabled,
    repetitions: defaultRepetitions,
    interval: defaultInterval,
    beginAfter: defaultBeginAfter,
    duration: defaultDuration,
    angle: defaultAngle,
    colorsAndLocations: defaultColorsAndLocations
  )
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
