// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivRadialGradientRelativeRadiusTemplate: TemplateValue {
  public typealias Value = DivRadialGradientRelativeRadius.Value

  public static let type: String = "relative"
  public let parent: String? // at least 1 char
  public let value: Field<Expression<Value>>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        value: try dictionary.getOptionalExpressionField("value")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-radial-gradient-relative-radius_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    value: Field<Expression<Value>>? = nil
  ) {
    self.parent = parent
    self.value = value
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivRadialGradientRelativeRadiusTemplate?) -> DeserializationResult<DivRadialGradientRelativeRadius> {
    let valueValue = parent?.value?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
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
    let result = DivRadialGradientRelativeRadius(
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivRadialGradientRelativeRadiusTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivRadialGradientRelativeRadius> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var valueValue: DeserializationResult<Expression<DivRadialGradientRelativeRadius.Value>> = parent?.value?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "value":
        valueValue = deserialize(__dictValue).merged(with: valueValue)
      case parent?.value?.link:
        valueValue = valueValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    var errors = mergeErrors(
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
    let result = DivRadialGradientRelativeRadius(
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivRadialGradientRelativeRadiusTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivRadialGradientRelativeRadiusTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivRadialGradientRelativeRadiusTemplate(
      parent: nil,
      value: value ?? mergedParent.value
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivRadialGradientRelativeRadiusTemplate {
    return try mergedWithParent(templates: templates)
  }
}
