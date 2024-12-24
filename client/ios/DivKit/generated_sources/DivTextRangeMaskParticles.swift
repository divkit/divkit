// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTextRangeMaskParticles {
  public static let type: String = "particles"
  public let color: Expression<Color>
  public let density: Expression<Double> // default value: 0.8
  public let isAnimated: Expression<Bool> // default value: false
  public let isEnabled: Expression<Bool> // default value: true
  public let particleSize: DivFixedSize // default value: DivFixedSize(value: .value(1))

  public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(color)
  }

  public func resolveDensity(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(density) ?? 0.8
  }

  public func resolveIsAnimated(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(isAnimated) ?? false
  }

  public func resolveIsEnabled(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(isEnabled) ?? true
  }

  init(
    color: Expression<Color>,
    density: Expression<Double>? = nil,
    isAnimated: Expression<Bool>? = nil,
    isEnabled: Expression<Bool>? = nil,
    particleSize: DivFixedSize? = nil
  ) {
    self.color = color
    self.density = density ?? .value(0.8)
    self.isAnimated = isAnimated ?? .value(false)
    self.isEnabled = isEnabled ?? .value(true)
    self.particleSize = particleSize ?? DivFixedSize(value: .value(1))
  }
}

#if DEBUG
extension DivTextRangeMaskParticles: Equatable {
  public static func ==(lhs: DivTextRangeMaskParticles, rhs: DivTextRangeMaskParticles) -> Bool {
    guard
      lhs.color == rhs.color,
      lhs.density == rhs.density,
      lhs.isAnimated == rhs.isAnimated
    else {
      return false
    }
    guard
      lhs.isEnabled == rhs.isEnabled,
      lhs.particleSize == rhs.particleSize
    else {
      return false
    }
    return true
  }
}
#endif

extension DivTextRangeMaskParticles: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["color"] = color.toValidSerializationValue()
    result["density"] = density.toValidSerializationValue()
    result["is_animated"] = isAnimated.toValidSerializationValue()
    result["is_enabled"] = isEnabled.toValidSerializationValue()
    result["particle_size"] = particleSize.toDictionary()
    return result
  }
}
