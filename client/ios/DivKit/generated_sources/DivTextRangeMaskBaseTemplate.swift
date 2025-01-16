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
    let isEnabledValue = { parent?.isEnabled?.resolveOptionalValue(context: context) ?? .noValue }()
    let errors = mergeErrors(
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) }
    )
    let result = DivTextRangeMaskBase(
      isEnabled: { isEnabledValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTextRangeMaskBaseTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTextRangeMaskBase> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var isEnabledValue: DeserializationResult<Expression<Bool>> = { parent?.isEnabled?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "is_enabled" {
           isEnabledValue = deserialize(__dictValue).merged(with: isEnabledValue)
          }
        }()
        _ = {
         if key == parent?.isEnabled?.link {
           isEnabledValue = isEnabledValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    let errors = mergeErrors(
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) }
    )
    let result = DivTextRangeMaskBase(
      isEnabled: { isEnabledValue.value }()
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
