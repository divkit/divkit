// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivShapeDrawableTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "shape_drawable"
  public let parent: String? // at least 1 char
  public let color: Field<Expression<Color>>?
  public let shape: Field<DivShapeTemplate>?
  public let stroke: Field<DivStrokeTemplate>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        color: try dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:)),
        shape: try dictionary.getOptionalField("shape", templateToType: templateToType),
        stroke: try dictionary.getOptionalField("stroke", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-shape-drawable_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    color: Field<Expression<Color>>? = nil,
    shape: Field<DivShapeTemplate>? = nil,
    stroke: Field<DivStrokeTemplate>? = nil
  ) {
    self.parent = parent
    self.color = color
    self.shape = shape
    self.stroke = stroke
  }

  private static func resolveOnlyLinks(context: Context, parent: DivShapeDrawableTemplate?) -> DeserializationResult<DivShapeDrawable> {
    let colorValue = parent?.color?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue
    let shapeValue = parent?.shape?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    let strokeValue = parent?.stroke?.resolveOptionalValue(context: context, validator: ResolvedValue.strokeValidator, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "color", level: .error)) },
      shapeValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "shape", level: .error)) },
      strokeValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "stroke", level: .warning)) }
    )
    if case .noValue = colorValue {
      errors.append(.left(DeserializationError.requiredFieldIsMissing(fieldName: "color")))
    }
    if case .noValue = shapeValue {
      errors.append(.left(DeserializationError.requiredFieldIsMissing(fieldName: "shape")))
    }
    guard
      let colorNonNil = colorValue.value,
      let shapeNonNil = shapeValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivShapeDrawable(
      color: colorNonNil,
      shape: shapeNonNil,
      stroke: strokeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivShapeDrawableTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivShapeDrawable> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var colorValue: DeserializationResult<Expression<Color>> = parent?.color?.value() ?? .noValue
    var shapeValue: DeserializationResult<DivShape> = .noValue
    var strokeValue: DeserializationResult<DivStroke> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "color":
        colorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: colorValue)
      case "shape":
        shapeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShapeTemplate.self).merged(with: shapeValue)
      case "stroke":
        strokeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.strokeValidator, type: DivStrokeTemplate.self).merged(with: strokeValue)
      case parent?.color?.link:
        colorValue = colorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:)))
      case parent?.shape?.link:
        shapeValue = shapeValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShapeTemplate.self))
      case parent?.stroke?.link:
        strokeValue = strokeValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.strokeValidator, type: DivStrokeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      shapeValue = shapeValue.merged(with: parent.shape?.resolveValue(context: context, useOnlyLinks: true))
      strokeValue = strokeValue.merged(with: parent.stroke?.resolveOptionalValue(context: context, validator: ResolvedValue.strokeValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "color", level: .error)) },
      shapeValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "shape", level: .error)) },
      strokeValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "stroke", level: .warning)) }
    )
    if case .noValue = colorValue {
      errors.append(.left(DeserializationError.requiredFieldIsMissing(fieldName: "color")))
    }
    if case .noValue = shapeValue {
      errors.append(.left(DeserializationError.requiredFieldIsMissing(fieldName: "shape")))
    }
    guard
      let colorNonNil = colorValue.value,
      let shapeNonNil = shapeValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivShapeDrawable(
      color: colorNonNil,
      shape: shapeNonNil,
      stroke: strokeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivShapeDrawableTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivShapeDrawableTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivShapeDrawableTemplate(
      parent: nil,
      color: color ?? mergedParent.color,
      shape: shape ?? mergedParent.shape,
      stroke: stroke ?? mergedParent.stroke
    )
  }

  public func resolveParent(templates: Templates) throws -> DivShapeDrawableTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivShapeDrawableTemplate(
      parent: nil,
      color: merged.color,
      shape: try merged.shape?.resolveParent(templates: templates),
      stroke: merged.stroke?.tryResolveParent(templates: templates)
    )
  }
}
