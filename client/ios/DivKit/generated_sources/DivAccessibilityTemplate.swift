// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivAccessibilityTemplate: TemplateValue {
  public typealias Kind = DivAccessibility.Kind

  public typealias Mode = DivAccessibility.Mode

  public let description: Field<Expression<String>>?
  public let hint: Field<Expression<String>>?
  public let mode: Field<Expression<Mode>>? // default value: default
  public let muteAfterAction: Field<Expression<Bool>>? // default value: false
  public let stateDescription: Field<Expression<String>>?
  public let type: Field<Kind>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      description: dictionary.getOptionalExpressionField("description"),
      hint: dictionary.getOptionalExpressionField("hint"),
      mode: dictionary.getOptionalExpressionField("mode"),
      muteAfterAction: dictionary.getOptionalExpressionField("mute_after_action"),
      stateDescription: dictionary.getOptionalExpressionField("state_description"),
      type: dictionary.getOptionalField("type")
    )
  }

  init(
    description: Field<Expression<String>>? = nil,
    hint: Field<Expression<String>>? = nil,
    mode: Field<Expression<Mode>>? = nil,
    muteAfterAction: Field<Expression<Bool>>? = nil,
    stateDescription: Field<Expression<String>>? = nil,
    type: Field<Kind>? = nil
  ) {
    self.description = description
    self.hint = hint
    self.mode = mode
    self.muteAfterAction = muteAfterAction
    self.stateDescription = stateDescription
    self.type = type
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivAccessibilityTemplate?) -> DeserializationResult<DivAccessibility> {
    let descriptionValue = parent?.description?.resolveOptionalValue(context: context) ?? .noValue
    let hintValue = parent?.hint?.resolveOptionalValue(context: context) ?? .noValue
    let modeValue = parent?.mode?.resolveOptionalValue(context: context) ?? .noValue
    let muteAfterActionValue = parent?.muteAfterAction?.resolveOptionalValue(context: context) ?? .noValue
    let stateDescriptionValue = parent?.stateDescription?.resolveOptionalValue(context: context) ?? .noValue
    let typeValue = parent?.type?.resolveOptionalValue(context: context) ?? .noValue
    let errors = mergeErrors(
      descriptionValue.errorsOrWarnings?.map { .nestedObjectError(field: "description", error: $0) },
      hintValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint", error: $0) },
      modeValue.errorsOrWarnings?.map { .nestedObjectError(field: "mode", error: $0) },
      muteAfterActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "mute_after_action", error: $0) },
      stateDescriptionValue.errorsOrWarnings?.map { .nestedObjectError(field: "state_description", error: $0) },
      typeValue.errorsOrWarnings?.map { .nestedObjectError(field: "type", error: $0) }
    )
    let result = DivAccessibility(
      description: descriptionValue.value,
      hint: hintValue.value,
      mode: modeValue.value,
      muteAfterAction: muteAfterActionValue.value,
      stateDescription: stateDescriptionValue.value,
      type: typeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivAccessibilityTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAccessibility> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var descriptionValue: DeserializationResult<Expression<String>> = parent?.description?.value() ?? .noValue
    var hintValue: DeserializationResult<Expression<String>> = parent?.hint?.value() ?? .noValue
    var modeValue: DeserializationResult<Expression<DivAccessibility.Mode>> = parent?.mode?.value() ?? .noValue
    var muteAfterActionValue: DeserializationResult<Expression<Bool>> = parent?.muteAfterAction?.value() ?? .noValue
    var stateDescriptionValue: DeserializationResult<Expression<String>> = parent?.stateDescription?.value() ?? .noValue
    var typeValue: DeserializationResult<DivAccessibility.Kind> = parent?.type?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "description":
        descriptionValue = deserialize(__dictValue).merged(with: descriptionValue)
      case "hint":
        hintValue = deserialize(__dictValue).merged(with: hintValue)
      case "mode":
        modeValue = deserialize(__dictValue).merged(with: modeValue)
      case "mute_after_action":
        muteAfterActionValue = deserialize(__dictValue).merged(with: muteAfterActionValue)
      case "state_description":
        stateDescriptionValue = deserialize(__dictValue).merged(with: stateDescriptionValue)
      case "type":
        typeValue = deserialize(__dictValue).merged(with: typeValue)
      case parent?.description?.link:
        descriptionValue = descriptionValue.merged(with: { deserialize(__dictValue) })
      case parent?.hint?.link:
        hintValue = hintValue.merged(with: { deserialize(__dictValue) })
      case parent?.mode?.link:
        modeValue = modeValue.merged(with: { deserialize(__dictValue) })
      case parent?.muteAfterAction?.link:
        muteAfterActionValue = muteAfterActionValue.merged(with: { deserialize(__dictValue) })
      case parent?.stateDescription?.link:
        stateDescriptionValue = stateDescriptionValue.merged(with: { deserialize(__dictValue) })
      case parent?.type?.link:
        typeValue = typeValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    let errors = mergeErrors(
      descriptionValue.errorsOrWarnings?.map { .nestedObjectError(field: "description", error: $0) },
      hintValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint", error: $0) },
      modeValue.errorsOrWarnings?.map { .nestedObjectError(field: "mode", error: $0) },
      muteAfterActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "mute_after_action", error: $0) },
      stateDescriptionValue.errorsOrWarnings?.map { .nestedObjectError(field: "state_description", error: $0) },
      typeValue.errorsOrWarnings?.map { .nestedObjectError(field: "type", error: $0) }
    )
    let result = DivAccessibility(
      description: descriptionValue.value,
      hint: hintValue.value,
      mode: modeValue.value,
      muteAfterAction: muteAfterActionValue.value,
      stateDescription: stateDescriptionValue.value,
      type: typeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivAccessibilityTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivAccessibilityTemplate {
    return try mergedWithParent(templates: templates)
  }
}
