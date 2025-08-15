// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

public final class EntityWithStringArrayPropertyTemplate: TemplateValue, Sendable {
  public static let type: String = "entity_with_string_array_property"
  public let parent: String?
  public let array: Field<[Expression<String>]>? // at least 1 elements

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      array: dictionary.getOptionalExpressionArray("array")
    )
  }

  init(
    parent: String?,
    array: Field<[Expression<String>]>? = nil
  ) {
    self.parent = parent
    self.array = array
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithStringArrayPropertyTemplate?) -> DeserializationResult<EntityWithStringArrayProperty> {
    let arrayValue = { parent?.array?.resolveValue(context: context, validator: ResolvedValue.arrayValidator) ?? .noValue }()
    var errors = mergeErrors(
      arrayValue.errorsOrWarnings?.map { .nestedObjectError(field: "array", error: $0) }
    )
    if case .noValue = arrayValue {
      errors.append(.requiredFieldIsMissing(field: "array"))
    }
    guard
      let arrayNonNil = arrayValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = EntityWithStringArrayProperty(
      array: { arrayNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithStringArrayPropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithStringArrayProperty> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var arrayValue: DeserializationResult<[Expression<String>]> = { parent?.array?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "array" {
           arrayValue = deserialize(__dictValue, validator: ResolvedValue.arrayValidator).merged(with: arrayValue)
          }
        }()
        _ = {
         if key == parent?.array?.link {
           arrayValue = arrayValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.arrayValidator) })
          }
        }()
      }
    }()
    var errors = mergeErrors(
      arrayValue.errorsOrWarnings?.map { .nestedObjectError(field: "array", error: $0) }
    )
    if case .noValue = arrayValue {
      errors.append(.requiredFieldIsMissing(field: "array"))
    }
    guard
      let arrayNonNil = arrayValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = EntityWithStringArrayProperty(
      array: { arrayNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithStringArrayPropertyTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithStringArrayPropertyTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithStringArrayPropertyTemplate(
      parent: nil,
      array: array ?? mergedParent.array
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithStringArrayPropertyTemplate {
    return try mergedWithParent(templates: templates)
  }
}
