// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivCornersRadiusTemplate: TemplateValue, TemplateDeserializable {
  public let bottomLeft: Field<Expression<Int>>? // constraint: number >= 0
  public let bottomRight: Field<Expression<Int>>? // constraint: number >= 0
  public let topLeft: Field<Expression<Int>>? // constraint: number >= 0
  public let topRight: Field<Expression<Int>>? // constraint: number >= 0

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    self.init(
      bottomLeft: try dictionary.getOptionalField("bottom-left"),
      bottomRight: try dictionary.getOptionalField("bottom-right"),
      topLeft: try dictionary.getOptionalField("top-left"),
      topRight: try dictionary.getOptionalField("top-right")
    )
  }

  init(
    bottomLeft: Field<Expression<Int>>? = nil,
    bottomRight: Field<Expression<Int>>? = nil,
    topLeft: Field<Expression<Int>>? = nil,
    topRight: Field<Expression<Int>>? = nil
  ) {
    self.bottomLeft = bottomLeft
    self.bottomRight = bottomRight
    self.topLeft = topLeft
    self.topRight = topRight
  }

  private static func resolveOnlyLinks(context: Context, parent: DivCornersRadiusTemplate?) -> DeserializationResult<DivCornersRadius> {
    let bottomLeftValue = parent?.bottomLeft?.resolveOptionalValue(context: context, validator: ResolvedValue.bottomLeftValidator) ?? .noValue
    let bottomRightValue = parent?.bottomRight?.resolveOptionalValue(context: context, validator: ResolvedValue.bottomRightValidator) ?? .noValue
    let topLeftValue = parent?.topLeft?.resolveOptionalValue(context: context, validator: ResolvedValue.topLeftValidator) ?? .noValue
    let topRightValue = parent?.topRight?.resolveOptionalValue(context: context, validator: ResolvedValue.topRightValidator) ?? .noValue
    let errors = mergeErrors(
      bottomLeftValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "bottom-left", level: .warning)) },
      bottomRightValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "bottom-right", level: .warning)) },
      topLeftValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "top-left", level: .warning)) },
      topRightValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "top-right", level: .warning)) }
    )
    let result = DivCornersRadius(
      bottomLeft: bottomLeftValue.value,
      bottomRight: bottomRightValue.value,
      topLeft: topLeftValue.value,
      topRight: topRightValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivCornersRadiusTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCornersRadius> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var bottomLeftValue: DeserializationResult<Expression<Int>> = parent?.bottomLeft?.value() ?? .noValue
    var bottomRightValue: DeserializationResult<Expression<Int>> = parent?.bottomRight?.value() ?? .noValue
    var topLeftValue: DeserializationResult<Expression<Int>> = parent?.topLeft?.value() ?? .noValue
    var topRightValue: DeserializationResult<Expression<Int>> = parent?.topRight?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "bottom-left":
        bottomLeftValue = deserialize(__dictValue, validator: ResolvedValue.bottomLeftValidator).merged(with: bottomLeftValue)
      case "bottom-right":
        bottomRightValue = deserialize(__dictValue, validator: ResolvedValue.bottomRightValidator).merged(with: bottomRightValue)
      case "top-left":
        topLeftValue = deserialize(__dictValue, validator: ResolvedValue.topLeftValidator).merged(with: topLeftValue)
      case "top-right":
        topRightValue = deserialize(__dictValue, validator: ResolvedValue.topRightValidator).merged(with: topRightValue)
      case parent?.bottomLeft?.link:
        bottomLeftValue = bottomLeftValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.bottomLeftValidator))
      case parent?.bottomRight?.link:
        bottomRightValue = bottomRightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.bottomRightValidator))
      case parent?.topLeft?.link:
        topLeftValue = topLeftValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.topLeftValidator))
      case parent?.topRight?.link:
        topRightValue = topRightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.topRightValidator))
      default: break
      }
    }
    let errors = mergeErrors(
      bottomLeftValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "bottom-left", level: .warning)) },
      bottomRightValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "bottom-right", level: .warning)) },
      topLeftValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "top-left", level: .warning)) },
      topRightValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "top-right", level: .warning)) }
    )
    let result = DivCornersRadius(
      bottomLeft: bottomLeftValue.value,
      bottomRight: bottomRightValue.value,
      topLeft: topLeftValue.value,
      topRight: topRightValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivCornersRadiusTemplate {
    return self
  }

  public func resolveParent(templates: Templates) throws -> DivCornersRadiusTemplate {
    return try mergedWithParent(templates: templates)
  }
}
