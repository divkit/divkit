// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionAnimatorStopTemplate: TemplateValue {
  public static let type: String = "animator_stop"
  public let parent: String?
  public let animatorId: Field<String>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      animatorId: dictionary.getOptionalField("animator_id")
    )
  }

  init(
    parent: String?,
    animatorId: Field<String>? = nil
  ) {
    self.parent = parent
    self.animatorId = animatorId
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionAnimatorStopTemplate?) -> DeserializationResult<DivActionAnimatorStop> {
    let animatorIdValue = parent?.animatorId?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      animatorIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "animator_id", error: $0) }
    )
    if case .noValue = animatorIdValue {
      errors.append(.requiredFieldIsMissing(field: "animator_id"))
    }
    guard
      let animatorIdNonNil = animatorIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionAnimatorStop(
      animatorId: animatorIdNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionAnimatorStopTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionAnimatorStop> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var animatorIdValue: DeserializationResult<String> = parent?.animatorId?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "animator_id":
        animatorIdValue = deserialize(__dictValue).merged(with: animatorIdValue)
      case parent?.animatorId?.link:
        animatorIdValue = animatorIdValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    var errors = mergeErrors(
      animatorIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "animator_id", error: $0) }
    )
    if case .noValue = animatorIdValue {
      errors.append(.requiredFieldIsMissing(field: "animator_id"))
    }
    guard
      let animatorIdNonNil = animatorIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionAnimatorStop(
      animatorId: animatorIdNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionAnimatorStopTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionAnimatorStopTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionAnimatorStopTemplate(
      parent: nil,
      animatorId: animatorId ?? mergedParent.animatorId
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionAnimatorStopTemplate {
    return try mergedWithParent(templates: templates)
  }
}
