// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivFunctionTemplate: TemplateValue, Sendable {
  public let arguments: Field<[DivFunctionArgumentTemplate]>?
  public let body: Field<String>?
  public let name: Field<String>? // regex: ^[a-zA-Z_][a-zA-Z0-9_]*$
  public let returnType: Field<DivEvaluableType>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      arguments: dictionary.getOptionalArray("arguments", templateToType: templateToType),
      body: dictionary.getOptionalField("body"),
      name: dictionary.getOptionalField("name"),
      returnType: dictionary.getOptionalField("return_type")
    )
  }

  init(
    arguments: Field<[DivFunctionArgumentTemplate]>? = nil,
    body: Field<String>? = nil,
    name: Field<String>? = nil,
    returnType: Field<DivEvaluableType>? = nil
  ) {
    self.arguments = arguments
    self.body = body
    self.name = name
    self.returnType = returnType
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivFunctionTemplate?) -> DeserializationResult<DivFunction> {
    let argumentsValue = { parent?.arguments?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let bodyValue = { parent?.body?.resolveValue(context: context) ?? .noValue }()
    let nameValue = { parent?.name?.resolveValue(context: context, validator: ResolvedValue.nameValidator) ?? .noValue }()
    let returnTypeValue = { parent?.returnType?.resolveValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      argumentsValue.errorsOrWarnings?.map { .nestedObjectError(field: "arguments", error: $0) },
      bodyValue.errorsOrWarnings?.map { .nestedObjectError(field: "body", error: $0) },
      nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
      returnTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "return_type", error: $0) }
    )
    if case .noValue = argumentsValue {
      errors.append(.requiredFieldIsMissing(field: "arguments"))
    }
    if case .noValue = bodyValue {
      errors.append(.requiredFieldIsMissing(field: "body"))
    }
    if case .noValue = nameValue {
      errors.append(.requiredFieldIsMissing(field: "name"))
    }
    if case .noValue = returnTypeValue {
      errors.append(.requiredFieldIsMissing(field: "return_type"))
    }
    guard
      let argumentsNonNil = argumentsValue.value,
      let bodyNonNil = bodyValue.value,
      let nameNonNil = nameValue.value,
      let returnTypeNonNil = returnTypeValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivFunction(
      arguments: { argumentsNonNil }(),
      body: { bodyNonNil }(),
      name: { nameNonNil }(),
      returnType: { returnTypeNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivFunctionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivFunction> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var argumentsValue: DeserializationResult<[DivFunctionArgument]> = .noValue
    var bodyValue: DeserializationResult<String> = { parent?.body?.value() ?? .noValue }()
    var nameValue: DeserializationResult<String> = { parent?.name?.value(validatedBy: ResolvedValue.nameValidator) ?? .noValue }()
    var returnTypeValue: DeserializationResult<DivEvaluableType> = { parent?.returnType?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "arguments" {
           argumentsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFunctionArgumentTemplate.self).merged(with: argumentsValue)
          }
        }()
        _ = {
          if key == "body" {
           bodyValue = deserialize(__dictValue).merged(with: bodyValue)
          }
        }()
        _ = {
          if key == "name" {
           nameValue = deserialize(__dictValue, validator: ResolvedValue.nameValidator).merged(with: nameValue)
          }
        }()
        _ = {
          if key == "return_type" {
           returnTypeValue = deserialize(__dictValue).merged(with: returnTypeValue)
          }
        }()
        _ = {
         if key == parent?.arguments?.link {
           argumentsValue = argumentsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFunctionArgumentTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.body?.link {
           bodyValue = bodyValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.name?.link {
           nameValue = nameValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.nameValidator) })
          }
        }()
        _ = {
         if key == parent?.returnType?.link {
           returnTypeValue = returnTypeValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { argumentsValue = argumentsValue.merged(with: { parent.arguments?.resolveValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      argumentsValue.errorsOrWarnings?.map { .nestedObjectError(field: "arguments", error: $0) },
      bodyValue.errorsOrWarnings?.map { .nestedObjectError(field: "body", error: $0) },
      nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
      returnTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "return_type", error: $0) }
    )
    if case .noValue = argumentsValue {
      errors.append(.requiredFieldIsMissing(field: "arguments"))
    }
    if case .noValue = bodyValue {
      errors.append(.requiredFieldIsMissing(field: "body"))
    }
    if case .noValue = nameValue {
      errors.append(.requiredFieldIsMissing(field: "name"))
    }
    if case .noValue = returnTypeValue {
      errors.append(.requiredFieldIsMissing(field: "return_type"))
    }
    guard
      let argumentsNonNil = argumentsValue.value,
      let bodyNonNil = bodyValue.value,
      let nameNonNil = nameValue.value,
      let returnTypeNonNil = returnTypeValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivFunction(
      arguments: { argumentsNonNil }(),
      body: { bodyNonNil }(),
      name: { nameNonNil }(),
      returnType: { returnTypeNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivFunctionTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivFunctionTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivFunctionTemplate(
      arguments: try merged.arguments?.resolveParent(templates: templates),
      body: merged.body,
      name: merged.name,
      returnType: merged.returnType
    )
  }
}
