// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivExtensionTemplate: TemplateValue, @unchecked Sendable {
  public let id: Field<String>?
  public let params: Field<[String: Any]>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      id: dictionary.getOptionalField("id"),
      params: dictionary.getOptionalField("params")
    )
  }

  init(
    id: Field<String>? = nil,
    params: Field<[String: Any]>? = nil
  ) {
    self.id = id
    self.params = params
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivExtensionTemplate?) -> DeserializationResult<DivExtension> {
    let idValue = { parent?.id?.resolveValue(context: context) ?? .noValue }()
    let paramsValue = { parent?.params?.resolveOptionalValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      paramsValue.errorsOrWarnings?.map { .nestedObjectError(field: "params", error: $0) }
    )
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivExtension(
      id: { idNonNil }(),
      params: { paramsValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivExtensionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivExtension> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var idValue: DeserializationResult<String> = { parent?.id?.value() ?? .noValue }()
    var paramsValue: DeserializationResult<[String: Any]> = { parent?.params?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "id" {
           idValue = deserialize(__dictValue).merged(with: idValue)
          }
        }()
        _ = {
          if key == "params" {
           paramsValue = deserialize(__dictValue).merged(with: paramsValue)
          }
        }()
        _ = {
         if key == parent?.id?.link {
           idValue = idValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.params?.link {
           paramsValue = paramsValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    var errors = mergeErrors(
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      paramsValue.errorsOrWarnings?.map { .nestedObjectError(field: "params", error: $0) }
    )
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivExtension(
      id: { idNonNil }(),
      params: { paramsValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivExtensionTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivExtensionTemplate {
    return try mergedWithParent(templates: templates)
  }
}
