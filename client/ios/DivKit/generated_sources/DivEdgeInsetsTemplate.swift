// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivEdgeInsetsTemplate: TemplateValue {
  public let bottom: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let end: Field<Expression<Int>>? // constraint: number >= 0
  public let left: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let right: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let start: Field<Expression<Int>>? // constraint: number >= 0
  public let top: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      bottom: try dictionary.getOptionalExpressionField("bottom"),
      end: try dictionary.getOptionalExpressionField("end"),
      left: try dictionary.getOptionalExpressionField("left"),
      right: try dictionary.getOptionalExpressionField("right"),
      start: try dictionary.getOptionalExpressionField("start"),
      top: try dictionary.getOptionalExpressionField("top"),
      unit: try dictionary.getOptionalExpressionField("unit")
    )
  }

  init(
    bottom: Field<Expression<Int>>? = nil,
    end: Field<Expression<Int>>? = nil,
    left: Field<Expression<Int>>? = nil,
    right: Field<Expression<Int>>? = nil,
    start: Field<Expression<Int>>? = nil,
    top: Field<Expression<Int>>? = nil,
    unit: Field<Expression<DivSizeUnit>>? = nil
  ) {
    self.bottom = bottom
    self.end = end
    self.left = left
    self.right = right
    self.start = start
    self.top = top
    self.unit = unit
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivEdgeInsetsTemplate?) -> DeserializationResult<DivEdgeInsets> {
    let bottomValue = parent?.bottom?.resolveOptionalValue(context: context, validator: ResolvedValue.bottomValidator) ?? .noValue
    let endValue = parent?.end?.resolveOptionalValue(context: context, validator: ResolvedValue.endValidator) ?? .noValue
    let leftValue = parent?.left?.resolveOptionalValue(context: context, validator: ResolvedValue.leftValidator) ?? .noValue
    let rightValue = parent?.right?.resolveOptionalValue(context: context, validator: ResolvedValue.rightValidator) ?? .noValue
    let startValue = parent?.start?.resolveOptionalValue(context: context, validator: ResolvedValue.startValidator) ?? .noValue
    let topValue = parent?.top?.resolveOptionalValue(context: context, validator: ResolvedValue.topValidator) ?? .noValue
    let unitValue = parent?.unit?.resolveOptionalValue(context: context) ?? .noValue
    let errors = mergeErrors(
      bottomValue.errorsOrWarnings?.map { .nestedObjectError(field: "bottom", error: $0) },
      endValue.errorsOrWarnings?.map { .nestedObjectError(field: "end", error: $0) },
      leftValue.errorsOrWarnings?.map { .nestedObjectError(field: "left", error: $0) },
      rightValue.errorsOrWarnings?.map { .nestedObjectError(field: "right", error: $0) },
      startValue.errorsOrWarnings?.map { .nestedObjectError(field: "start", error: $0) },
      topValue.errorsOrWarnings?.map { .nestedObjectError(field: "top", error: $0) },
      unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) }
    )
    let result = DivEdgeInsets(
      bottom: bottomValue.value,
      end: endValue.value,
      left: leftValue.value,
      right: rightValue.value,
      start: startValue.value,
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
    var endValue: DeserializationResult<Expression<Int>> = parent?.end?.value() ?? .noValue
    var leftValue: DeserializationResult<Expression<Int>> = parent?.left?.value() ?? .noValue
    var rightValue: DeserializationResult<Expression<Int>> = parent?.right?.value() ?? .noValue
    var startValue: DeserializationResult<Expression<Int>> = parent?.start?.value() ?? .noValue
    var topValue: DeserializationResult<Expression<Int>> = parent?.top?.value() ?? .noValue
    var unitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.unit?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "bottom":
        bottomValue = deserialize(__dictValue, validator: ResolvedValue.bottomValidator).merged(with: bottomValue)
      case "end":
        endValue = deserialize(__dictValue, validator: ResolvedValue.endValidator).merged(with: endValue)
      case "left":
        leftValue = deserialize(__dictValue, validator: ResolvedValue.leftValidator).merged(with: leftValue)
      case "right":
        rightValue = deserialize(__dictValue, validator: ResolvedValue.rightValidator).merged(with: rightValue)
      case "start":
        startValue = deserialize(__dictValue, validator: ResolvedValue.startValidator).merged(with: startValue)
      case "top":
        topValue = deserialize(__dictValue, validator: ResolvedValue.topValidator).merged(with: topValue)
      case "unit":
        unitValue = deserialize(__dictValue).merged(with: unitValue)
      case parent?.bottom?.link:
        bottomValue = bottomValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.bottomValidator))
      case parent?.end?.link:
        endValue = endValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.endValidator))
      case parent?.left?.link:
        leftValue = leftValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.leftValidator))
      case parent?.right?.link:
        rightValue = rightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.rightValidator))
      case parent?.start?.link:
        startValue = startValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.startValidator))
      case parent?.top?.link:
        topValue = topValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.topValidator))
      case parent?.unit?.link:
        unitValue = unitValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    let errors = mergeErrors(
      bottomValue.errorsOrWarnings?.map { .nestedObjectError(field: "bottom", error: $0) },
      endValue.errorsOrWarnings?.map { .nestedObjectError(field: "end", error: $0) },
      leftValue.errorsOrWarnings?.map { .nestedObjectError(field: "left", error: $0) },
      rightValue.errorsOrWarnings?.map { .nestedObjectError(field: "right", error: $0) },
      startValue.errorsOrWarnings?.map { .nestedObjectError(field: "start", error: $0) },
      topValue.errorsOrWarnings?.map { .nestedObjectError(field: "top", error: $0) },
      unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) }
    )
    let result = DivEdgeInsets(
      bottom: bottomValue.value,
      end: endValue.value,
      left: leftValue.value,
      right: rightValue.value,
      start: startValue.value,
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
