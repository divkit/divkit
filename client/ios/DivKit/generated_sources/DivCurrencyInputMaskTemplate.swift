// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivCurrencyInputMaskTemplate: TemplateValue {
  public static let type: String = "currency"
  public let parent: String? // at least 1 char
  public let locale: Field<Expression<String>>? // at least 1 char

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      locale: try dictionary.getOptionalExpressionField("locale")
    )
  }

  init(
    parent: String?,
    locale: Field<Expression<String>>? = nil
  ) {
    self.parent = parent
    self.locale = locale
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivCurrencyInputMaskTemplate?) -> DeserializationResult<DivCurrencyInputMask> {
    let localeValue = parent?.locale?.resolveOptionalValue(context: context, validator: ResolvedValue.localeValidator) ?? .noValue
    let errors = mergeErrors(
      localeValue.errorsOrWarnings?.map { .nestedObjectError(field: "locale", error: $0) }
    )
    let result = DivCurrencyInputMask(
      locale: localeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivCurrencyInputMaskTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCurrencyInputMask> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var localeValue: DeserializationResult<Expression<String>> = parent?.locale?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "locale":
        localeValue = deserialize(__dictValue, validator: ResolvedValue.localeValidator).merged(with: localeValue)
      case parent?.locale?.link:
        localeValue = localeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.localeValidator))
      default: break
      }
    }
    let errors = mergeErrors(
      localeValue.errorsOrWarnings?.map { .nestedObjectError(field: "locale", error: $0) }
    )
    let result = DivCurrencyInputMask(
      locale: localeValue.value
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
      locale: locale ?? mergedParent.locale
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivCurrencyInputMaskTemplate {
    return try mergedWithParent(templates: templates)
  }
}
