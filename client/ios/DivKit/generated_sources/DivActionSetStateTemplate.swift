// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionSetStateTemplate: TemplateValue, Sendable {
  public static let type: String = "set_state"
  public let parent: String?
  public let stateId: Field<Expression<String>>?
  public let temporary: Field<Expression<Bool>>? // default value: true

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      stateId: dictionary.getOptionalExpressionField("state_id"),
      temporary: dictionary.getOptionalExpressionField("temporary")
    )
  }

  init(
    parent: String?,
    stateId: Field<Expression<String>>? = nil,
    temporary: Field<Expression<Bool>>? = nil
  ) {
    self.parent = parent
    self.stateId = stateId
    self.temporary = temporary
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionSetStateTemplate?) -> DeserializationResult<DivActionSetState> {
    let stateIdValue = { parent?.stateId?.resolveValue(context: context) ?? .noValue }()
    let temporaryValue = { parent?.temporary?.resolveOptionalValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      stateIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "state_id", error: $0) },
      temporaryValue.errorsOrWarnings?.map { .nestedObjectError(field: "temporary", error: $0) }
    )
    if case .noValue = stateIdValue {
      errors.append(.requiredFieldIsMissing(field: "state_id"))
    }
    guard
      let stateIdNonNil = stateIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionSetState(
      stateId: { stateIdNonNil }(),
      temporary: { temporaryValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionSetStateTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionSetState> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var stateIdValue: DeserializationResult<Expression<String>> = { parent?.stateId?.value() ?? .noValue }()
    var temporaryValue: DeserializationResult<Expression<Bool>> = { parent?.temporary?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "state_id" {
           stateIdValue = deserialize(__dictValue).merged(with: stateIdValue)
          }
        }()
        _ = {
          if key == "temporary" {
           temporaryValue = deserialize(__dictValue).merged(with: temporaryValue)
          }
        }()
        _ = {
         if key == parent?.stateId?.link {
           stateIdValue = stateIdValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.temporary?.link {
           temporaryValue = temporaryValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    var errors = mergeErrors(
      stateIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "state_id", error: $0) },
      temporaryValue.errorsOrWarnings?.map { .nestedObjectError(field: "temporary", error: $0) }
    )
    if case .noValue = stateIdValue {
      errors.append(.requiredFieldIsMissing(field: "state_id"))
    }
    guard
      let stateIdNonNil = stateIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionSetState(
      stateId: { stateIdNonNil }(),
      temporary: { temporaryValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionSetStateTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionSetStateTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionSetStateTemplate(
      parent: nil,
      stateId: stateId ?? mergedParent.stateId,
      temporary: temporary ?? mergedParent.temporary
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionSetStateTemplate {
    return try mergedWithParent(templates: templates)
  }
}
