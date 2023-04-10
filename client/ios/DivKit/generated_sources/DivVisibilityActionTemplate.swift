// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivVisibilityActionTemplate: TemplateValue {
  public let downloadCallbacks: Field<DivDownloadCallbacksTemplate>?
  public let logId: Field<String>? // at least 1 char
  public let logLimit: Field<Expression<Int>>? // constraint: number >= 0; default value: 1
  public let payload: Field<[String: Any]>?
  public let referer: Field<Expression<URL>>?
  public let url: Field<Expression<URL>>?
  public let visibilityDuration: Field<Expression<Int>>? // constraint: number >= 0; default value: 800
  public let visibilityPercentage: Field<Expression<Int>>? // constraint: number > 0 && number <= 100; default value: 50

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        downloadCallbacks: try dictionary.getOptionalField("download_callbacks", templateToType: templateToType),
        logId: try dictionary.getOptionalField("log_id"),
        logLimit: try dictionary.getOptionalExpressionField("log_limit"),
        payload: try dictionary.getOptionalField("payload"),
        referer: try dictionary.getOptionalExpressionField("referer", transform: URL.init(string:)),
        url: try dictionary.getOptionalExpressionField("url", transform: URL.init(string:)),
        visibilityDuration: try dictionary.getOptionalExpressionField("visibility_duration"),
        visibilityPercentage: try dictionary.getOptionalExpressionField("visibility_percentage")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-visibility-action_template." + field, representation: representation)
    }
  }

  init(
    downloadCallbacks: Field<DivDownloadCallbacksTemplate>? = nil,
    logId: Field<String>? = nil,
    logLimit: Field<Expression<Int>>? = nil,
    payload: Field<[String: Any]>? = nil,
    referer: Field<Expression<URL>>? = nil,
    url: Field<Expression<URL>>? = nil,
    visibilityDuration: Field<Expression<Int>>? = nil,
    visibilityPercentage: Field<Expression<Int>>? = nil
  ) {
    self.downloadCallbacks = downloadCallbacks
    self.logId = logId
    self.logLimit = logLimit
    self.payload = payload
    self.referer = referer
    self.url = url
    self.visibilityDuration = visibilityDuration
    self.visibilityPercentage = visibilityPercentage
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivVisibilityActionTemplate?) -> DeserializationResult<DivVisibilityAction> {
    let downloadCallbacksValue = parent?.downloadCallbacks?.resolveOptionalValue(context: context, validator: ResolvedValue.downloadCallbacksValidator, useOnlyLinks: true) ?? .noValue
    let logIdValue = parent?.logId?.resolveValue(context: context, validator: ResolvedValue.logIdValidator) ?? .noValue
    let logLimitValue = parent?.logLimit?.resolveOptionalValue(context: context, validator: ResolvedValue.logLimitValidator) ?? .noValue
    let payloadValue = parent?.payload?.resolveOptionalValue(context: context, validator: ResolvedValue.payloadValidator) ?? .noValue
    let refererValue = parent?.referer?.resolveOptionalValue(context: context, transform: URL.init(string:), validator: ResolvedValue.refererValidator) ?? .noValue
    let urlValue = parent?.url?.resolveOptionalValue(context: context, transform: URL.init(string:), validator: ResolvedValue.urlValidator) ?? .noValue
    let visibilityDurationValue = parent?.visibilityDuration?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityDurationValidator) ?? .noValue
    let visibilityPercentageValue = parent?.visibilityPercentage?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityPercentageValidator) ?? .noValue
    var errors = mergeErrors(
      downloadCallbacksValue.errorsOrWarnings?.map { .nestedObjectError(field: "download_callbacks", error: $0) },
      logIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_id", error: $0) },
      logLimitValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_limit", error: $0) },
      payloadValue.errorsOrWarnings?.map { .nestedObjectError(field: "payload", error: $0) },
      refererValue.errorsOrWarnings?.map { .nestedObjectError(field: "referer", error: $0) },
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
      logId: logIdNonNil,
      logLimit: logLimitValue.value,
      payload: payloadValue.value,
      referer: refererValue.value,
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
    var logIdValue: DeserializationResult<String> = parent?.logId?.value(validatedBy: ResolvedValue.logIdValidator) ?? .noValue
    var logLimitValue: DeserializationResult<Expression<Int>> = parent?.logLimit?.value() ?? .noValue
    var payloadValue: DeserializationResult<[String: Any]> = parent?.payload?.value(validatedBy: ResolvedValue.payloadValidator) ?? .noValue
    var refererValue: DeserializationResult<Expression<URL>> = parent?.referer?.value() ?? .noValue
    var urlValue: DeserializationResult<Expression<URL>> = parent?.url?.value() ?? .noValue
    var visibilityDurationValue: DeserializationResult<Expression<Int>> = parent?.visibilityDuration?.value() ?? .noValue
    var visibilityPercentageValue: DeserializationResult<Expression<Int>> = parent?.visibilityPercentage?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "download_callbacks":
        downloadCallbacksValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.downloadCallbacksValidator, type: DivDownloadCallbacksTemplate.self).merged(with: downloadCallbacksValue)
      case "log_id":
        logIdValue = deserialize(__dictValue, validator: ResolvedValue.logIdValidator).merged(with: logIdValue)
      case "log_limit":
        logLimitValue = deserialize(__dictValue, validator: ResolvedValue.logLimitValidator).merged(with: logLimitValue)
      case "payload":
        payloadValue = deserialize(__dictValue, validator: ResolvedValue.payloadValidator).merged(with: payloadValue)
      case "referer":
        refererValue = deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.refererValidator).merged(with: refererValue)
      case "url":
        urlValue = deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.urlValidator).merged(with: urlValue)
      case "visibility_duration":
        visibilityDurationValue = deserialize(__dictValue, validator: ResolvedValue.visibilityDurationValidator).merged(with: visibilityDurationValue)
      case "visibility_percentage":
        visibilityPercentageValue = deserialize(__dictValue, validator: ResolvedValue.visibilityPercentageValidator).merged(with: visibilityPercentageValue)
      case parent?.downloadCallbacks?.link:
        downloadCallbacksValue = downloadCallbacksValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.downloadCallbacksValidator, type: DivDownloadCallbacksTemplate.self))
      case parent?.logId?.link:
        logIdValue = logIdValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.logIdValidator))
      case parent?.logLimit?.link:
        logLimitValue = logLimitValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.logLimitValidator))
      case parent?.payload?.link:
        payloadValue = payloadValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.payloadValidator))
      case parent?.referer?.link:
        refererValue = refererValue.merged(with: deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.refererValidator))
      case parent?.url?.link:
        urlValue = urlValue.merged(with: deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.urlValidator))
      case parent?.visibilityDuration?.link:
        visibilityDurationValue = visibilityDurationValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.visibilityDurationValidator))
      case parent?.visibilityPercentage?.link:
        visibilityPercentageValue = visibilityPercentageValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.visibilityPercentageValidator))
      default: break
      }
    }
    if let parent = parent {
      downloadCallbacksValue = downloadCallbacksValue.merged(with: parent.downloadCallbacks?.resolveOptionalValue(context: context, validator: ResolvedValue.downloadCallbacksValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      downloadCallbacksValue.errorsOrWarnings?.map { .nestedObjectError(field: "download_callbacks", error: $0) },
      logIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_id", error: $0) },
      logLimitValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_limit", error: $0) },
      payloadValue.errorsOrWarnings?.map { .nestedObjectError(field: "payload", error: $0) },
      refererValue.errorsOrWarnings?.map { .nestedObjectError(field: "referer", error: $0) },
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
      logId: logIdNonNil,
      logLimit: logLimitValue.value,
      payload: payloadValue.value,
      referer: refererValue.value,
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
      logId: merged.logId,
      logLimit: merged.logLimit,
      payload: merged.payload,
      referer: merged.referer,
      url: merged.url,
      visibilityDuration: merged.visibilityDuration,
      visibilityPercentage: merged.visibilityPercentage
    )
  }
}
