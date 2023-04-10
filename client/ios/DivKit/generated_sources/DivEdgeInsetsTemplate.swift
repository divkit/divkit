// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivEdgeInsetsTemplate: TemplateValue {
  public let bottom: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let left: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let right: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let top: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      bottom: try dictionary.getOptionalExpressionField("bottom"),
      left: try dictionary.getOptionalExpressionField("left"),
      right: try dictionary.getOptionalExpressionField("right"),
      top: try dictionary.getOptionalExpressionField("top"),
      unit: try dictionary.getOptionalExpressionField("unit")
    )
  }

  init(
    bottom: Field<Expression<Int>>? = nil,
    left: Field<Expression<Int>>? = nil,
    right: Field<Expression<Int>>? = nil,
    top: Field<Expression<Int>>? = nil,
    unit: Field<Expression<DivSizeUnit>>? = nil
  ) {
    self.bottom = bottom
    self.left = left
    self.right = right
    self.top = top
    self.unit = unit
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivEdgeInsetsTemplate?) -> DeserializationResult<DivEdgeInsets> {
    let bottomValue = parent?.bottom?.resolveOptionalValue(context: context, validator: ResolvedValue.bottomValidator) ?? .noValue
    let leftValue = parent?.left?.resolveOptionalValue(context: context, validator: ResolvedValue.leftValidator) ?? .noValue
    let rightValue = parent?.right?.resolveOptionalValue(context: context, validator: ResolvedValue.rightValidator) ?? .noValue
    let topValue = parent?.top?.resolveOptionalValue(context: context, validator: ResolvedValue.topValidator) ?? .noValue
    let unitValue = parent?.unit?.resolveOptionalValue(context: context, validator: ResolvedValue.unitValidator) ?? .noValue
    let errors = mergeErrors(
      bottomValue.errorsOrWarnings?.map { .nestedObjectError(field: "bottom", error: $0) },
      leftValue.errorsOrWarnings?.map { .nestedObjectError(field: "left", error: $0) },
      rightValue.errorsOrWarnings?.map { .nestedObjectError(field: "right", error: $0) },
      topValue.errorsOrWarnings?.map { .nestedObjectError(field: "top", error: $0) },
      unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) }
    )
    let result = DivEdgeInsets(
      bottom: bottomValue.value,
      left: leftValue.value,
      right: rightValue.value,
      top: topValue.value,
      unit: unitValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivEdgeInsetsTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivEdgeInsets> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var bottomValue: DeserializationResult<Expression<Int>> = parent?.bottom?.value() ?? .noValue
    var leftValue: DeserializationResult<Expression<Int>> = parent?.left?.value() ?? .noValue
    var rightValue: DeserializationResult<Expression<Int>> = parent?.right?.value() ?? .noValue
    var topValue: DeserializationResult<Expression<Int>> = parent?.top?.value() ?? .noValue
    var unitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.unit?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "bottom":
        bottomValue = deserialize(__dictValue, validator: ResolvedValue.bottomValidator).merged(with: bottomValue)
      case "left":
        leftValue = deserialize(__dictValue, validator: ResolvedValue.leftValidator).merged(with: leftValue)
      case "right":
        rightValue = deserialize(__dictValue, validator: ResolvedValue.rightValidator).merged(with: rightValue)
      case "top":
        topValue = deserialize(__dictValue, validator: ResolvedValue.topValidator).merged(with: topValue)
      case "unit":
        unitValue = deserialize(__dictValue, validator: ResolvedValue.unitValidator).merged(with: unitValue)
      case parent?.bottom?.link:
        bottomValue = bottomValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.bottomValidator))
      case parent?.left?.link:
        leftValue = leftValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.leftValidator))
      case parent?.right?.link:
        rightValue = rightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.rightValidator))
      case parent?.top?.link:
        topValue = topValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.topValidator))
      case parent?.unit?.link:
        unitValue = unitValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.unitValidator))
      default: break
      }
    }
    let errors = mergeErrors(
      bottomValue.errorsOrWarnings?.map { .nestedObjectError(field: "bottom", error: $0) },
      leftValue.errorsOrWarnings?.map { .nestedObjectError(field: "left", error: $0) },
      rightValue.errorsOrWarnings?.map { .nestedObjectError(field: "right", error: $0) },
      topValue.errorsOrWarnings?.map { .nestedObjectError(field: "top", error: $0) },
      unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) }
    )
    let result = DivEdgeInsets(
      bottom: bottomValue.value,
      left: leftValue.value,
      right: rightValue.value,
      top: topValue.value,
      unit: unitValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivEdgeInsetsTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivEdgeInsetsTemplate {
    return try mergedWithParent(templates: templates)
  }
}
