// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivChangeSetTransitionTemplate: TemplateValue {
  public static let type: String = "set"
  public let parent: String? // at least 1 char
  public let items: Field<[DivChangeTransitionTemplate]>? // at least 1 elements

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        items: try dictionary.getOptionalArray("items", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-change-set-transition_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    items: Field<[DivChangeTransitionTemplate]>? = nil
  ) {
    self.parent = parent
    self.items = items
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivChangeSetTransitionTemplate?) -> DeserializationResult<DivChangeSetTransition> {
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
    let result = DivChangeSetTransition(
      items: itemsNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivChangeSetTransitionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivChangeSetTransition> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var itemsValue: DeserializationResult<[DivChangeTransition]> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "items":
        itemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: DivChangeTransitionTemplate.self).merged(with: itemsValue)
      case parent?.items?.link:
        itemsValue = itemsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: DivChangeTransitionTemplate.self))
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
    let result = DivChangeSetTransition(
      items: itemsNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivChangeSetTransitionTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivChangeSetTransitionTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivChangeSetTransitionTemplate(
      parent: nil,
      items: items ?? mergedParent.items
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivChangeSetTransitionTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivChangeSetTransitionTemplate(
      parent: nil,
      items: try merged.items?.resolveParent(templates: templates)
    )
  }
}
