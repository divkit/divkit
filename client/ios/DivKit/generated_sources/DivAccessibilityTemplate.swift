// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivAccessibilityTemplate: TemplateValue, TemplateDeserializable {
  public typealias Kind = DivAccessibility.Kind

  public typealias Mode = DivAccessibility.Mode

  public let description: Field<Expression<String>>? // at least 1 char
  public let hint: Field<Expression<String>>? // at least 1 char
  public let mode: Field<Expression<Mode>>? // default value: default
  public let muteAfterAction: Field<Expression<Bool>>? // default value: false
  public let stateDescription: Field<Expression<String>>? // at least 1 char
  public let type: Field<Kind>?

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    self.init(
      description: try dictionary.getOptionalField("description"),
      hint: try dictionary.getOptionalField("hint"),
      mode: try dictionary.getOptionalField("mode"),
      muteAfterAction: try dictionary.getOptionalField("mute_after_action"),
      stateDescription: try dictionary.getOptionalField("state_description"),
      type: try dictionary.getOptionalField("type")
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

  private static func resolveOnlyLinks(context: Context, parent: DivAccessibilityTemplate?) -> DeserializationResult<DivAccessibility> {
    let descriptionValue = parent?.description?.resolveOptionalValue(context: context, validator: ResolvedValue.descriptionValidator) ?? .noValue
    let hintValue = parent?.hint?.resolveOptionalValue(context: context, validator: ResolvedValue.hintValidator) ?? .noValue
    let modeValue = parent?.mode?.resolveOptionalValue(context: context, validator: ResolvedValue.modeValidator) ?? .noValue
    let muteAfterActionValue = parent?.muteAfterAction?.resolveOptionalValue(context: context, validator: ResolvedValue.muteAfterActionValidator) ?? .noValue
    let stateDescriptionValue = parent?.stateDescription?.resolveOptionalValue(context: context, validator: ResolvedValue.stateDescriptionValidator) ?? .noValue
    let typeValue = parent?.type?.resolveOptionalValue(context: context, validator: ResolvedValue.typeValidator) ?? .noValue
    let errors = mergeErrors(
      descriptionValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "description", level: .warning)) },
      hintValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "hint", level: .warning)) },
      modeValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "mode", level: .warning)) },
      muteAfterActionValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "mute_after_action", level: .warning)) },
      stateDescriptionValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "state_description", level: .warning)) },
      typeValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "type", level: .warning)) }
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

  public static func resolveValue(context: Context, parent: DivAccessibilityTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAccessibility> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var descriptionValue: DeserializationResult<Expression<String>> = parent?.description?.value() ?? .noValue
    var hintValue: DeserializationResult<Expression<String>> = parent?.hint?.value() ?? .noValue
    var modeValue: DeserializationResult<Expression<DivAccessibility.Mode>> = parent?.mode?.value() ?? .noValue
    var muteAfterActionValue: DeserializationResult<Expression<Bool>> = parent?.muteAfterAction?.value() ?? .noValue
    var stateDescriptionValue: DeserializationResult<Expression<String>> = parent?.stateDescription?.value() ?? .noValue
    var typeValue: DeserializationResult<DivAccessibility.Kind> = parent?.type?.value(validatedBy: ResolvedValue.typeValidator) ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "description":
        descriptionValue = deserialize(__dictValue, validator: ResolvedValue.descriptionValidator).merged(with: descriptionValue)
      case "hint":
        hintValue = deserialize(__dictValue, validator: ResolvedValue.hintValidator).merged(with: hintValue)
      case "mode":
        modeValue = deserialize(__dictValue, validator: ResolvedValue.modeValidator).merged(with: modeValue)
      case "mute_after_action":
        muteAfterActionValue = deserialize(__dictValue, validator: ResolvedValue.muteAfterActionValidator).merged(with: muteAfterActionValue)
      case "state_description":
        stateDescriptionValue = deserialize(__dictValue, validator: ResolvedValue.stateDescriptionValidator).merged(with: stateDescriptionValue)
      case "type":
        typeValue = deserialize(__dictValue, validator: ResolvedValue.typeValidator).merged(with: typeValue)
      case parent?.description?.link:
        descriptionValue = descriptionValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.descriptionValidator))
      case parent?.hint?.link:
        hintValue = hintValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.hintValidator))
      case parent?.mode?.link:
        modeValue = modeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.modeValidator))
      case parent?.muteAfterAction?.link:
        muteAfterActionValue = muteAfterActionValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.muteAfterActionValidator))
      case parent?.stateDescription?.link:
        stateDescriptionValue = stateDescriptionValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.stateDescriptionValidator))
      case parent?.type?.link:
        typeValue = typeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.typeValidator))
      default: break
      }
    }
    let errors = mergeErrors(
      descriptionValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "description", level: .warning)) },
      hintValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "hint", level: .warning)) },
      modeValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "mode", level: .warning)) },
      muteAfterActionValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "mute_after_action", level: .warning)) },
      stateDescriptionValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "state_description", level: .warning)) },
      typeValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "type", level: .warning)) }
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

  private func mergedWithParent(templates: Templates) throws -> DivAccessibilityTemplate {
    return self
  }

  public func resolveParent(templates: Templates) throws -> DivAccessibilityTemplate {
    return try mergedWithParent(templates: templates)
  }
}
