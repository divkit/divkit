// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivDisappearActionTemplate: TemplateValue, @unchecked Sendable {
  public let disappearDuration: Field<Expression<Int>>? // constraint: number >= 0; default value: 800
  public let downloadCallbacks: Field<DivDownloadCallbacksTemplate>?
  public let isEnabled: Field<Expression<Bool>>? // default value: true
  public let logId: Field<Expression<String>>?
  public let logLimit: Field<Expression<Int>>? // constraint: number >= 0; default value: 1
  public let payload: Field<[String: Any]>?
  public let referer: Field<Expression<URL>>?
  public let scopeId: Field<Expression<String>>?
  public let typed: Field<DivActionTypedTemplate>?
  public let url: Field<Expression<URL>>?
  public let visibilityPercentage: Field<Expression<Int>>? // constraint: number >= 0 && number < 100; default value: 0

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      disappearDuration: dictionary.getOptionalExpressionField("disappear_duration"),
      downloadCallbacks: dictionary.getOptionalField("download_callbacks", templateToType: templateToType),
      isEnabled: dictionary.getOptionalExpressionField("is_enabled"),
      logId: dictionary.getOptionalExpressionField("log_id"),
      logLimit: dictionary.getOptionalExpressionField("log_limit"),
      payload: dictionary.getOptionalField("payload"),
      referer: dictionary.getOptionalExpressionField("referer", transform: URL.makeFromNonEncodedString),
      scopeId: dictionary.getOptionalExpressionField("scope_id"),
      typed: dictionary.getOptionalField("typed", templateToType: templateToType),
      url: dictionary.getOptionalExpressionField("url", transform: URL.makeFromNonEncodedString),
      visibilityPercentage: dictionary.getOptionalExpressionField("visibility_percentage")
    )
  }

  init(
    disappearDuration: Field<Expression<Int>>? = nil,
    downloadCallbacks: Field<DivDownloadCallbacksTemplate>? = nil,
    isEnabled: Field<Expression<Bool>>? = nil,
    logId: Field<Expression<String>>? = nil,
    logLimit: Field<Expression<Int>>? = nil,
    payload: Field<[String: Any]>? = nil,
    referer: Field<Expression<URL>>? = nil,
    scopeId: Field<Expression<String>>? = nil,
    typed: Field<DivActionTypedTemplate>? = nil,
    url: Field<Expression<URL>>? = nil,
    visibilityPercentage: Field<Expression<Int>>? = nil
  ) {
    self.disappearDuration = disappearDuration
    self.downloadCallbacks = downloadCallbacks
    self.isEnabled = isEnabled
    self.logId = logId
    self.logLimit = logLimit
    self.payload = payload
    self.referer = referer
    self.scopeId = scopeId
    self.typed = typed
    self.url = url
    self.visibilityPercentage = visibilityPercentage
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivDisappearActionTemplate?) -> DeserializationResult<DivDisappearAction> {
    let disappearDurationValue = { parent?.disappearDuration?.resolveOptionalValue(context: context, validator: ResolvedValue.disappearDurationValidator) ?? .noValue }()
    let downloadCallbacksValue = { parent?.downloadCallbacks?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let isEnabledValue = { parent?.isEnabled?.resolveOptionalValue(context: context) ?? .noValue }()
    let logIdValue = { parent?.logId?.resolveOptionalValue(context: context) ?? .noValue }()
    let logLimitValue = { parent?.logLimit?.resolveOptionalValue(context: context, validator: ResolvedValue.logLimitValidator) ?? .noValue }()
    let payloadValue = { parent?.payload?.resolveOptionalValue(context: context) ?? .noValue }()
    let refererValue = { parent?.referer?.resolveOptionalValue(context: context, transform: URL.makeFromNonEncodedString) ?? .noValue }()
    let scopeIdValue = { parent?.scopeId?.resolveOptionalValue(context: context) ?? .noValue }()
    let typedValue = { parent?.typed?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let urlValue = { parent?.url?.resolveOptionalValue(context: context, transform: URL.makeFromNonEncodedString) ?? .noValue }()
    let visibilityPercentageValue = { parent?.visibilityPercentage?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityPercentageValidator) ?? .noValue }()
    let errors = mergeErrors(
      disappearDurationValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_duration", error: $0) },
      downloadCallbacksValue.errorsOrWarnings?.map { .nestedObjectError(field: "download_callbacks", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) },
      logIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_id", error: $0) },
      logLimitValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_limit", error: $0) },
      payloadValue.errorsOrWarnings?.map { .nestedObjectError(field: "payload", error: $0) },
      refererValue.errorsOrWarnings?.map { .nestedObjectError(field: "referer", error: $0) },
      scopeIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "scope_id", error: $0) },
      typedValue.errorsOrWarnings?.map { .nestedObjectError(field: "typed", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) },
      visibilityPercentageValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_percentage", error: $0) }
    )
    let result = DivDisappearAction(
      disappearDuration: { disappearDurationValue.value }(),
      downloadCallbacks: { downloadCallbacksValue.value }(),
      isEnabled: { isEnabledValue.value }(),
      logId: { logIdValue.value }(),
      logLimit: { logLimitValue.value }(),
      payload: { payloadValue.value }(),
      referer: { refererValue.value }(),
      scopeId: { scopeIdValue.value }(),
      typed: { typedValue.value }(),
      url: { urlValue.value }(),
      visibilityPercentage: { visibilityPercentageValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivDisappearActionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivDisappearAction> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var disappearDurationValue: DeserializationResult<Expression<Int>> = { parent?.disappearDuration?.value() ?? .noValue }()
    var downloadCallbacksValue: DeserializationResult<DivDownloadCallbacks> = .noValue
    var isEnabledValue: DeserializationResult<Expression<Bool>> = { parent?.isEnabled?.value() ?? .noValue }()
    var logIdValue: DeserializationResult<Expression<String>> = { parent?.logId?.value() ?? .noValue }()
    var logLimitValue: DeserializationResult<Expression<Int>> = { parent?.logLimit?.value() ?? .noValue }()
    var payloadValue: DeserializationResult<[String: Any]> = { parent?.payload?.value() ?? .noValue }()
    var refererValue: DeserializationResult<Expression<URL>> = { parent?.referer?.value() ?? .noValue }()
    var scopeIdValue: DeserializationResult<Expression<String>> = { parent?.scopeId?.value() ?? .noValue }()
    var typedValue: DeserializationResult<DivActionTyped> = .noValue
    var urlValue: DeserializationResult<Expression<URL>> = { parent?.url?.value() ?? .noValue }()
    var visibilityPercentageValue: DeserializationResult<Expression<Int>> = { parent?.visibilityPercentage?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "disappear_duration" {
           disappearDurationValue = deserialize(__dictValue, validator: ResolvedValue.disappearDurationValidator).merged(with: disappearDurationValue)
          }
        }()
        _ = {
          if key == "download_callbacks" {
           downloadCallbacksValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDownloadCallbacksTemplate.self).merged(with: downloadCallbacksValue)
          }
        }()
        _ = {
          if key == "is_enabled" {
           isEnabledValue = deserialize(__dictValue).merged(with: isEnabledValue)
          }
        }()
        _ = {
          if key == "log_id" {
           logIdValue = deserialize(__dictValue).merged(with: logIdValue)
          }
        }()
        _ = {
          if key == "log_limit" {
           logLimitValue = deserialize(__dictValue, validator: ResolvedValue.logLimitValidator).merged(with: logLimitValue)
          }
        }()
        _ = {
          if key == "payload" {
           payloadValue = deserialize(__dictValue).merged(with: payloadValue)
          }
        }()
        _ = {
          if key == "referer" {
           refererValue = deserialize(__dictValue, transform: URL.makeFromNonEncodedString).merged(with: refererValue)
          }
        }()
        _ = {
          if key == "scope_id" {
           scopeIdValue = deserialize(__dictValue).merged(with: scopeIdValue)
          }
        }()
        _ = {
          if key == "typed" {
           typedValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTypedTemplate.self).merged(with: typedValue)
          }
        }()
        _ = {
          if key == "url" {
           urlValue = deserialize(__dictValue, transform: URL.makeFromNonEncodedString).merged(with: urlValue)
          }
        }()
        _ = {
          if key == "visibility_percentage" {
           visibilityPercentageValue = deserialize(__dictValue, validator: ResolvedValue.visibilityPercentageValidator).merged(with: visibilityPercentageValue)
          }
        }()
        _ = {
         if key == parent?.disappearDuration?.link, context.templateData["disappear_duration"] == nil {
           disappearDurationValue = deserialize(__dictValue, validator: ResolvedValue.disappearDurationValidator).orFallback(disappearDurationValue)
          }
        }()
        _ = {
         if key == parent?.downloadCallbacks?.link, context.templateData["download_callbacks"] == nil {
           downloadCallbacksValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDownloadCallbacksTemplate.self).orFallback(downloadCallbacksValue)
          }
        }()
        _ = {
         if key == parent?.isEnabled?.link, context.templateData["is_enabled"] == nil {
           isEnabledValue = deserialize(__dictValue).orFallback(isEnabledValue)
          }
        }()
        _ = {
         if key == parent?.logId?.link, context.templateData["log_id"] == nil {
           logIdValue = deserialize(__dictValue).orFallback(logIdValue)
          }
        }()
        _ = {
         if key == parent?.logLimit?.link, context.templateData["log_limit"] == nil {
           logLimitValue = deserialize(__dictValue, validator: ResolvedValue.logLimitValidator).orFallback(logLimitValue)
          }
        }()
        _ = {
         if key == parent?.payload?.link, context.templateData["payload"] == nil {
           payloadValue = deserialize(__dictValue).orFallback(payloadValue)
          }
        }()
        _ = {
         if key == parent?.referer?.link, context.templateData["referer"] == nil {
           refererValue = deserialize(__dictValue, transform: URL.makeFromNonEncodedString).orFallback(refererValue)
          }
        }()
        _ = {
         if key == parent?.scopeId?.link, context.templateData["scope_id"] == nil {
           scopeIdValue = deserialize(__dictValue).orFallback(scopeIdValue)
          }
        }()
        _ = {
         if key == parent?.typed?.link, context.templateData["typed"] == nil {
           typedValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTypedTemplate.self).orFallback(typedValue)
          }
        }()
        _ = {
         if key == parent?.url?.link, context.templateData["url"] == nil {
           urlValue = deserialize(__dictValue, transform: URL.makeFromNonEncodedString).orFallback(urlValue)
          }
        }()
        _ = {
         if key == parent?.visibilityPercentage?.link, context.templateData["visibility_percentage"] == nil {
           visibilityPercentageValue = deserialize(__dictValue, validator: ResolvedValue.visibilityPercentageValidator).orFallback(visibilityPercentageValue)
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { downloadCallbacksValue = downloadCallbacksValue.merged(with: { parent.downloadCallbacks?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { typedValue = typedValue.merged(with: { parent.typed?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    let errors = mergeErrors(
      disappearDurationValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_duration", error: $0) },
      downloadCallbacksValue.errorsOrWarnings?.map { .nestedObjectError(field: "download_callbacks", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) },
      logIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_id", error: $0) },
      logLimitValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_limit", error: $0) },
      payloadValue.errorsOrWarnings?.map { .nestedObjectError(field: "payload", error: $0) },
      refererValue.errorsOrWarnings?.map { .nestedObjectError(field: "referer", error: $0) },
      scopeIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "scope_id", error: $0) },
      typedValue.errorsOrWarnings?.map { .nestedObjectError(field: "typed", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) },
      visibilityPercentageValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_percentage", error: $0) }
    )
    let result = DivDisappearAction(
      disappearDuration: { disappearDurationValue.value }(),
      downloadCallbacks: { downloadCallbacksValue.value }(),
      isEnabled: { isEnabledValue.value }(),
      logId: { logIdValue.value }(),
      logLimit: { logLimitValue.value }(),
      payload: { payloadValue.value }(),
      referer: { refererValue.value }(),
      scopeId: { scopeIdValue.value }(),
      typed: { typedValue.value }(),
      url: { urlValue.value }(),
      visibilityPercentage: { visibilityPercentageValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivDisappearActionTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivDisappearActionTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivDisappearActionTemplate(
      disappearDuration: merged.disappearDuration,
      downloadCallbacks: merged.downloadCallbacks?.tryResolveParent(templates: templates),
      isEnabled: merged.isEnabled,
      logId: merged.logId,
      logLimit: merged.logLimit,
      payload: merged.payload,
      referer: merged.referer,
      scopeId: merged.scopeId,
      typed: merged.typed?.tryResolveParent(templates: templates),
      url: merged.url,
      visibilityPercentage: merged.visibilityPercentage
    )
  }
}
