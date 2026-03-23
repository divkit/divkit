// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivLayoutProviderTemplate: TemplateValue, Sendable {
  public let heightVariableName: Field<String>?
  public let widthVariableName: Field<String>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      heightVariableName: dictionary.getOptionalField("height_variable_name"),
      widthVariableName: dictionary.getOptionalField("width_variable_name")
    )
  }

  init(
    heightVariableName: Field<String>? = nil,
    widthVariableName: Field<String>? = nil
  ) {
    self.heightVariableName = heightVariableName
    self.widthVariableName = widthVariableName
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivLayoutProviderTemplate?) -> DeserializationResult<DivLayoutProvider> {
    let heightVariableNameValue = { parent?.heightVariableName?.resolveOptionalValue(context: context) ?? .noValue }()
    let widthVariableNameValue = { parent?.widthVariableName?.resolveOptionalValue(context: context) ?? .noValue }()
    let errors = mergeErrors(
      heightVariableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "height_variable_name", error: $0) },
      widthVariableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "width_variable_name", error: $0) }
    )
    let result = DivLayoutProvider(
      heightVariableName: { heightVariableNameValue.value }(),
      widthVariableName: { widthVariableNameValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivLayoutProviderTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivLayoutProvider> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var heightVariableNameValue: DeserializationResult<String> = { parent?.heightVariableName?.value() ?? .noValue }()
    var widthVariableNameValue: DeserializationResult<String> = { parent?.widthVariableName?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "height_variable_name" {
           heightVariableNameValue = deserialize(__dictValue).merged(with: heightVariableNameValue)
          }
        }()
        _ = {
          if key == "width_variable_name" {
           widthVariableNameValue = deserialize(__dictValue).merged(with: widthVariableNameValue)
          }
        }()
        _ = {
         if key == parent?.heightVariableName?.link {
           heightVariableNameValue = heightVariableNameValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.widthVariableName?.link {
           widthVariableNameValue = widthVariableNameValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    let errors = mergeErrors(
      heightVariableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "height_variable_name", error: $0) },
      widthVariableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "width_variable_name", error: $0) }
    )
    let result = DivLayoutProvider(
      heightVariableName: { heightVariableNameValue.value }(),
      widthVariableName: { widthVariableNameValue.value }()
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
