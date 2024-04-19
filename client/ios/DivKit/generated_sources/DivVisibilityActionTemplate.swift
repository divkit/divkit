// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivVisibilityActionTemplate: TemplateValue {
  public let downloadCallbacks: Field<DivDownloadCallbacksTemplate>?
  public let isEnabled: Field<Expression<Bool>>? // default value: true
  public let logId: Field<Expression<String>>?
  public let logLimit: Field<Expression<Int>>? // constraint: number >= 0; default value: 1
  public let payload: Field<[String: Any]>?
  public let referer: Field<Expression<URL>>?
  public let typed: Field<DivActionTypedTemplate>?
  public let url: Field<Expression<URL>>?
  public let visibilityDuration: Field<Expression<Int>>? // constraint: number >= 0; default value: 800
  public let visibilityPercentage: Field<Expression<Int>>? // constraint: number > 0 && number <= 100; default value: 50

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      downloadCallbacks: dictionary.getOptionalField("download_callbacks", templateToType: templateToType),
      isEnabled: dictionary.getOptionalExpressionField("is_enabled"),
      logId: dictionary.getOptionalExpressionField("log_id"),
      logLimit: dictionary.getOptionalExpressionField("log_limit"),
      payload: dictionary.getOptionalField("payload"),
      referer: dictionary.getOptionalExpressionField("referer", transform: URL.init(string:)),
      typed: dictionary.getOptionalField("typed", templateToType: templateToType),
      url: dictionary.getOptionalExpressionField("url", transform: URL.init(string:)),
      visibilityDuration: dictionary.getOptionalExpressionField("visibility_duration"),
      visibilityPercentage: dictionary.getOptionalExpressionField("visibility_percentage")
    )
  }

  init(
    downloadCallbacks: Field<DivDownloadCallbacksTemplate>? = nil,
    isEnabled: Field<Expression<Bool>>? = nil,
    logId: Field<Expression<String>>? = nil,
    logLimit: Field<Expression<Int>>? = nil,
    payload: Field<[String: Any]>? = nil,
    referer: Field<Expression<URL>>? = nil,
    typed: Field<DivActionTypedTemplate>? = nil,
    url: Field<Expression<URL>>? = nil,
    visibilityDuration: Field<Expression<Int>>? = nil,
    visibilityPercentage: Field<Expression<Int>>? = nil
  ) {
    self.downloadCallbacks = downloadCallbacks
    self.isEnabled = isEnabled
    self.logId = logId
    self.logLimit = logLimit
    self.payload = payload
    self.referer = referer
    self.typed = typed
    self.url = url
    self.visibilityDuration = visibilityDuration
    self.visibilityPercentage = visibilityPercentage
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivVisibilityActionTemplate?) -> DeserializationResult<DivVisibilityAction> {
    let downloadCallbacksValue = parent?.downloadCallbacks?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let isEnabledValue = parent?.isEnabled?.resolveOptionalValue(context: context) ?? .noValue
    let logIdValue = parent?.logId?.resolveValue(context: context) ?? .noValue
    let logLimitValue = parent?.logLimit?.resolveOptionalValue(context: context, validator: ResolvedValue.logLimitValidator) ?? .noValue
    let payloadValue = parent?.payload?.resolveOptionalValue(context: context) ?? .noValue
    let refererValue = parent?.referer?.resolveOptionalValue(context: context, transform: URL.init(string:)) ?? .noValue
    let typedValue = parent?.typed?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let urlValue = parent?.url?.resolveOptionalValue(context: context, transform: URL.init(string:)) ?? .noValue
    let visibilityDurationValue = parent?.visibilityDuration?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityDurationValidator) ?? .noValue
    let visibilityPercentageValue = parent?.visibilityPercentage?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityPercentageValidator) ?? .noValue
    var errors = mergeErrors(
      downloadCallbacksValue.errorsOrWarnings?.map { .nestedObjectError(field: "download_callbacks", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) },
      logIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_id", error: $0) },
      logLimitValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_limit", error: $0) },
      payloadValue.errorsOrWarnings?.map { .nestedObjectError(field: "payload", error: $0) },
      refererValue.errorsOrWarnings?.map { .nestedObjectError(field: "referer", error: $0) },
      typedValue.errorsOrWarnings?.map { .nestedObjectError(field: "typed", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) },
      visibilityDurationValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_duration", error: $0) },
      visibilityPercentageValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_percentage", error: $0) }
    )
    if case .noValue = logIdValue {
      errors.append(.requiredFieldIsMissing(field: "log_id"))
    }
    guard
      let logIdNonNil = logIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivVisibilityAction(
      downloadCallbacks: downloadCallbacksValue.value,
      isEnabled: isEnabledValue.value,
      logId: logIdNonNil,
      logLimit: logLimitValue.value,
      payload: payloadValue.value,
      referer: refererValue.value,
      typed: typedValue.value,
      url: urlValue.value,
      visibilityDuration: visibilityDurationValue.value,
      visibilityPercentage: visibilityPercentageValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivVisibilityActionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivVisibilityAction> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var downloadCallbacksValue: DeserializationResult<DivDownloadCallbacks> = .noValue
    var isEnabledValue: DeserializationResult<Expression<Bool>> = parent?.isEnabled?.value() ?? .noValue
    var logIdValue: DeserializationResult<Expression<String>> = parent?.logId?.value() ?? .noValue
    var logLimitValue: DeserializationResult<Expression<Int>> = parent?.logLimit?.value() ?? .noValue
    var payloadValue: DeserializationResult<[String: Any]> = parent?.payload?.value() ?? .noValue
    var refererValue: DeserializationResult<Expression<URL>> = parent?.referer?.value() ?? .noValue
    var typedValue: DeserializationResult<DivActionTyped> = .noValue
    var urlValue: DeserializationResult<Expression<URL>> = parent?.url?.value() ?? .noValue
    var visibilityDurationValue: DeserializationResult<Expression<Int>> = parent?.visibilityDuration?.value() ?? .noValue
    var visibilityPercentageValue: DeserializationResult<Expression<Int>> = parent?.visibilityPercentage?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "download_callbacks":
        downloadCallbacksValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDownloadCallbacksTemplate.self).merged(with: downloadCallbacksValue)
      case "is_enabled":
        isEnabledValue = deserialize(__dictValue).merged(with: isEnabledValue)
      case "log_id":
        logIdValue = deserialize(__dictValue).merged(with: logIdValue)
      case "log_limit":
        logLimitValue = deserialize(__dictValue, validator: ResolvedValue.logLimitValidator).merged(with: logLimitValue)
      case "payload":
        payloadValue = deserialize(__dictValue).merged(with: payloadValue)
      case "referer":
        refererValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: refererValue)
      case "typed":
        typedValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTypedTemplate.self).merged(with: typedValue)
      case "url":
        urlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: urlValue)
      case "visibility_duration":
        visibilityDurationValue = deserialize(__dictValue, validator: ResolvedValue.visibilityDurationValidator).merged(with: visibilityDurationValue)
      case "visibility_percentage":
        visibilityPercentageValue = deserialize(__dictValue, validator: ResolvedValue.visibilityPercentageValidator).merged(with: visibilityPercentageValue)
      case parent?.downloadCallbacks?.link:
        downloadCallbacksValue = downloadCallbacksValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDownloadCallbacksTemplate.self) })
      case parent?.isEnabled?.link:
        isEnabledValue = isEnabledValue.merged(with: { deserialize(__dictValue) })
      case parent?.logId?.link:
        logIdValue = logIdValue.merged(with: { deserialize(__dictValue) })
      case parent?.logLimit?.link:
        logLimitValue = logLimitValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.logLimitValidator) })
      case parent?.payload?.link:
        payloadValue = payloadValue.merged(with: { deserialize(__dictValue) })
      case parent?.referer?.link:
        refererValue = refererValue.merged(with: { deserialize(__dictValue, transform: URL.init(string:)) })
      case parent?.typed?.link:
        typedValue = typedValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTypedTemplate.self) })
      case parent?.url?.link:
        urlValue = urlValue.merged(with: { deserialize(__dictValue, transform: URL.init(string:)) })
      case parent?.visibilityDuration?.link:
        visibilityDurationValue = visibilityDurationValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.visibilityDurationValidator) })
      case parent?.visibilityPercentage?.link:
        visibilityPercentageValue = visibilityPercentageValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.visibilityPercentageValidator) })
      default: break
      }
    }
    if let parent = parent {
      downloadCallbacksValue = downloadCallbacksValue.merged(with: { parent.downloadCallbacks?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      typedValue = typedValue.merged(with: { parent.typed?.resolveOptionalValue(context: context, useOnlyLinks: true) })
    }
    var errors = mergeErrors(
      downloadCallbacksValue.errorsOrWarnings?.map { .nestedObjectError(field: "download_callbacks", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) },
      logIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_id", error: $0) },
      logLimitValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_limit", error: $0) },
      payloadValue.errorsOrWarnings?.map { .nestedObjectError(field: "payload", error: $0) },
      refererValue.errorsOrWarnings?.map { .nestedObjectError(field: "referer", error: $0) },
      typedValue.errorsOrWarnings?.map { .nestedObjectError(field: "typed", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) },
      visibilityDurationValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_duration", error: $0) },
      visibilityPercentageValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_percentage", error: $0) }
    )
    if case .noValue = logIdValue {
      errors.append(.requiredFieldIsMissing(field: "log_id"))
    }
    guard
      let logIdNonNil = logIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivVisibilityAction(
      downloadCallbacks: downloadCallbacksValue.value,
      isEnabled: isEnabledValue.value,
      logId: logIdNonNil,
      logLimit: logLimitValue.value,
      payload: payloadValue.value,
      referer: refererValue.value,
      typed: typedValue.value,
      url: urlValue.value,
      visibilityDuration: visibilityDurationValue.value,
      visibilityPercentage: visibilityPercentageValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivVisibilityActionTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivVisibilityActionTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivVisibilityActionTemplate(
      downloadCallbacks: merged.downloadCallbacks?.tryResolveParent(templates: templates),
      isEnabled: merged.isEnabled,
      logId: merged.logId,
      logLimit: merged.logLimit,
      payload: merged.payload,
      referer: merged.referer,
      typed: merged.typed?.tryResolveParent(templates: templates),
      url: merged.url,
      visibilityDuration: merged.visibilityDuration,
      visibilityPercentage: merged.visibilityPercentage
    )
  }
}
