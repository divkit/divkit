// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithArrayWithTransformTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "entity_with_array_with_transform"
  public let parent: String? // at least 1 char
  public let array: Field<[Expression<Color>]>? // at least 1 elements

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        array: try dictionary.getOptionalExpressionArray("array", transform: Color.color(withHexString:))
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "entity_with_array_with_transform_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    array: Field<[Expression<Color>]>? = nil
  ) {
    self.parent = parent
    self.array = array
  }

  private static func resolveOnlyLinks(context: Context, parent: EntityWithArrayWithTransformTemplate?) -> DeserializationResult<EntityWithArrayWithTransform> {
    let arrayValue = parent?.array?.resolveValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.arrayValidator) ?? .noValue
    var errors = mergeErrors(
      arrayValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "array", level: .error)) }
    )
    if case .noValue = arrayValue {
      errors.append(.right(FieldError(fieldName: "array", level: .error, error: .requiredFieldIsMissing)))
    }
    guard
      let arrayNonNil = arrayValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = EntityWithArrayWithTransform(
      array: arrayNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: EntityWithArrayWithTransformTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithArrayWithTransform> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var arrayValue: DeserializationResult<[Expression<Color>]> = parent?.array?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "array":
        arrayValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.arrayValidator).merged(with: arrayValue)
      case parent?.array?.link:
        arrayValue = arrayValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.arrayValidator))
      default: break
      }
    }
    var errors = mergeErrors(
      arrayValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "array", level: .error)) }
    )
    if case .noValue = arrayValue {
      errors.append(.right(FieldError(fieldName: "array", level: .error, error: .requiredFieldIsMissing)))
    }
    guard
      let arrayNonNil = arrayValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = EntityWithArrayWithTransform(
      array: arrayNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> EntityWithArrayWithTransformTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithArrayWithTransformTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithArrayWithTransformTemplate(
      parent: nil,
      array: array ?? mergedParent.array
    )
  }

  public func resolveParent(templates: Templates) throws -> EntityWithArrayWithTransformTemplate {
    return try mergedWithParent(templates: templates)
  }
}
