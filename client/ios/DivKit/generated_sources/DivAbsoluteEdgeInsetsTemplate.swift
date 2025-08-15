// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivAbsoluteEdgeInsetsTemplate: TemplateValue, Sendable {
  public let bottom: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let left: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let right: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let top: Field<Expression<Int>>? // constraint: number >= 0; default value: 0

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      bottom: dictionary.getOptionalExpressionField("bottom"),
      left: dictionary.getOptionalExpressionField("left"),
      right: dictionary.getOptionalExpressionField("right"),
      top: dictionary.getOptionalExpressionField("top")
    )
  }

  init(
    bottom: Field<Expression<Int>>? = nil,
    left: Field<Expression<Int>>? = nil,
    right: Field<Expression<Int>>? = nil,
    top: Field<Expression<Int>>? = nil
  ) {
    self.bottom = bottom
    self.left = left
    self.right = right
    self.top = top
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivAbsoluteEdgeInsetsTemplate?) -> DeserializationResult<DivAbsoluteEdgeInsets> {
    let bottomValue = { parent?.bottom?.resolveOptionalValue(context: context, validator: ResolvedValue.bottomValidator) ?? .noValue }()
    let leftValue = { parent?.left?.resolveOptionalValue(context: context, validator: ResolvedValue.leftValidator) ?? .noValue }()
    let rightValue = { parent?.right?.resolveOptionalValue(context: context, validator: ResolvedValue.rightValidator) ?? .noValue }()
    let topValue = { parent?.top?.resolveOptionalValue(context: context, validator: ResolvedValue.topValidator) ?? .noValue }()
    let errors = mergeErrors(
      bottomValue.errorsOrWarnings?.map { .nestedObjectError(field: "bottom", error: $0) },
      leftValue.errorsOrWarnings?.map { .nestedObjectError(field: "left", error: $0) },
      rightValue.errorsOrWarnings?.map { .nestedObjectError(field: "right", error: $0) },
      topValue.errorsOrWarnings?.map { .nestedObjectError(field: "top", error: $0) }
    )
    let result = DivAbsoluteEdgeInsets(
      bottom: { bottomValue.value }(),
      left: { leftValue.value }(),
      right: { rightValue.value }(),
      top: { topValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivAbsoluteEdgeInsetsTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAbsoluteEdgeInsets> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var bottomValue: DeserializationResult<Expression<Int>> = { parent?.bottom?.value() ?? .noValue }()
    var leftValue: DeserializationResult<Expression<Int>> = { parent?.left?.value() ?? .noValue }()
    var rightValue: DeserializationResult<Expression<Int>> = { parent?.right?.value() ?? .noValue }()
    var topValue: DeserializationResult<Expression<Int>> = { parent?.top?.value() ?? .noValue }()
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
          if key == "top" {
           topValue = deserialize(__dictValue, validator: ResolvedValue.topValidator).merged(with: topValue)
          }
        }()
        _ = {
         if key == parent?.bottom?.link {
           bottomValue = bottomValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.bottomValidator) })
          }
        }()
        _ = {
         if key == parent?.left?.link {
           leftValue = leftValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.leftValidator) })
          }
        }()
        _ = {
         if key == parent?.right?.link {
           rightValue = rightValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.rightValidator) })
          }
        }()
        _ = {
         if key == parent?.top?.link {
           topValue = topValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.topValidator) })
          }
        }()
      }
    }()
    let errors = mergeErrors(
      bottomValue.errorsOrWarnings?.map { .nestedObjectError(field: "bottom", error: $0) },
      leftValue.errorsOrWarnings?.map { .nestedObjectError(field: "left", error: $0) },
      rightValue.errorsOrWarnings?.map { .nestedObjectError(field: "right", error: $0) },
      topValue.errorsOrWarnings?.map { .nestedObjectError(field: "top", error: $0) }
    )
    let result = DivAbsoluteEdgeInsets(
      bottom: { bottomValue.value }(),
      left: { leftValue.value }(),
      right: { rightValue.value }(),
      top: { topValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivAbsoluteEdgeInsetsTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivAbsoluteEdgeInsetsTemplate {
    return try mergedWithParent(templates: templates)
  }
}
