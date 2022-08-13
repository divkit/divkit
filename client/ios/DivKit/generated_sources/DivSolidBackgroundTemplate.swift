// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivSolidBackgroundTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "solid"
  public let parent: String? // at least 1 char
  public let color: Field<Expression<Color>>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType _: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        color: try dictionary.getOptionalField("color", transform: Color.color(withHexString:))
      )
    } catch let DeserializationError.invalidFieldRepresentation(
      field: field,
      representation: representation
    ) {
      throw DeserializationError.invalidFieldRepresentation(
        field: "div-solid-background_template." + field,
        representation: representation
      )
    }
  }

  init(
    parent: String?,
    color: Field<Expression<Color>>? = nil
  ) {
    self.parent = parent
    self.color = color
  }

  private static func resolveOnlyLinks(
    context: Context,
    parent: DivSolidBackgroundTemplate?
  ) -> DeserializationResult<DivSolidBackground> {
    let colorValue = parent?.color?.resolveValue(
      context: context,
      transform: Color.color(withHexString:)
    ) ?? .noValue
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "color", level: .error)) }
    )
    if case .noValue = colorValue {
      errors
        .append(.right(FieldError(
          fieldName: "color",
          level: .error,
          error: .requiredFieldIsMissing
        )))
    }
    guard
      let colorNonNil = colorValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivSolidBackground(
      color: colorNonNil
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(
    context: Context,
    parent: DivSolidBackgroundTemplate?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<DivSolidBackground> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var colorValue: DeserializationResult<Expression<Color>> = parent?.color?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "color":
        colorValue = deserialize(__dictValue, transform: Color.color(withHexString:))
          .merged(with: colorValue)
      case parent?.color?.link:
        colorValue = colorValue
          .merged(with: deserialize(__dictValue, transform: Color.color(withHexString:)))
      default: break
      }
    }
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "color", level: .error)) }
    )
    if case .noValue = colorValue {
      errors
        .append(.right(FieldError(
          fieldName: "color",
          level: .error,
          error: .requiredFieldIsMissing
        )))
    }
    guard
      let colorNonNil = colorValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivSolidBackground(
      color: colorNonNil
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivSolidBackgroundTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivSolidBackgroundTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivSolidBackgroundTemplate(
      parent: nil,
      color: color ?? mergedParent.color
    )
  }

  public func resolveParent(templates: Templates) throws -> DivSolidBackgroundTemplate {
    try mergedWithParent(templates: templates)
  }
}
