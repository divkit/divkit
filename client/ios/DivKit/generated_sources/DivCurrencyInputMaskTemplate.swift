// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivCurrencyInputMaskTemplate: TemplateValue, Sendable {
  public static let type: String = "currency"
  public let parent: String?
  public let locale: Field<Expression<String>>?
  public let rawTextVariable: Field<String>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      locale: dictionary.getOptionalExpressionField("locale"),
      rawTextVariable: dictionary.getOptionalField("raw_text_variable")
    )
  }

  init(
    parent: String?,
    locale: Field<Expression<String>>? = nil,
    rawTextVariable: Field<String>? = nil
  ) {
    self.parent = parent
    self.locale = locale
    self.rawTextVariable = rawTextVariable
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivCurrencyInputMaskTemplate?) -> DeserializationResult<DivCurrencyInputMask> {
    let localeValue = { parent?.locale?.resolveOptionalValue(context: context) ?? .noValue }()
    let rawTextVariableValue = { parent?.rawTextVariable?.resolveValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      localeValue.errorsOrWarnings?.map { .nestedObjectError(field: "locale", error: $0) },
      rawTextVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "raw_text_variable", error: $0) }
    )
    if case .noValue = rawTextVariableValue {
      errors.append(.requiredFieldIsMissing(field: "raw_text_variable"))
    }
    guard
      let rawTextVariableNonNil = rawTextVariableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivCurrencyInputMask(
      locale: { localeValue.value }(),
      rawTextVariable: { rawTextVariableNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivCurrencyInputMaskTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCurrencyInputMask> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var localeValue: DeserializationResult<Expression<String>> = { parent?.locale?.value() ?? .noValue }()
    var rawTextVariableValue: DeserializationResult<String> = { parent?.rawTextVariable?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "locale" {
           localeValue = deserialize(__dictValue).merged(with: localeValue)
          }
        }()
        _ = {
          if key == "raw_text_variable" {
           rawTextVariableValue = deserialize(__dictValue).merged(with: rawTextVariableValue)
          }
        }()
        _ = {
         if key == parent?.locale?.link {
           localeValue = localeValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.rawTextVariable?.link {
           rawTextVariableValue = rawTextVariableValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    var errors = mergeErrors(
      localeValue.errorsOrWarnings?.map { .nestedObjectError(field: "locale", error: $0) },
      rawTextVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "raw_text_variable", error: $0) }
    )
    if case .noValue = rawTextVariableValue {
      errors.append(.requiredFieldIsMissing(field: "raw_text_variable"))
    }
    guard
      let rawTextVariableNonNil = rawTextVariableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivCurrencyInputMask(
      locale: { localeValue.value }(),
      rawTextVariable: { rawTextVariableNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivCurrencyInputMaskTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivCurrencyInputMaskTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivCurrencyInputMaskTemplate(
      parent: nil,
      locale: locale ?? mergedParent.locale,
      rawTextVariable: rawTextVariable ?? mergedParent.rawTextVariable
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivCurrencyInputMaskTemplate {
    return try mergedWithParent(templates: templates)
  }
}
