// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionDownloadTemplate: TemplateValue, Sendable {
  public static let type: String = "download"
  public let parent: String?
  public let onFailActions: Field<[DivActionTemplate]>?
  public let onSuccessActions: Field<[DivActionTemplate]>?
  public let url: Field<Expression<String>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      onFailActions: dictionary.getOptionalArray("on_fail_actions", templateToType: templateToType),
      onSuccessActions: dictionary.getOptionalArray("on_success_actions", templateToType: templateToType),
      url: dictionary.getOptionalExpressionField("url")
    )
  }

  init(
    parent: String?,
    onFailActions: Field<[DivActionTemplate]>? = nil,
    onSuccessActions: Field<[DivActionTemplate]>? = nil,
    url: Field<Expression<String>>? = nil
  ) {
    self.parent = parent
    self.onFailActions = onFailActions
    self.onSuccessActions = onSuccessActions
    self.url = url
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionDownloadTemplate?) -> DeserializationResult<DivActionDownload> {
    let onFailActionsValue = { parent?.onFailActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let onSuccessActionsValue = { parent?.onSuccessActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let urlValue = { parent?.url?.resolveValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      onFailActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_fail_actions", error: $0) },
      onSuccessActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_success_actions", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
    )
    if case .noValue = urlValue {
      errors.append(.requiredFieldIsMissing(field: "url"))
    }
    guard
      let urlNonNil = urlValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionDownload(
      onFailActions: { onFailActionsValue.value }(),
      onSuccessActions: { onSuccessActionsValue.value }(),
      url: { urlNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionDownloadTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionDownload> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var onFailActionsValue: DeserializationResult<[DivAction]> = .noValue
    var onSuccessActionsValue: DeserializationResult<[DivAction]> = .noValue
    var urlValue: DeserializationResult<Expression<String>> = { parent?.url?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "on_fail_actions" {
           onFailActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: onFailActionsValue)
          }
        }()
        _ = {
          if key == "on_success_actions" {
           onSuccessActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: onSuccessActionsValue)
          }
        }()
        _ = {
          if key == "url" {
           urlValue = deserialize(__dictValue).merged(with: urlValue)
          }
        }()
        _ = {
         if key == parent?.onFailActions?.link {
           onFailActionsValue = onFailActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.onSuccessActions?.link {
           onSuccessActionsValue = onSuccessActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.url?.link {
           urlValue = urlValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { onFailActionsValue = onFailActionsValue.merged(with: { parent.onFailActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { onSuccessActionsValue = onSuccessActionsValue.merged(with: { parent.onSuccessActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      onFailActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_fail_actions", error: $0) },
      onSuccessActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_success_actions", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
    )
    if case .noValue = urlValue {
      errors.append(.requiredFieldIsMissing(field: "url"))
    }
    guard
      let urlNonNil = urlValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionDownload(
      onFailActions: { onFailActionsValue.value }(),
      onSuccessActions: { onSuccessActionsValue.value }(),
      url: { urlNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionDownloadTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionDownloadTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionDownloadTemplate(
      parent: nil,
      onFailActions: onFailActions ?? mergedParent.onFailActions,
      onSuccessActions: onSuccessActions ?? mergedParent.onSuccessActions,
      url: url ?? mergedParent.url
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionDownloadTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionDownloadTemplate(
      parent: nil,
      onFailActions: merged.onFailActions?.tryResolveParent(templates: templates),
      onSuccessActions: merged.onSuccessActions?.tryResolveParent(templates: templates),
      url: merged.url
    )
  }
}
