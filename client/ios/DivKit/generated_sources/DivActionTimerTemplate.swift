// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionTimerTemplate: TemplateValue, Sendable {
  public typealias Action = DivActionTimer.Action

  public static let type: String = "timer"
  public let parent: String?
  public let action: Field<Expression<Action>>?
  public let id: Field<Expression<String>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      action: dictionary.getOptionalExpressionField("action"),
      id: dictionary.getOptionalExpressionField("id")
    )
  }

  init(
    parent: String?,
    action: Field<Expression<Action>>? = nil,
    id: Field<Expression<String>>? = nil
  ) {
    self.parent = parent
    self.action = action
    self.id = id
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionTimerTemplate?) -> DeserializationResult<DivActionTimer> {
    let actionValue = { parent?.action?.resolveValue(context: context) ?? .noValue }()
    let idValue = { parent?.id?.resolveValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) }
    )
    if case .noValue = actionValue {
      errors.append(.requiredFieldIsMissing(field: "action"))
    }
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let actionNonNil = actionValue.value,
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionTimer(
      action: { actionNonNil }(),
      id: { idNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionTimerTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionTimer> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var actionValue: DeserializationResult<Expression<DivActionTimer.Action>> = { parent?.action?.value() ?? .noValue }()
    var idValue: DeserializationResult<Expression<String>> = { parent?.id?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "action" {
           actionValue = deserialize(__dictValue).merged(with: actionValue)
          }
        }()
        _ = {
          if key == "id" {
           idValue = deserialize(__dictValue).merged(with: idValue)
          }
        }()
        _ = {
         if key == parent?.action?.link {
           actionValue = actionValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.id?.link {
           idValue = idValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    var errors = mergeErrors(
      actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) }
    )
    if case .noValue = actionValue {
      errors.append(.requiredFieldIsMissing(field: "action"))
    }
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let actionNonNil = actionValue.value,
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionTimer(
      action: { actionNonNil }(),
      id: { idNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionTimerTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionTimerTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionTimerTemplate(
      parent: nil,
      action: action ?? mergedParent.action,
      id: id ?? mergedParent.id
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionTimerTemplate {
    return try mergedWithParent(templates: templates)
  }
}
