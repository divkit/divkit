// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionShowTooltipTemplate: TemplateValue {
  public static let type: String = "show_tooltip"
  public let parent: String?
  public let id: Field<Expression<String>>?
  public let multiple: Field<Expression<Bool>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      id: dictionary.getOptionalExpressionField("id"),
      multiple: dictionary.getOptionalExpressionField("multiple")
    )
  }

  init(
    parent: String?,
    id: Field<Expression<String>>? = nil,
    multiple: Field<Expression<Bool>>? = nil
  ) {
    self.parent = parent
    self.id = id
    self.multiple = multiple
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionShowTooltipTemplate?) -> DeserializationResult<DivActionShowTooltip> {
    let idValue = parent?.id?.resolveValue(context: context) ?? .noValue
    let multipleValue = parent?.multiple?.resolveOptionalValue(context: context) ?? .noValue
    var errors = mergeErrors(
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      multipleValue.errorsOrWarnings?.map { .nestedObjectError(field: "multiple", error: $0) }
    )
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionShowTooltip(
      id: idNonNil,
      multiple: multipleValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionShowTooltipTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionShowTooltip> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var idValue: DeserializationResult<Expression<String>> = parent?.id?.value() ?? .noValue
    var multipleValue: DeserializationResult<Expression<Bool>> = parent?.multiple?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "id":
        idValue = deserialize(__dictValue).merged(with: idValue)
      case "multiple":
        multipleValue = deserialize(__dictValue).merged(with: multipleValue)
      case parent?.id?.link:
        idValue = idValue.merged(with: { deserialize(__dictValue) })
      case parent?.multiple?.link:
        multipleValue = multipleValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    var errors = mergeErrors(
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      multipleValue.errorsOrWarnings?.map { .nestedObjectError(field: "multiple", error: $0) }
    )
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionShowTooltip(
      id: idNonNil,
      multiple: multipleValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionShowTooltipTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionShowTooltipTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionShowTooltipTemplate(
      parent: nil,
      id: id ?? mergedParent.id,
      multiple: multiple ?? mergedParent.multiple
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionShowTooltipTemplate {
    return try mergedWithParent(templates: templates)
  }
}
