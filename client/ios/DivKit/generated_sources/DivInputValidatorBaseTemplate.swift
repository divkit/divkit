// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInputValidatorBaseTemplate: TemplateValue, Sendable {
  public let allowEmpty: Field<Expression<Bool>>? // default value: false
  public let labelId: Field<Expression<String>>?
  public let variable: Field<String>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      allowEmpty: dictionary.getOptionalExpressionField("allow_empty"),
      labelId: dictionary.getOptionalExpressionField("label_id"),
      variable: dictionary.getOptionalField("variable")
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
    let allowEmptyValue = { parent?.allowEmpty?.resolveOptionalValue(context: context) ?? .noValue }()
    let labelIdValue = { parent?.labelId?.resolveOptionalValue(context: context) ?? .noValue }()
    let variableValue = { parent?.variable?.resolveOptionalValue(context: context) ?? .noValue }()
    let errors = mergeErrors(
      allowEmptyValue.errorsOrWarnings?.map { .nestedObjectError(field: "allow_empty", error: $0) },
      labelIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "label_id", error: $0) },
      variableValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable", error: $0) }
    )
    let result = DivInputValidatorBase(
      allowEmpty: { allowEmptyValue.value }(),
      labelId: { labelIdValue.value }(),
      variable: { variableValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInputValidatorBaseTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputValidatorBase> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var allowEmptyValue: DeserializationResult<Expression<Bool>> = { parent?.allowEmpty?.value() ?? .noValue }()
    var labelIdValue: DeserializationResult<Expression<String>> = { parent?.labelId?.value() ?? .noValue }()
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
         if key == parent?.variable?.link {
           variableValue = variableValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    let errors = mergeErrors(
      allowEmptyValue.errorsOrWarnings?.map { .nestedObjectError(field: "allow_empty", error: $0) },
      labelIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "label_id", error: $0) },
      variableValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable", error: $0) }
    )
    let result = DivInputValidatorBase(
      allowEmpty: { allowEmptyValue.value }(),
      labelId: { labelIdValue.value }(),
      variable: { variableValue.value }()
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
