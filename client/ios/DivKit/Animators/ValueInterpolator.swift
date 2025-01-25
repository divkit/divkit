import Foundation
import VGSL

protocol BuildFromDivVariable {
  static func makeValueType(from: DivVariableValue) -> Self?
}

protocol ValueInterpolator {
  associatedtype ValueType: BuildFromDivVariable
  func interpolate(from: ValueType, to: ValueType, progress: CGFloat) -> ValueType
}

extension Double: BuildFromDivVariable {
  static func makeValueType(from value: DivVariableValue) -> Double? {
    switch value {
    case let .string(string):
      return Double(string)
    case let .number(double):
      return double
    case let .integer(int):
      return Double(int)
    case .bool, .color, .dict, .array, .url:
      break
    }
    return nil
  }
}

struct DoubleInterpolator: ValueInterpolator {
  typealias ValueType = Double
  func interpolate(from: Double, to: Double, progress: CGFloat) -> Double {
    from + (to - from) * progress
  }
}

extension Color: BuildFromDivVariable {
  static func makeValueType(from value: DivVariableValue) -> Color? {
    switch value {
    case let .string(string):
      return RGBAColor.color(withHexString: string)
    case let .color(color):
      return color
    case .number, .integer, .bool, .dict, .array, .url:
      break
    }
    return nil
  }
}

struct ColorInterpolator: ValueInterpolator {
  typealias ValueType = Color
  func interpolate(from: Color, to: Color, progress: CGFloat) -> Color {
    guard let fromComponents = from.cgColor.components, let toComponents = to.cgColor.components,
          fromComponents.count >= 3, toComponents.count >= 3 else {
      return from
    }

    let interpolatedComponents = zip(fromComponents, toComponents).map { from, to -> CGFloat in
      from + (to - from) * progress
    }

    return Color(
      red: interpolatedComponents[0],
      green: interpolatedComponents[1],
      blue: interpolatedComponents[2],
      alpha: interpolatedComponents.count > 3 ? interpolatedComponents[3] : 1.0
    )
  }
}
