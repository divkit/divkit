// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivStrokeTemplate: TemplateValue {
  public let color: Field<Expression<Color>>?
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp
  public let width: Field<Expression<Double>>? // constraint: number >= 0; default value: 1

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      color: dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:)),
      unit: dictionary.getOptionalExpressionField("unit"),
      width: dictionary.getOptionalExpressionField("width")
    )
  }

  init(
    color: Field<Expression<Color>>? = nil,
    unit: Field<Expression<DivSizeUnit>>? = nil,
    width: Field<Expression<Double>>? = nil
  ) {
    self.color = color
    self.unit = unit
    self.width = width
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivStrokeTemplate?) -> DeserializationResult<DivStroke> {
    let colorValue = parent?.color?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue
    let unitValue = parent?.unit?.resolveOptionalValue(context: context) ?? .noValue
    let widthValue = parent?.width?.resolveOptionalValue(context: context, validator: ResolvedValue.widthValidator) ?? .noValue
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
      unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = colorValue {
      errors.append(.requiredFieldIsMissing(field: "color"))
    }
    guard
      let colorNonNil = colorValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivStroke(
      color: colorNonNil,
      unit: unitValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivStrokeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivStroke> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var colorValue: DeserializationResult<Expression<Color>> = parent?.color?.value() ?? .noValue
    var unitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.unit?.value() ?? .noValue
    var widthValue: DeserializationResult<Expression<Double>> = parent?.width?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "color":
        colorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: colorValue)
      case "unit":
        unitValue = deserialize(__dictValue).merged(with: unitValue)
      case "width":
        widthValue = deserialize(__dictValue, validator: ResolvedValue.widthValidator).merged(with: widthValue)
      case parent?.color?.link:
        colorValue = colorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
      case parent?.unit?.link:
        unitValue = unitValue.merged(with: { deserialize(__dictValue) })
      case parent?.width?.link:
        widthValue = widthValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.widthValidator) })
      default: break
      }
    }
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
      unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = colorValue {
      errors.append(.requiredFieldIsMissing(field: "color"))
    }
    guard
      let colorNonNil = colorValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivStroke(
      color: colorNonNil,
      unit: unitValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivStrokeTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivStrokeTemplate {
    return try mergedWithParent(templates: templates)
  }
}
