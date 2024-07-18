// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivBlurTemplate: TemplateValue {
  public static let type: String = "blur"
  public let parent: String?
  public let radius: Field<Expression<Int>>? // constraint: number >= 0

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      radius: dictionary.getOptionalExpressionField("radius")
    )
  }

  init(
    parent: String?,
    radius: Field<Expression<Int>>? = nil
  ) {
    self.parent = parent
    self.radius = radius
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivBlurTemplate?) -> DeserializationResult<DivBlur> {
    let radiusValue = parent?.radius?.resolveValue(context: context, validator: ResolvedValue.radiusValidator) ?? .noValue
    var errors = mergeErrors(
      radiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "radius", error: $0) }
    )
    if case .noValue = radiusValue {
      errors.append(.requiredFieldIsMissing(field: "radius"))
    }
    guard
      let radiusNonNil = radiusValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivBlur(
      radius: radiusNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivBlurTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivBlur> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var radiusValue: DeserializationResult<Expression<Int>> = parent?.radius?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "radius":
        radiusValue = deserialize(__dictValue, validator: ResolvedValue.radiusValidator).merged(with: radiusValue)
      case parent?.radius?.link:
        radiusValue = radiusValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.radiusValidator) })
      default: break
      }
    }
    var errors = mergeErrors(
      radiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "radius", error: $0) }
    )
    if case .noValue = radiusValue {
      errors.append(.requiredFieldIsMissing(field: "radius"))
    }
    guard
      let radiusNonNil = radiusValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivBlur(
      radius: radiusNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivBlurTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivBlurTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivBlurTemplate(
      parent: nil,
      radius: radius ?? mergedParent.radius
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivBlurTemplate {
    return try mergedWithParent(templates: templates)
  }
}
