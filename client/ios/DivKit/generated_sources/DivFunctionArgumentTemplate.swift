// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivFunctionArgumentTemplate: TemplateValue {
  public let name: Field<String>?
  public let type: Field<DivEvaluableType>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      name: dictionary.getOptionalField("name"),
      type: dictionary.getOptionalField("type")
    )
  }

  init(
    name: Field<String>? = nil,
    type: Field<DivEvaluableType>? = nil
  ) {
    self.name = name
    self.type = type
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivFunctionArgumentTemplate?) -> DeserializationResult<DivFunctionArgument> {
    let nameValue = { parent?.name?.resolveValue(context: context) ?? .noValue }()
    let typeValue = { parent?.type?.resolveValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
      typeValue.errorsOrWarnings?.map { .nestedObjectError(field: "type", error: $0) }
    )
    if case .noValue = nameValue {
      errors.append(.requiredFieldIsMissing(field: "name"))
    }
    if case .noValue = typeValue {
      errors.append(.requiredFieldIsMissing(field: "type"))
    }
    guard
      let nameNonNil = nameValue.value,
      let typeNonNil = typeValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivFunctionArgument(
      name: { nameNonNil }(),
      type: { typeNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivFunctionArgumentTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivFunctionArgument> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var nameValue: DeserializationResult<String> = { parent?.name?.value() ?? .noValue }()
    var typeValue: DeserializationResult<DivEvaluableType> = { parent?.type?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "name" {
           nameValue = deserialize(__dictValue).merged(with: nameValue)
          }
        }()
        _ = {
          if key == "type" {
           typeValue = deserialize(__dictValue).merged(with: typeValue)
          }
        }()
        _ = {
         if key == parent?.name?.link {
           nameValue = nameValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.type?.link {
           typeValue = typeValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    var errors = mergeErrors(
      nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
      typeValue.errorsOrWarnings?.map { .nestedObjectError(field: "type", error: $0) }
    )
    if case .noValue = nameValue {
      errors.append(.requiredFieldIsMissing(field: "name"))
    }
    if case .noValue = typeValue {
      errors.append(.requiredFieldIsMissing(field: "type"))
    }
    guard
      let nameNonNil = nameValue.value,
      let typeNonNil = typeValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivFunctionArgument(
      name: { nameNonNil }(),
      type: { typeNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivFunctionArgumentTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivFunctionArgumentTemplate {
    return try mergedWithParent(templates: templates)
  }
}
