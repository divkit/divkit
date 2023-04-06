// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivImageBackground {
  public static let type: String = "image"
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let contentAlignmentHorizontal: Expression<DivAlignmentHorizontal> // default value: center
  public let contentAlignmentVertical: Expression<DivAlignmentVertical> // default value: center
  public let filters: [DivFilter]? // at least 1 elements
  public let imageUrl: Expression<URL>
  public let preloadRequired: Expression<Bool> // default value: false
  public let scale: Expression<DivImageScale> // default value: fill

  public func resolveAlpha(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: alpha) ?? 1.0
  }

  public func resolveContentAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal {
    resolver.resolveStringBasedValue(expression: contentAlignmentHorizontal, initializer: DivAlignmentHorizontal.init(rawValue:)) ?? DivAlignmentHorizontal.center
  }

  public func resolveContentAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical {
    resolver.resolveStringBasedValue(expression: contentAlignmentVertical, initializer: DivAlignmentVertical.init(rawValue:)) ?? DivAlignmentVertical.center
  }

  public func resolveImageUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveStringBasedValue(expression: imageUrl, initializer: URL.init(string:))
  }

  public func resolvePreloadRequired(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: preloadRequired) ?? false
  }

  public func resolveScale(_ resolver: ExpressionResolver) -> DivImageScale {
    resolver.resolveStringBasedValue(expression: scale, initializer: DivImageScale.init(rawValue:)) ?? DivImageScale.fill
  }

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let contentAlignmentHorizontalValidator: AnyValueValidator<DivAlignmentHorizontal> =
    makeNoOpValueValidator()

  static let contentAlignmentVerticalValidator: AnyValueValidator<DivAlignmentVertical> =
    makeNoOpValueValidator()

  static let filtersValidator: AnyArrayValueValidator<DivFilter> =
    makeArrayValidator(minItems: 1)

  static let preloadRequiredValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let scaleValidator: AnyValueValidator<DivImageScale> =
    makeNoOpValueValidator()

  init(
    alpha: Expression<Double>? = nil,
    contentAlignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    contentAlignmentVertical: Expression<DivAlignmentVertical>? = nil,
    filters: [DivFilter]? = nil,
    imageUrl: Expression<URL>,
    preloadRequired: Expression<Bool>? = nil,
    scale: Expression<DivImageScale>? = nil
  ) {
    self.alpha = alpha ?? .value(1.0)
    self.contentAlignmentHorizontal = contentAlignmentHorizontal ?? .value(.center)
    self.contentAlignmentVertical = contentAlignmentVertical ?? .value(.center)
    self.filters = filters
    self.imageUrl = imageUrl
    self.preloadRequired = preloadRequired ?? .value(false)
    self.scale = scale ?? .value(.fill)
  }
}

#if DEBUG
extension DivImageBackground: Equatable {
  public static func ==(lhs: DivImageBackground, rhs: DivImageBackground) -> Bool {
    guard
      lhs.alpha == rhs.alpha,
      lhs.contentAlignmentHorizontal == rhs.contentAlignmentHorizontal,
      lhs.contentAlignmentVertical == rhs.contentAlignmentVertical
    else {
      return false
    }
    guard
      lhs.filters == rhs.filters,
      lhs.imageUrl == rhs.imageUrl,
      lhs.preloadRequired == rhs.preloadRequired
    else {
      return false
    }
    guard
      lhs.scale == rhs.scale
    else {
      return false
    }
    return true
  }
}
#endif

extension DivImageBackground: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["alpha"] = alpha.toValidSerializationValue()
    result["content_alignment_horizontal"] = contentAlignmentHorizontal.toValidSerializationValue()
    result["content_alignment_vertical"] = contentAlignmentVertical.toValidSerializationValue()
    result["filters"] = filters?.map { $0.toDictionary() }
    result["image_url"] = imageUrl.toValidSerializationValue()
    result["preload_required"] = preloadRequired.toValidSerializationValue()
    result["scale"] = scale.toValidSerializationValue()
    return result
  }
}
