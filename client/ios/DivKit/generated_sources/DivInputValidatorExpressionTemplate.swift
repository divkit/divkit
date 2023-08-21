// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivInputValidatorExpressionTemplate: TemplateValue {
  public static let type: String = "expression"
  public let parent: String? // at least 1 char
  public let allowEmpty: Field<Expression<Bool>>? // default value: false
  public let condition: Field<Expression<Bool>>?
  public let labelId: Field<Expression<String>>? // at least 1 char
  public let variable: Field<String>? // at least 1 char

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        allowEmpty: try dictionary.getOptionalExpressionField("allow_empty"),
        condition: try dictionary.getOptionalExpressionField("condition"),
        labelId: try dictionary.getOptionalExpressionField("label_id"),
        variable: try dictionary.getOptionalField("variable")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-input-validator-expression_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    allowEmpty: Field<Expression<Bool>>? = nil,
    condition: Field<Expression<Bool>>? = nil,
    labelId: Field<Expression<String>>? = nil,
    variable: Field<String>? = nil
  ) {
    self.parent = parent
    self.allowEmpty = allowEmpty
    self.condition = condition
    self.labelId = labelId
    self.variable = variable
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivInputValidatorExpressionTemplate?) -> DeserializationResult<DivInputValidatorExpression> {
    let allowEmptyValue = parent?.allowEmpty?.resolveOptionalValue(context: context, validator: ResolvedValue.allowEmptyValidator) ?? .noValue
    let conditionValue = parent?.condition?.resolveValue(context: context) ?? .noValue
    let labelIdValue = parent?.labelId?.resolveValue(context: context, validator: ResolvedValue.labelIdValidator) ?? .noValue
    let variableValue = parent?.variable?.resolveValue(context: context, validator: ResolvedValue.variableValidator) ?? .noValue
    var errors = mergeErrors(
      allowEmptyValue.errorsOrWarnings?.map { .nestedObjectError(field: "allow_empty", error: $0) },
      conditionValue.errorsOrWarnings?.map { .nestedObjectError(field: "condition", error: $0) },
      labelIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "label_id", error: $0) },
      variableValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable", error: $0) }
    )
    if case .noValue = conditionValue {
      errors.append(.requiredFieldIsMissing(field: "condition"))
    }
    if case .noValue = labelIdValue {
      errors.append(.requiredFieldIsMissing(field: "label_id"))
    }
    if case .noValue = variableValue {
      errors.append(.requiredFieldIsMissing(field: "variable"))
    }
    guard
      let conditionNonNil = conditionValue.value,
      let labelIdNonNil = labelIdValue.value,
      let variableNonNil = variableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivInputValidatorExpression(
      allowEmpty: allowEmptyValue.value,
      condition: conditionNonNil,
      labelId: labelIdNonNil,
      variable: variableNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInputValidatorExpressionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputValidatorExpression> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var allowEmptyValue: DeserializationResult<Expression<Bool>> = parent?.allowEmpty?.value() ?? .noValue
    var conditionValue: DeserializationResult<Expression<Bool>> = parent?.condition?.value() ?? .noValue
    var labelIdValue: DeserializationResult<Expression<String>> = parent?.labelId?.value() ?? .noValue
    var variableValue: DeserializationResult<String> = parent?.variable?.value(validatedBy: ResolvedValue.variableValidator) ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "allow_empty":
        allowEmptyValue = deserialize(__dictValue, validator: ResolvedValue.allowEmptyValidator).merged(with: allowEmptyValue)
      case "condition":
        conditionValue = deserialize(__dictValue).merged(with: conditionValue)
      case "label_id":
        labelIdValue = deserialize(__dictValue, validator: ResolvedValue.labelIdValidator).merged(with: labelIdValue)
      case "variable":
        variableValue = deserialize(__dictValue, validator: ResolvedValue.variableValidator).merged(with: variableValue)
      case parent?.allowEmpty?.link:
        allowEmptyValue = allowEmptyValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.allowEmptyValidator))
      case parent?.condition?.link:
        conditionValue = conditionValue.merged(with: deserialize(__dictValue))
      case parent?.labelId?.link:
        labelIdValue = labelIdValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.labelIdValidator))
      case parent?.variable?.link:
        variableValue = variableValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.variableValidator))
      default: break
      }
    }
    var errors = mergeErrors(
      allowEmptyValue.errorsOrWarnings?.map { .nestedObjectError(field: "allow_empty", error: $0) },
      conditionValue.errorsOrWarnings?.map { .nestedObjectError(field: "condition", error: $0) },
      labelIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "label_id", error: $0) },
      variableValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable", error: $0) }
    )
    if case .noValue = conditionValue {
      errors.append(.requiredFieldIsMissing(field: "condition"))
    }
    if case .noValue = labelIdValue {
      errors.append(.requiredFieldIsMissing(field: "label_id"))
    }
    if case .noValue = variableValue {
      errors.append(.requiredFieldIsMissing(field: "variable"))
    }
    guard
      let conditionNonNil = conditionValue.value,
      let labelIdNonNil = labelIdValue.value,
      let variableNonNil = variableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivInputValidatorExpression(
      allowEmpty: allowEmptyValue.value,
      condition: conditionNonNil,
      labelId: labelIdNonNil,
      variable: variableNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivInputValidatorExpressionTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivInputValidatorExpressionTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivInputValidatorExpressionTemplate(
      parent: nil,
      allowEmpty: allowEmpty ?? mergedParent.allowEmpty,
      condition: condition ?? mergedParent.condition,
      labelId: labelId ?? mergedParent.labelId,
      variable: variable ?? mergedParent.variable
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivInputValidatorExpressionTemplate {
    return try mergedWithParent(templates: templates)
  }
}
