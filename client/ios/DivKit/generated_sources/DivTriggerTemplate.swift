// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivTriggerTemplate: TemplateValue, TemplateDeserializable {
  public typealias Mode = DivTrigger.Mode

  public let actions: Field<[DivActionTemplate]>? // at least 1 elements
  public let condition: Field<Expression<Bool>>?
  public let mode: Field<Expression<Mode>>? // default value: on_condition

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        actions: try dictionary.getOptionalArray("actions", templateToType: templateToType),
        condition: try dictionary.getOptionalField("condition"),
        mode: try dictionary.getOptionalField("mode")
      )
    } catch let DeserializationError.invalidFieldRepresentation(
      field: field,
      representation: representation
    ) {
      throw DeserializationError.invalidFieldRepresentation(
        field: "div-trigger_template." + field,
        representation: representation
      )
    }
  }

  init(
    actions: Field<[DivActionTemplate]>? = nil,
    condition: Field<Expression<Bool>>? = nil,
    mode: Field<Expression<Mode>>? = nil
  ) {
    self.actions = actions
    self.condition = condition
    self.mode = mode
  }

  private static func resolveOnlyLinks(
    context: Context,
    parent: DivTriggerTemplate?
  ) -> DeserializationResult<DivTrigger> {
    let actionsValue = parent?.actions?.resolveValue(
      context: context,
      validator: ResolvedValue.actionsValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let conditionValue = parent?.condition?.resolveValue(context: context) ?? .noValue
    let modeValue = parent?.mode?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.modeValidator
    ) ?? .noValue
    var errors = mergeErrors(
      actionsValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "actions", level: .error)) },
      conditionValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "condition", level: .error)) },
      modeValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "mode", level: .warning)) }
    )
    if case .noValue = actionsValue {
      errors
        .append(.right(FieldError(
          fieldName: "actions",
          level: .error,
          error: .requiredFieldIsMissing
        )))
    }
    if case .noValue = conditionValue {
      errors
        .append(.right(FieldError(
          fieldName: "condition",
          level: .error,
          error: .requiredFieldIsMissing
        )))
    }
    guard
      let actionsNonNil = actionsValue.value,
      let conditionNonNil = conditionValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivTrigger(
      actions: actionsNonNil,
      condition: conditionNonNil,
      mode: modeValue.value
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(
    context: Context,
    parent: DivTriggerTemplate?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<DivTrigger> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var actionsValue: DeserializationResult<[DivAction]> = .noValue
    var conditionValue: DeserializationResult<Expression<Bool>> = parent?.condition?
      .value() ?? .noValue
    var modeValue: DeserializationResult<Expression<DivTrigger.Mode>> = parent?.mode?
      .value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "actions":
        actionsValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.actionsValidator,
          type: DivActionTemplate.self
        ).merged(with: actionsValue)
      case "condition":
        conditionValue = deserialize(__dictValue).merged(with: conditionValue)
      case "mode":
        modeValue = deserialize(__dictValue, validator: ResolvedValue.modeValidator)
          .merged(with: modeValue)
      case parent?.actions?.link:
        actionsValue = actionsValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.actionsValidator,
          type: DivActionTemplate.self
        ))
      case parent?.condition?.link:
        conditionValue = conditionValue.merged(with: deserialize(__dictValue))
      case parent?.mode?.link:
        modeValue = modeValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.modeValidator))
      default: break
      }
    }
    if let parent = parent {
      actionsValue = actionsValue.merged(with: parent.actions?.resolveValue(
        context: context,
        validator: ResolvedValue.actionsValidator,
        useOnlyLinks: true
      ))
    }
    var errors = mergeErrors(
      actionsValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "actions", level: .error)) },
      conditionValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "condition", level: .error)) },
      modeValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "mode", level: .warning)) }
    )
    if case .noValue = actionsValue {
      errors
        .append(.right(FieldError(
          fieldName: "actions",
          level: .error,
          error: .requiredFieldIsMissing
        )))
    }
    if case .noValue = conditionValue {
      errors
        .append(.right(FieldError(
          fieldName: "condition",
          level: .error,
          error: .requiredFieldIsMissing
        )))
    }
    guard
      let actionsNonNil = actionsValue.value,
      let conditionNonNil = conditionValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivTrigger(
      actions: actionsNonNil,
      condition: conditionNonNil,
      mode: modeValue.value
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates _: Templates) throws -> DivTriggerTemplate {
    self
  }

  public func resolveParent(templates: Templates) throws -> DivTriggerTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivTriggerTemplate(
      actions: try merged.actions?.resolveParent(templates: templates),
      condition: merged.condition,
      mode: merged.mode
    )
  }
}
