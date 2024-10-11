// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivCornersRadiusTemplate: TemplateValue {
  public let bottomLeft: Field<Expression<Int>>? // constraint: number >= 0
  public let bottomRight: Field<Expression<Int>>? // constraint: number >= 0
  public let topLeft: Field<Expression<Int>>? // constraint: number >= 0
  public let topRight: Field<Expression<Int>>? // constraint: number >= 0

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      bottomLeft: dictionary.getOptionalExpressionField("bottom-left"),
      bottomRight: dictionary.getOptionalExpressionField("bottom-right"),
      topLeft: dictionary.getOptionalExpressionField("top-left"),
      topRight: dictionary.getOptionalExpressionField("top-right")
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivCornersRadiusTemplate?) -> DeserializationResult<DivCornersRadius> {
    let bottomLeftValue = { parent?.bottomLeft?.resolveOptionalValue(context: context, validator: ResolvedValue.bottomLeftValidator) ?? .noValue }()
    let bottomRightValue = { parent?.bottomRight?.resolveOptionalValue(context: context, validator: ResolvedValue.bottomRightValidator) ?? .noValue }()
    let topLeftValue = { parent?.topLeft?.resolveOptionalValue(context: context, validator: ResolvedValue.topLeftValidator) ?? .noValue }()
    let topRightValue = { parent?.topRight?.resolveOptionalValue(context: context, validator: ResolvedValue.topRightValidator) ?? .noValue }()
    let errors = mergeErrors(
      bottomLeftValue.errorsOrWarnings?.map { .nestedObjectError(field: "bottom-left", error: $0) },
      bottomRightValue.errorsOrWarnings?.map { .nestedObjectError(field: "bottom-right", error: $0) },
      topLeftValue.errorsOrWarnings?.map { .nestedObjectError(field: "top-left", error: $0) },
      topRightValue.errorsOrWarnings?.map { .nestedObjectError(field: "top-right", error: $0) }
    )
    let result = DivCornersRadius(
      bottomLeft: { bottomLeftValue.value }(),
      bottomRight: { bottomRightValue.value }(),
      topLeft: { topLeftValue.value }(),
      topRight: { topRightValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivCornersRadiusTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCornersRadius> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var bottomLeftValue: DeserializationResult<Expression<Int>> = { parent?.bottomLeft?.value() ?? .noValue }()
    var bottomRightValue: DeserializationResult<Expression<Int>> = { parent?.bottomRight?.value() ?? .noValue }()
    var topLeftValue: DeserializationResult<Expression<Int>> = { parent?.topLeft?.value() ?? .noValue }()
    var topRightValue: DeserializationResult<Expression<Int>> = { parent?.topRight?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "bottom-left" {
           bottomLeftValue = deserialize(__dictValue, validator: ResolvedValue.bottomLeftValidator).merged(with: bottomLeftValue)
          }
        }()
        _ = {
          if key == "bottom-right" {
           bottomRightValue = deserialize(__dictValue, validator: ResolvedValue.bottomRightValidator).merged(with: bottomRightValue)
          }
        }()
        _ = {
          if key == "top-left" {
           topLeftValue = deserialize(__dictValue, validator: ResolvedValue.topLeftValidator).merged(with: topLeftValue)
          }
        }()
        _ = {
          if key == "top-right" {
           topRightValue = deserialize(__dictValue, validator: ResolvedValue.topRightValidator).merged(with: topRightValue)
          }
        }()
        _ = {
         if key == parent?.bottomLeft?.link {
           bottomLeftValue = bottomLeftValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.bottomLeftValidator) })
          }
        }()
        _ = {
         if key == parent?.bottomRight?.link {
           bottomRightValue = bottomRightValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.bottomRightValidator) })
          }
        }()
        _ = {
         if key == parent?.topLeft?.link {
           topLeftValue = topLeftValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.topLeftValidator) })
          }
        }()
        _ = {
         if key == parent?.topRight?.link {
           topRightValue = topRightValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.topRightValidator) })
          }
        }()
      }
    }()
    let errors = mergeErrors(
      bottomLeftValue.errorsOrWarnings?.map { .nestedObjectError(field: "bottom-left", error: $0) },
      bottomRightValue.errorsOrWarnings?.map { .nestedObjectError(field: "bottom-right", error: $0) },
      topLeftValue.errorsOrWarnings?.map { .nestedObjectError(field: "top-left", error: $0) },
      topRightValue.errorsOrWarnings?.map { .nestedObjectError(field: "top-right", error: $0) }
    )
    let result = DivCornersRadius(
      bottomLeft: { bottomLeftValue.value }(),
      bottomRight: { bottomRightValue.value }(),
      topLeft: { topLeftValue.value }(),
      topRight: { topRightValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivCornersRadiusTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivCornersRadiusTemplate {
    return try mergedWithParent(templates: templates)
  }
}
