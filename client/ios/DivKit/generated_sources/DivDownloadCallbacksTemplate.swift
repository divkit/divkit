// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivDownloadCallbacksTemplate: TemplateValue, TemplateDeserializable {
  public let onFailActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let onSuccessActions: Field<[DivActionTemplate]>? // at least 1 elements

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    self.init(
      onFailActions: try dictionary.getOptionalArray("on_fail_actions", templateToType: templateToType),
      onSuccessActions: try dictionary.getOptionalArray("on_success_actions", templateToType: templateToType)
    )
  }

  init(
    onFailActions: Field<[DivActionTemplate]>? = nil,
    onSuccessActions: Field<[DivActionTemplate]>? = nil
  ) {
    self.onFailActions = onFailActions
    self.onSuccessActions = onSuccessActions
  }

  private static func resolveOnlyLinks(context: Context, parent: DivDownloadCallbacksTemplate?) -> DeserializationResult<DivDownloadCallbacks> {
    let onFailActionsValue = parent?.onFailActions?.resolveOptionalValue(context: context, validator: ResolvedValue.onFailActionsValidator, useOnlyLinks: true) ?? .noValue
    let onSuccessActionsValue = parent?.onSuccessActions?.resolveOptionalValue(context: context, validator: ResolvedValue.onSuccessActionsValidator, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      onFailActionsValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "on_fail_actions", level: .warning)) },
      onSuccessActionsValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "on_success_actions", level: .warning)) }
    )
    let result = DivDownloadCallbacks(
      onFailActions: onFailActionsValue.value,
      onSuccessActions: onSuccessActionsValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivDownloadCallbacksTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivDownloadCallbacks> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var onFailActionsValue: DeserializationResult<[DivAction]> = .noValue
    var onSuccessActionsValue: DeserializationResult<[DivAction]> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "on_fail_actions":
        onFailActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.onFailActionsValidator, type: DivActionTemplate.self).merged(with: onFailActionsValue)
      case "on_success_actions":
        onSuccessActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.onSuccessActionsValidator, type: DivActionTemplate.self).merged(with: onSuccessActionsValue)
      case parent?.onFailActions?.link:
        onFailActionsValue = onFailActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.onFailActionsValidator, type: DivActionTemplate.self))
      case parent?.onSuccessActions?.link:
        onSuccessActionsValue = onSuccessActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.onSuccessActionsValidator, type: DivActionTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      onFailActionsValue = onFailActionsValue.merged(with: parent.onFailActions?.resolveOptionalValue(context: context, validator: ResolvedValue.onFailActionsValidator, useOnlyLinks: true))
      onSuccessActionsValue = onSuccessActionsValue.merged(with: parent.onSuccessActions?.resolveOptionalValue(context: context, validator: ResolvedValue.onSuccessActionsValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      onFailActionsValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "on_fail_actions", level: .warning)) },
      onSuccessActionsValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "on_success_actions", level: .warning)) }
    )
    let result = DivDownloadCallbacks(
      onFailActions: onFailActionsValue.value,
      onSuccessActions: onSuccessActionsValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivDownloadCallbacksTemplate {
    return self
  }

  public func resolveParent(templates: Templates) throws -> DivDownloadCallbacksTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivDownloadCallbacksTemplate(
      onFailActions: merged.onFailActions?.tryResolveParent(templates: templates),
      onSuccessActions: merged.onSuccessActions?.tryResolveParent(templates: templates)
    )
  }
}
