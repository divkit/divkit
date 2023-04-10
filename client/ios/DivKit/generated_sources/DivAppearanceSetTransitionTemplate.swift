// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivAppearanceSetTransitionTemplate: TemplateValue {
  public static let type: String = "set"
  public let parent: String? // at least 1 char
  public let items: Field<[DivAppearanceTransitionTemplate]>? // at least 1 elements

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        items: try dictionary.getOptionalArray("items", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-appearance-set-transition_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    items: Field<[DivAppearanceTransitionTemplate]>? = nil
  ) {
    self.parent = parent
    self.items = items
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivAppearanceSetTransitionTemplate?) -> DeserializationResult<DivAppearanceSetTransition> {
    let itemsValue = parent?.items?.resolveValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) }
    )
    if case .noValue = itemsValue {
      errors.append(.requiredFieldIsMissing(field: "items"))
    }
    guard
      let itemsNonNil = itemsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivAppearanceSetTransition(
      items: itemsNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivAppearanceSetTransitionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAppearanceSetTransition> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var itemsValue: DeserializationResult<[DivAppearanceTransition]> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "items":
        itemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: DivAppearanceTransitionTemplate.self).merged(with: itemsValue)
      case parent?.items?.link:
        itemsValue = itemsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: DivAppearanceTransitionTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      itemsValue = itemsValue.merged(with: parent.items?.resolveValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) }
    )
    if case .noValue = itemsValue {
      errors.append(.requiredFieldIsMissing(field: "items"))
    }
    guard
      let itemsNonNil = itemsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivAppearanceSetTransition(
      items: itemsNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivAppearanceSetTransitionTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivAppearanceSetTransitionTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivAppearanceSetTransitionTemplate(
      parent: nil,
      items: items ?? mergedParent.items
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivAppearanceSetTransitionTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivAppearanceSetTransitionTemplate(
      parent: nil,
      items: try merged.items?.resolveParent(templates: templates)
    )
  }
}
