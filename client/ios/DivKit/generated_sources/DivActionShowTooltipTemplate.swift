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
    let idValue = { parent?.id?.resolveValue(context: context) ?? .noValue }()
    let multipleValue = { parent?.multiple?.resolveOptionalValue(context: context) ?? .noValue }()
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
      id: { idNonNil }(),
      multiple: { multipleValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionShowTooltipTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionShowTooltip> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var idValue: DeserializationResult<Expression<String>> = { parent?.id?.value() ?? .noValue }()
    var multipleValue: DeserializationResult<Expression<Bool>> = { parent?.multiple?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "id" {
           idValue = deserialize(__dictValue).merged(with: idValue)
          }
        }()
        _ = {
          if key == "multiple" {
           multipleValue = deserialize(__dictValue).merged(with: multipleValue)
          }
        }()
        _ = {
         if key == parent?.id?.link {
           idValue = idValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.multiple?.link {
           multipleValue = multipleValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
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
      id: { idNonNil }(),
      multiple: { multipleValue.value }()
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
