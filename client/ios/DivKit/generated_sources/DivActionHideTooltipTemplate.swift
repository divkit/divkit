// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionHideTooltipTemplate: TemplateValue {
  public static let type: String = "hide_tooltip"
  public let parent: String?
  public let id: Field<Expression<String>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      id: dictionary.getOptionalExpressionField("id")
    )
  }

  init(
    parent: String?,
    id: Field<Expression<String>>? = nil
  ) {
    self.parent = parent
    self.id = id
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionHideTooltipTemplate?) -> DeserializationResult<DivActionHideTooltip> {
    let idValue = parent?.id?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) }
    )
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionHideTooltip(
      id: idNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionHideTooltipTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionHideTooltip> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var idValue: DeserializationResult<Expression<String>> = parent?.id?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "id":
        idValue = deserialize(__dictValue).merged(with: idValue)
      case parent?.id?.link:
        idValue = idValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    var errors = mergeErrors(
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) }
    )
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionHideTooltip(
      id: idNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionHideTooltipTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionHideTooltipTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionHideTooltipTemplate(
      parent: nil,
      id: id ?? mergedParent.id
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionHideTooltipTemplate {
    return try mergedWithParent(templates: templates)
  }
}
