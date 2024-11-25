import Foundation

import DivKit
import VGSL

public struct ColorAndLocation: Equatable {
  var color: Color
  var location: CGFloat

  public init(color: Color, location: CGFloat) {
    self.color = color
    self.location = location
  }
}

extension DivBase {
  func resolveExtensionParams(for extensionId: String) -> [String: Any]? {
    extensions?.first(where: { $0.id == extensionId })?.params
  }
}

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

  func getOptionalColorArray(
    _ key: Key,
    expressionResolver: ExpressionResolver
  ) throws -> [Color]? {
    try getOptionalArray(
      key,
      transform: { expressionResolver.resolveColor($0) }
    )
  }

  func getOptionalFloatArray(
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

  func getOptionalColorsAndLocations(
    colorsKey: Key,
    locationsKey: Key,
    expressionResolver: ExpressionResolver
  ) throws -> [ColorAndLocation]? {
    let colors: [Color]? = try getOptionalColorArray(
      colorsKey,
      expressionResolver: expressionResolver
    )
    let locations: [CGFloat]? = try getOptionalFloatArray(
      locationsKey,
      expressionResolver: expressionResolver
    )
    guard let colors, let locations else {
      return nil
    }
    guard colors.count == locations.count else {
      throw ShimmerSerializationError.invalidColorsAndLocationsCount
    }
    return zip(colors, locations).map(ColorAndLocation.init)
  }
}

private enum ShimmerSerializationError: Error {
  case invalidColorsAndLocationsCount
}
