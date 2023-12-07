// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivInputValidatorBaseTemplate: TemplateValue {
  public let allowEmpty: Field<Expression<Bool>>? // default value: false
  public let labelId: Field<Expression<String>>?
  public let variable: Field<String>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      allowEmpty: try dictionary.getOptionalExpressionField("allow_empty"),
      labelId: try dictionary.getOptionalExpressionField("label_id"),
      variable: try dictionary.getOptionalField("variable")
    )
  }

  init(
    allowEmpty: Field<Expression<Bool>>? = nil,
    labelId: Field<Expression<String>>? = nil,
    variable: Field<String>? = nil
  ) {
    self.allowEmpty = allowEmpty
    self.labelId = labelId
    self.variable = variable
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivInputValidatorBaseTemplate?) -> DeserializationResult<DivInputValidatorBase> {
    let allowEmptyValue = parent?.allowEmpty?.resolveOptionalValue(context: context, validator: ResolvedValue.allowEmptyValidator) ?? .noValue
    let labelIdValue = parent?.labelId?.resolveOptionalValue(context: context, validator: ResolvedValue.labelIdValidator) ?? .noValue
    let variableValue = parent?.variable?.resolveOptionalValue(context: context, validator: ResolvedValue.variableValidator) ?? .noValue
    let errors = mergeErrors(
      allowEmptyValue.errorsOrWarnings?.map { .nestedObjectError(field: "allow_empty", error: $0) },
      labelIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "label_id", error: $0) },
      variableValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable", error: $0) }
    )
    let result = DivInputValidatorBase(
      allowEmpty: allowEmptyValue.value,
      labelId: labelIdValue.value,
      variable: variableValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInputValidatorBaseTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputValidatorBase> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var allowEmptyValue: DeserializationResult<Expression<Bool>> = parent?.allowEmpty?.value() ?? .noValue
    var labelIdValue: DeserializationResult<Expression<String>> = parent?.labelId?.value() ?? .noValue
    var variableValue: DeserializationResult<String> = parent?.variable?.value(validatedBy: ResolvedValue.variableValidator) ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "allow_empty":
        allowEmptyValue = deserialize(__dictValue, validator: ResolvedValue.allowEmptyValidator).merged(with: allowEmptyValue)
      case "label_id":
        labelIdValue = deserialize(__dictValue, validator: ResolvedValue.labelIdValidator).merged(with: labelIdValue)
      case "variable":
        variableValue = deserialize(__dictValue, validator: ResolvedValue.variableValidator).merged(with: variableValue)
      case parent?.allowEmpty?.link:
        allowEmptyValue = allowEmptyValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.allowEmptyValidator))
      case parent?.labelId?.link:
        labelIdValue = labelIdValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.labelIdValidator))
      case parent?.variable?.link:
        variableValue = variableValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.variableValidator))
      default: break
      }
    }
    let errors = mergeErrors(
      allowEmptyValue.errorsOrWarnings?.map { .nestedObjectError(field: "allow_empty", error: $0) },
      labelIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "label_id", error: $0) },
      variableValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable", error: $0) }
    )
    let result = DivInputValidatorBase(
      allowEmpty: allowEmptyValue.value,
      labelId: labelIdValue.value,
      variable: variableValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivInputValidatorBaseTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivInputValidatorBaseTemplate {
    return try mergedWithParent(templates: templates)
  }
}
