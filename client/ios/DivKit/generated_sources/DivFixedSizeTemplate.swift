// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivFixedSizeTemplate: TemplateValue {
  public static let type: String = "fixed"
  public let parent: String? // at least 1 char
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp
  public let value: Field<Expression<Int>>? // constraint: number >= 0

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        unit: try dictionary.getOptionalExpressionField("unit"),
        value: try dictionary.getOptionalExpressionField("value")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-fixed-size_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    unit: Field<Expression<DivSizeUnit>>? = nil,
    value: Field<Expression<Int>>? = nil
  ) {
    self.parent = parent
    self.unit = unit
    self.value = value
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivFixedSizeTemplate?) -> DeserializationResult<DivFixedSize> {
    let unitValue = parent?.unit?.resolveOptionalValue(context: context, validator: ResolvedValue.unitValidator) ?? .noValue
    let valueValue = parent?.value?.resolveValue(context: context, validator: ResolvedValue.valueValidator) ?? .noValue
    var errors = mergeErrors(
      unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    guard
      let valueNonNil = valueValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivFixedSize(
      unit: unitValue.value,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivFixedSizeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivFixedSize> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var unitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.unit?.value() ?? .noValue
    var valueValue: DeserializationResult<Expression<Int>> = parent?.value?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "unit":
        unitValue = deserialize(__dictValue, validator: ResolvedValue.unitValidator).merged(with: unitValue)
      case "value":
        valueValue = deserialize(__dictValue, validator: ResolvedValue.valueValidator).merged(with: valueValue)
      case parent?.unit?.link:
        unitValue = unitValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.unitValidator))
      case parent?.value?.link:
        valueValue = valueValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.valueValidator))
      default: break
      }
    }
    var errors = mergeErrors(
      unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    guard
      let valueNonNil = valueValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivFixedSize(
      unit: unitValue.value,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivFixedSizeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivFixedSizeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivFixedSizeTemplate(
      parent: nil,
      unit: unit ?? mergedParent.unit,
      value: value ?? mergedParent.value
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivFixedSizeTemplate {
    return try mergedWithParent(templates: templates)
  }
}
