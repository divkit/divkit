// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivLayoutProviderTemplate: TemplateValue {
  public let heightVariableName: Field<Expression<String>>?
  public let widthVariableName: Field<Expression<String>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      heightVariableName: dictionary.getOptionalExpressionField("height_variable_name"),
      widthVariableName: dictionary.getOptionalExpressionField("width_variable_name")
    )
  }

  init(
    heightVariableName: Field<Expression<String>>? = nil,
    widthVariableName: Field<Expression<String>>? = nil
  ) {
    self.heightVariableName = heightVariableName
    self.widthVariableName = widthVariableName
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivLayoutProviderTemplate?) -> DeserializationResult<DivLayoutProvider> {
    let heightVariableNameValue = parent?.heightVariableName?.resolveOptionalValue(context: context) ?? .noValue
    let widthVariableNameValue = parent?.widthVariableName?.resolveOptionalValue(context: context) ?? .noValue
    let errors = mergeErrors(
      heightVariableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "height_variable_name", error: $0) },
      widthVariableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "width_variable_name", error: $0) }
    )
    let result = DivLayoutProvider(
      heightVariableName: heightVariableNameValue.value,
      widthVariableName: widthVariableNameValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivLayoutProviderTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivLayoutProvider> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var heightVariableNameValue: DeserializationResult<Expression<String>> = parent?.heightVariableName?.value() ?? .noValue
    var widthVariableNameValue: DeserializationResult<Expression<String>> = parent?.widthVariableName?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "height_variable_name":
        heightVariableNameValue = deserialize(__dictValue).merged(with: heightVariableNameValue)
      case "width_variable_name":
        widthVariableNameValue = deserialize(__dictValue).merged(with: widthVariableNameValue)
      case parent?.heightVariableName?.link:
        heightVariableNameValue = heightVariableNameValue.merged(with: { deserialize(__dictValue) })
      case parent?.widthVariableName?.link:
        widthVariableNameValue = widthVariableNameValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    let errors = mergeErrors(
      heightVariableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "height_variable_name", error: $0) },
      widthVariableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "width_variable_name", error: $0) }
    )
    let result = DivLayoutProvider(
      heightVariableName: heightVariableNameValue.value,
      widthVariableName: widthVariableNameValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivLayoutProviderTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivLayoutProviderTemplate {
    return try mergedWithParent(templates: templates)
  }
}
