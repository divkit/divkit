// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivPhoneInputMaskTemplate: TemplateValue {
  public static let type: String = "phone"
  public let parent: String? // at least 1 char
  public let rawTextVariable: Field<String>? // at least 1 char

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        rawTextVariable: try dictionary.getOptionalField("raw_text_variable")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-phone-input-mask_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    rawTextVariable: Field<String>? = nil
  ) {
    self.parent = parent
    self.rawTextVariable = rawTextVariable
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivPhoneInputMaskTemplate?) -> DeserializationResult<DivPhoneInputMask> {
    let rawTextVariableValue = parent?.rawTextVariable?.resolveValue(context: context, validator: ResolvedValue.rawTextVariableValidator) ?? .noValue
    var errors = mergeErrors(
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
    let result = DivPhoneInputMask(
      rawTextVariable: rawTextVariableNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivPhoneInputMaskTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPhoneInputMask> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var rawTextVariableValue: DeserializationResult<String> = parent?.rawTextVariable?.value(validatedBy: ResolvedValue.rawTextVariableValidator) ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "raw_text_variable":
        rawTextVariableValue = deserialize(__dictValue, validator: ResolvedValue.rawTextVariableValidator).merged(with: rawTextVariableValue)
      case parent?.rawTextVariable?.link:
        rawTextVariableValue = rawTextVariableValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.rawTextVariableValidator))
      default: break
      }
    }
    var errors = mergeErrors(
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
    let result = DivPhoneInputMask(
      rawTextVariable: rawTextVariableNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivPhoneInputMaskTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivPhoneInputMaskTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivPhoneInputMaskTemplate(
      parent: nil,
      rawTextVariable: rawTextVariable ?? mergedParent.rawTextVariable
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivPhoneInputMaskTemplate {
    return try mergedWithParent(templates: templates)
  }
}
