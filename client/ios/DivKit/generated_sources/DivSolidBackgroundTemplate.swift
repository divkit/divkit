// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivSolidBackgroundTemplate: TemplateValue {
  public static let type: String = "solid"
  public let parent: String? // at least 1 char
  public let color: Field<Expression<Color>>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        color: try dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:))
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-solid-background_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    color: Field<Expression<Color>>? = nil
  ) {
    self.parent = parent
    self.color = color
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivSolidBackgroundTemplate?) -> DeserializationResult<DivSolidBackground> {
    let colorValue = parent?.color?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) }
    )
    if case .noValue = colorValue {
      errors.append(.requiredFieldIsMissing(field: "color"))
    }
    guard
      let colorNonNil = colorValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivSolidBackground(
      color: colorNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivSolidBackgroundTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSolidBackground> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var colorValue: DeserializationResult<Expression<Color>> = parent?.color?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "color":
        colorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: colorValue)
      case parent?.color?.link:
        colorValue = colorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:)))
      default: break
      }
    }
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) }
    )
    if case .noValue = colorValue {
      errors.append(.requiredFieldIsMissing(field: "color"))
    }
    guard
      let colorNonNil = colorValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivSolidBackground(
      color: colorNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivSolidBackgroundTemplate {
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

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivSolidBackgroundTemplate {
    return try mergedWithParent(templates: templates)
  }
}
