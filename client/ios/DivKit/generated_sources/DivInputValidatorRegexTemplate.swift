// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivInputValidatorRegexTemplate: TemplateValue {
  public static let type: String = "regex"
  public let parent: String? // at least 1 char
  public let allowEmpty: Field<Expression<Bool>>? // default value: false
  public let labelId: Field<Expression<String>>? // at least 1 char
  public let pattern: Field<Expression<String>>? // at least 1 char
  public let variable: Field<String>? // at least 1 char

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        allowEmpty: try dictionary.getOptionalExpressionField("allow_empty"),
        labelId: try dictionary.getOptionalExpressionField("label_id"),
        pattern: try dictionary.getOptionalExpressionField("pattern"),
        variable: try dictionary.getOptionalField("variable")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-input-validator-regex_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    allowEmpty: Field<Expression<Bool>>? = nil,
    labelId: Field<Expression<String>>? = nil,
    pattern: Field<Expression<String>>? = nil,
    variable: Field<String>? = nil
  ) {
    self.parent = parent
    self.allowEmpty = allowEmpty
    self.labelId = labelId
    self.pattern = pattern
    self.variable = variable
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivInputValidatorRegexTemplate?) -> DeserializationResult<DivInputValidatorRegex> {
    let allowEmptyValue = parent?.allowEmpty?.resolveOptionalValue(context: context, validator: ResolvedValue.allowEmptyValidator) ?? .noValue
    let labelIdValue = parent?.labelId?.resolveValue(context: context, validator: ResolvedValue.labelIdValidator) ?? .noValue
    let patternValue = parent?.pattern?.resolveValue(context: context, validator: ResolvedValue.patternValidator) ?? .noValue
    let variableValue = parent?.variable?.resolveValue(context: context, validator: ResolvedValue.variableValidator) ?? .noValue
    var errors = mergeErrors(
      allowEmptyValue.errorsOrWarnings?.map { .nestedObjectError(field: "allow_empty", error: $0) },
      labelIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "label_id", error: $0) },
      patternValue.errorsOrWarnings?.map { .nestedObjectError(field: "pattern", error: $0) },
      variableValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable", error: $0) }
    )
    if case .noValue = labelIdValue {
      errors.append(.requiredFieldIsMissing(field: "label_id"))
    }
    if case .noValue = patternValue {
      errors.append(.requiredFieldIsMissing(field: "pattern"))
    }
    if case .noValue = variableValue {
      errors.append(.requiredFieldIsMissing(field: "variable"))
    }
    guard
      let labelIdNonNil = labelIdValue.value,
      let patternNonNil = patternValue.value,
      let variableNonNil = variableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivInputValidatorRegex(
      allowEmpty: allowEmptyValue.value,
      labelId: labelIdNonNil,
      pattern: patternNonNil,
      variable: variableNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInputValidatorRegexTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputValidatorRegex> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var allowEmptyValue: DeserializationResult<Expression<Bool>> = parent?.allowEmpty?.value() ?? .noValue
    var labelIdValue: DeserializationResult<Expression<String>> = parent?.labelId?.value() ?? .noValue
    var patternValue: DeserializationResult<Expression<String>> = parent?.pattern?.value() ?? .noValue
    var variableValue: DeserializationResult<String> = parent?.variable?.value(validatedBy: ResolvedValue.variableValidator) ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "allow_empty":
        allowEmptyValue = deserialize(__dictValue, validator: ResolvedValue.allowEmptyValidator).merged(with: allowEmptyValue)
      case "label_id":
        labelIdValue = deserialize(__dictValue, validator: ResolvedValue.labelIdValidator).merged(with: labelIdValue)
      case "pattern":
        patternValue = deserialize(__dictValue, validator: ResolvedValue.patternValidator).merged(with: patternValue)
      case "variable":
        variableValue = deserialize(__dictValue, validator: ResolvedValue.variableValidator).merged(with: variableValue)
      case parent?.allowEmpty?.link:
        allowEmptyValue = allowEmptyValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.allowEmptyValidator))
      case parent?.labelId?.link:
        labelIdValue = labelIdValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.labelIdValidator))
      case parent?.pattern?.link:
        patternValue = patternValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.patternValidator))
      case parent?.variable?.link:
        variableValue = variableValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.variableValidator))
      default: break
      }
    }
    var errors = mergeErrors(
      allowEmptyValue.errorsOrWarnings?.map { .nestedObjectError(field: "allow_empty", error: $0) },
      labelIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "label_id", error: $0) },
      patternValue.errorsOrWarnings?.map { .nestedObjectError(field: "pattern", error: $0) },
      variableValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable", error: $0) }
    )
    if case .noValue = labelIdValue {
      errors.append(.requiredFieldIsMissing(field: "label_id"))
    }
    if case .noValue = patternValue {
      errors.append(.requiredFieldIsMissing(field: "pattern"))
    }
    if case .noValue = variableValue {
      errors.append(.requiredFieldIsMissing(field: "variable"))
    }
    guard
      let labelIdNonNil = labelIdValue.value,
      let patternNonNil = patternValue.value,
      let variableNonNil = variableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivInputValidatorRegex(
      allowEmpty: allowEmptyValue.value,
      labelId: labelIdNonNil,
      pattern: patternNonNil,
      variable: variableNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivInputValidatorRegexTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivInputValidatorRegexTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivInputValidatorRegexTemplate(
      parent: nil,
      allowEmpty: allowEmpty ?? mergedParent.allowEmpty,
      labelId: labelId ?? mergedParent.labelId,
      pattern: pattern ?? mergedParent.pattern,
      variable: variable ?? mergedParent.variable
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivInputValidatorRegexTemplate {
    return try mergedWithParent(templates: templates)
  }
}
