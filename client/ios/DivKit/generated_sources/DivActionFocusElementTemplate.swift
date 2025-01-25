// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionFocusElementTemplate: TemplateValue, Sendable {
  public static let type: String = "focus_element"
  public let parent: String?
  public let elementId: Field<Expression<String>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      elementId: dictionary.getOptionalExpressionField("element_id")
    )
  }

  init(
    parent: String?,
    elementId: Field<Expression<String>>? = nil
  ) {
    self.parent = parent
    self.elementId = elementId
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionFocusElementTemplate?) -> DeserializationResult<DivActionFocusElement> {
    let elementIdValue = { parent?.elementId?.resolveValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      elementIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "element_id", error: $0) }
    )
    if case .noValue = elementIdValue {
      errors.append(.requiredFieldIsMissing(field: "element_id"))
    }
    guard
      let elementIdNonNil = elementIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionFocusElement(
      elementId: { elementIdNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionFocusElementTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionFocusElement> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var elementIdValue: DeserializationResult<Expression<String>> = { parent?.elementId?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "element_id" {
           elementIdValue = deserialize(__dictValue).merged(with: elementIdValue)
          }
        }()
        _ = {
         if key == parent?.elementId?.link {
           elementIdValue = elementIdValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    var errors = mergeErrors(
      elementIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "element_id", error: $0) }
    )
    if case .noValue = elementIdValue {
      errors.append(.requiredFieldIsMissing(field: "element_id"))
    }
    guard
      let elementIdNonNil = elementIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionFocusElement(
      elementId: { elementIdNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionFocusElementTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionFocusElementTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionFocusElementTemplate(
      parent: nil,
      elementId: elementId ?? mergedParent.elementId
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionFocusElementTemplate {
    return try mergedWithParent(templates: templates)
  }
}
