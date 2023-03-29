// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization
import TemplatesSupport

public final class DivCircleShapeTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "circle"
  public let parent: String? // at least 1 char
  public let radius: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(10))

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      radius: try dictionary.getOptionalField("radius", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    radius: Field<DivFixedSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.radius = radius
  }

  private static func resolveOnlyLinks(context: Context, parent: DivCircleShapeTemplate?) -> DeserializationResult<DivCircleShape> {
    let radiusValue = parent?.radius?.resolveOptionalValue(context: context, validator: ResolvedValue.radiusValidator, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      radiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "radius", error: $0) }
    )
    let result = DivCircleShape(
      radius: radiusValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivCircleShapeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCircleShape> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var radiusValue: DeserializationResult<DivFixedSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "radius":
        radiusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.radiusValidator, type: DivFixedSizeTemplate.self).merged(with: radiusValue)
      case parent?.radius?.link:
        radiusValue = radiusValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.radiusValidator, type: DivFixedSizeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      radiusValue = radiusValue.merged(with: parent.radius?.resolveOptionalValue(context: context, validator: ResolvedValue.radiusValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      radiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "radius", error: $0) }
    )
    let result = DivCircleShape(
      radius: radiusValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivCircleShapeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivCircleShapeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivCircleShapeTemplate(
      parent: nil,
      radius: radius ?? mergedParent.radius
    )
  }

  public func resolveParent(templates: Templates) throws -> DivCircleShapeTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivCircleShapeTemplate(
      parent: nil,
      radius: merged.radius?.tryResolveParent(templates: templates)
    )
  }
}
