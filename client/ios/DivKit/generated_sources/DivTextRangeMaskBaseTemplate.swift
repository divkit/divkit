// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTextRangeMaskBaseTemplate: TemplateValue, Sendable {
  public let isEnabled: Field<Expression<Bool>>? // default value: true

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      isEnabled: dictionary.getOptionalExpressionField("is_enabled")
    )
  }

  init(
    isEnabled: Field<Expression<Bool>>? = nil
  ) {
    self.isEnabled = isEnabled
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTextRangeMaskBaseTemplate?) -> DeserializationResult<DivTextRangeMaskBase> {
    let isEnabledValue = parent?.isEnabled?.resolveOptionalValue(context: context) ?? .noValue
    let errors = mergeErrors(
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) }
    )
    let result = DivTextRangeMaskBase(
      isEnabled: isEnabledValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTextRangeMaskBaseTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTextRangeMaskBase> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var isEnabledValue: DeserializationResult<Expression<Bool>> = parent?.isEnabled?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "is_enabled":
        isEnabledValue = deserialize(__dictValue).merged(with: isEnabledValue)
      case parent?.isEnabled?.link:
        isEnabledValue = isEnabledValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    let errors = mergeErrors(
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) }
    )
    let result = DivTextRangeMaskBase(
      isEnabled: isEnabledValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivTextRangeMaskBaseTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTextRangeMaskBaseTemplate {
    return try mergedWithParent(templates: templates)
  }
}
