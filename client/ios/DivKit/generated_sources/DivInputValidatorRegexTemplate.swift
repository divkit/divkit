// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInputValidatorRegexTemplate: TemplateValue, Sendable {
  public static let type: String = "regex"
  public let parent: String?
  public let allowEmpty: Field<Expression<Bool>>? // default value: false
  public let labelId: Field<Expression<String>>?
  public let pattern: Field<Expression<String>>?
  public let variable: Field<String>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      allowEmpty: dictionary.getOptionalExpressionField("allow_empty"),
      labelId: dictionary.getOptionalExpressionField("label_id"),
      pattern: dictionary.getOptionalExpressionField("pattern"),
      variable: dictionary.getOptionalField("variable")
    )
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
    let allowEmptyValue = { parent?.allowEmpty?.resolveOptionalValue(context: context) ?? .noValue }()
    let labelIdValue = { parent?.labelId?.resolveValue(context: context) ?? .noValue }()
    let patternValue = { parent?.pattern?.resolveValue(context: context) ?? .noValue }()
    let variableValue = { parent?.variable?.resolveValue(context: context) ?? .noValue }()
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
      allowEmpty: { allowEmptyValue.value }(),
      labelId: { labelIdNonNil }(),
      pattern: { patternNonNil }(),
      variable: { variableNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInputValidatorRegexTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputValidatorRegex> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var allowEmptyValue: DeserializationResult<Expression<Bool>> = { parent?.allowEmpty?.value() ?? .noValue }()
    var labelIdValue: DeserializationResult<Expression<String>> = { parent?.labelId?.value() ?? .noValue }()
    var patternValue: DeserializationResult<Expression<String>> = { parent?.pattern?.value() ?? .noValue }()
    var variableValue: DeserializationResult<String> = { parent?.variable?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "allow_empty" {
           allowEmptyValue = deserialize(__dictValue).merged(with: allowEmptyValue)
          }
        }()
        _ = {
          if key == "label_id" {
           labelIdValue = deserialize(__dictValue).merged(with: labelIdValue)
          }
        }()
        _ = {
          if key == "pattern" {
           patternValue = deserialize(__dictValue).merged(with: patternValue)
          }
        }()
        _ = {
          if key == "variable" {
           variableValue = deserialize(__dictValue).merged(with: variableValue)
          }
        }()
        _ = {
         if key == parent?.allowEmpty?.link {
           allowEmptyValue = allowEmptyValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.labelId?.link {
           labelIdValue = labelIdValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.pattern?.link {
           patternValue = patternValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.variable?.link {
           variableValue = variableValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
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
      allowEmpty: { allowEmptyValue.value }(),
      labelId: { labelIdNonNil }(),
      pattern: { patternNonNil }(),
      variable: { variableNonNil }()
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
