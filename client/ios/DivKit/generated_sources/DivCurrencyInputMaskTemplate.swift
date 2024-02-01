// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivCurrencyInputMaskTemplate: TemplateValue {
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
    let localeValue = parent?.locale?.resolveOptionalValue(context: context) ?? .noValue
    let rawTextVariableValue = parent?.rawTextVariable?.resolveValue(context: context) ?? .noValue
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
      locale: localeValue.value,
      rawTextVariable: rawTextVariableNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivCurrencyInputMaskTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCurrencyInputMask> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var localeValue: DeserializationResult<Expression<String>> = parent?.locale?.value() ?? .noValue
    var rawTextVariableValue: DeserializationResult<String> = parent?.rawTextVariable?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "locale":
        localeValue = deserialize(__dictValue).merged(with: localeValue)
      case "raw_text_variable":
        rawTextVariableValue = deserialize(__dictValue).merged(with: rawTextVariableValue)
      case parent?.locale?.link:
        localeValue = localeValue.merged(with: { deserialize(__dictValue) })
      case parent?.rawTextVariable?.link:
        rawTextVariableValue = rawTextVariableValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
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
      locale: localeValue.value,
      rawTextVariable: rawTextVariableNonNil
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
