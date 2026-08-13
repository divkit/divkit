// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivEdgeInsetsTemplate: TemplateValue, Sendable {
  public let bottom: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let end: Field<Expression<Int>>? // constraint: number >= 0
  public let left: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let right: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let start: Field<Expression<Int>>? // constraint: number >= 0
  public let top: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      bottom: dictionary.getOptionalExpressionField("bottom"),
      end: dictionary.getOptionalExpressionField("end"),
      left: dictionary.getOptionalExpressionField("left"),
      right: dictionary.getOptionalExpressionField("right"),
      start: dictionary.getOptionalExpressionField("start"),
      top: dictionary.getOptionalExpressionField("top"),
      unit: dictionary.getOptionalExpressionField("unit")
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
    let bottomValue = { parent?.bottom?.resolveOptionalValue(context: context, validator: ResolvedValue.bottomValidator) ?? .noValue }()
    let endValue = { parent?.end?.resolveOptionalValue(context: context, validator: ResolvedValue.endValidator) ?? .noValue }()
    let leftValue = { parent?.left?.resolveOptionalValue(context: context, validator: ResolvedValue.leftValidator) ?? .noValue }()
    let rightValue = { parent?.right?.resolveOptionalValue(context: context, validator: ResolvedValue.rightValidator) ?? .noValue }()
    let startValue = { parent?.start?.resolveOptionalValue(context: context, validator: ResolvedValue.startValidator) ?? .noValue }()
    let topValue = { parent?.top?.resolveOptionalValue(context: context, validator: ResolvedValue.topValidator) ?? .noValue }()
    let unitValue = { parent?.unit?.resolveOptionalValue(context: context) ?? .noValue }()
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
      bottom: { bottomValue.value }(),
      end: { endValue.value }(),
      left: { leftValue.value }(),
      right: { rightValue.value }(),
      start: { startValue.value }(),
      top: { topValue.value }(),
      unit: { unitValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivEdgeInsetsTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivEdgeInsets> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var bottomValue: DeserializationResult<Expression<Int>> = { parent?.bottom?.value() ?? .noValue }()
    var endValue: DeserializationResult<Expression<Int>> = { parent?.end?.value() ?? .noValue }()
    var leftValue: DeserializationResult<Expression<Int>> = { parent?.left?.value() ?? .noValue }()
    var rightValue: DeserializationResult<Expression<Int>> = { parent?.right?.value() ?? .noValue }()
    var startValue: DeserializationResult<Expression<Int>> = { parent?.start?.value() ?? .noValue }()
    var topValue: DeserializationResult<Expression<Int>> = { parent?.top?.value() ?? .noValue }()
    var unitValue: DeserializationResult<Expression<DivSizeUnit>> = { parent?.unit?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "bottom" {
           bottomValue = deserialize(__dictValue, validator: ResolvedValue.bottomValidator).merged(with: bottomValue)
          }
        }()
        _ = {
          if key == "end" {
           endValue = deserialize(__dictValue, validator: ResolvedValue.endValidator).merged(with: endValue)
          }
        }()
        _ = {
          if key == "left" {
           leftValue = deserialize(__dictValue, validator: ResolvedValue.leftValidator).merged(with: leftValue)
          }
        }()
        _ = {
          if key == "right" {
           rightValue = deserialize(__dictValue, validator: ResolvedValue.rightValidator).merged(with: rightValue)
          }
        }()
        _ = {
          if key == "start" {
           startValue = deserialize(__dictValue, validator: ResolvedValue.startValidator).merged(with: startValue)
          }
        }()
        _ = {
          if key == "top" {
           topValue = deserialize(__dictValue, validator: ResolvedValue.topValidator).merged(with: topValue)
          }
        }()
        _ = {
          if key == "unit" {
           unitValue = deserialize(__dictValue).merged(with: unitValue)
          }
        }()
        _ = {
         if key == parent?.bottom?.link, context.templateData["bottom"] == nil {
           bottomValue = deserialize(__dictValue, validator: ResolvedValue.bottomValidator).orFallback(bottomValue)
          }
        }()
        _ = {
         if key == parent?.end?.link, context.templateData["end"] == nil {
           endValue = deserialize(__dictValue, validator: ResolvedValue.endValidator).orFallback(endValue)
          }
        }()
        _ = {
         if key == parent?.left?.link, context.templateData["left"] == nil {
           leftValue = deserialize(__dictValue, validator: ResolvedValue.leftValidator).orFallback(leftValue)
          }
        }()
        _ = {
         if key == parent?.right?.link, context.templateData["right"] == nil {
           rightValue = deserialize(__dictValue, validator: ResolvedValue.rightValidator).orFallback(rightValue)
          }
        }()
        _ = {
         if key == parent?.start?.link, context.templateData["start"] == nil {
           startValue = deserialize(__dictValue, validator: ResolvedValue.startValidator).orFallback(startValue)
          }
        }()
        _ = {
         if key == parent?.top?.link, context.templateData["top"] == nil {
           topValue = deserialize(__dictValue, validator: ResolvedValue.topValidator).orFallback(topValue)
          }
        }()
        _ = {
         if key == parent?.unit?.link, context.templateData["unit"] == nil {
           unitValue = deserialize(__dictValue).orFallback(unitValue)
          }
        }()
      }
    }()
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
      bottom: { bottomValue.value }(),
      end: { endValue.value }(),
      left: { leftValue.value }(),
      right: { rightValue.value }(),
      start: { startValue.value }(),
      top: { topValue.value }(),
      unit: { unitValue.value }()
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
