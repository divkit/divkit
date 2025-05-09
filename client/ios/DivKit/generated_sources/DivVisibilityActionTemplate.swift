// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivVisibilityActionTemplate: TemplateValue, @unchecked Sendable {
  public let downloadCallbacks: Field<DivDownloadCallbacksTemplate>?
  public let isEnabled: Field<Expression<Bool>>? // default value: true
  public let logId: Field<Expression<String>>?
  public let logLimit: Field<Expression<Int>>? // constraint: number >= 0; default value: 1
  public let payload: Field<[String: Any]>?
  public let referer: Field<Expression<URL>>?
  public let scopeId: Field<String>?
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
      referer: dictionary.getOptionalExpressionField("referer", transform: URL.init(stringToEncode:)),
      scopeId: dictionary.getOptionalField("scope_id"),
      typed: dictionary.getOptionalField("typed", templateToType: templateToType),
      url: dictionary.getOptionalExpressionField("url", transform: URL.init(stringToEncode:)),
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
    scopeId: Field<String>? = nil,
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
    self.scopeId = scopeId
    self.typed = typed
    self.url = url
    self.visibilityDuration = visibilityDuration
    self.visibilityPercentage = visibilityPercentage
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivVisibilityActionTemplate?) -> DeserializationResult<DivVisibilityAction> {
    let downloadCallbacksValue = { parent?.downloadCallbacks?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let isEnabledValue = { parent?.isEnabled?.resolveOptionalValue(context: context) ?? .noValue }()
    let logIdValue = { parent?.logId?.resolveValue(context: context) ?? .noValue }()
    let logLimitValue = { parent?.logLimit?.resolveOptionalValue(context: context, validator: ResolvedValue.logLimitValidator) ?? .noValue }()
    let payloadValue = { parent?.payload?.resolveOptionalValue(context: context) ?? .noValue }()
    let refererValue = { parent?.referer?.resolveOptionalValue(context: context, transform: URL.init(stringToEncode:)) ?? .noValue }()
    let scopeIdValue = { parent?.scopeId?.resolveOptionalValue(context: context) ?? .noValue }()
    let typedValue = { parent?.typed?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let urlValue = { parent?.url?.resolveOptionalValue(context: context, transform: URL.init(stringToEncode:)) ?? .noValue }()
    let visibilityDurationValue = { parent?.visibilityDuration?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityDurationValidator) ?? .noValue }()
    let visibilityPercentageValue = { parent?.visibilityPercentage?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityPercentageValidator) ?? .noValue }()
    var errors = mergeErrors(
      downloadCallbacksValue.errorsOrWarnings?.map { .nestedObjectError(field: "download_callbacks", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) },
      logIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_id", error: $0) },
      logLimitValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_limit", error: $0) },
      payloadValue.errorsOrWarnings?.map { .nestedObjectError(field: "payload", error: $0) },
      refererValue.errorsOrWarnings?.map { .nestedObjectError(field: "referer", error: $0) },
      scopeIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "scope_id", error: $0) },
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
      downloadCallbacks: { downloadCallbacksValue.value }(),
      isEnabled: { isEnabledValue.value }(),
      logId: { logIdNonNil }(),
      logLimit: { logLimitValue.value }(),
      payload: { payloadValue.value }(),
      referer: { refererValue.value }(),
      scopeId: { scopeIdValue.value }(),
      typed: { typedValue.value }(),
      url: { urlValue.value }(),
      visibilityDuration: { visibilityDurationValue.value }(),
      visibilityPercentage: { visibilityPercentageValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivVisibilityActionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivVisibilityAction> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var downloadCallbacksValue: DeserializationResult<DivDownloadCallbacks> = .noValue
    var isEnabledValue: DeserializationResult<Expression<Bool>> = { parent?.isEnabled?.value() ?? .noValue }()
    var logIdValue: DeserializationResult<Expression<String>> = { parent?.logId?.value() ?? .noValue }()
    var logLimitValue: DeserializationResult<Expression<Int>> = { parent?.logLimit?.value() ?? .noValue }()
    var payloadValue: DeserializationResult<[String: Any]> = { parent?.payload?.value() ?? .noValue }()
    var refererValue: DeserializationResult<Expression<URL>> = { parent?.referer?.value() ?? .noValue }()
    var scopeIdValue: DeserializationResult<String> = { parent?.scopeId?.value() ?? .noValue }()
    var typedValue: DeserializationResult<DivActionTyped> = .noValue
    var urlValue: DeserializationResult<Expression<URL>> = { parent?.url?.value() ?? .noValue }()
    var visibilityDurationValue: DeserializationResult<Expression<Int>> = { parent?.visibilityDuration?.value() ?? .noValue }()
    var visibilityPercentageValue: DeserializationResult<Expression<Int>> = { parent?.visibilityPercentage?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
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
           refererValue = deserialize(__dictValue, transform: URL.init(stringToEncode:)).merged(with: refererValue)
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
           urlValue = deserialize(__dictValue, transform: URL.init(stringToEncode:)).merged(with: urlValue)
          }
        }()
        _ = {
          if key == "visibility_duration" {
           visibilityDurationValue = deserialize(__dictValue, validator: ResolvedValue.visibilityDurationValidator).merged(with: visibilityDurationValue)
          }
        }()
        _ = {
          if key == "visibility_percentage" {
           visibilityPercentageValue = deserialize(__dictValue, validator: ResolvedValue.visibilityPercentageValidator).merged(with: visibilityPercentageValue)
          }
        }()
        _ = {
         if key == parent?.downloadCallbacks?.link {
           downloadCallbacksValue = downloadCallbacksValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDownloadCallbacksTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.isEnabled?.link {
           isEnabledValue = isEnabledValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.logId?.link {
           logIdValue = logIdValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.logLimit?.link {
           logLimitValue = logLimitValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.logLimitValidator) })
          }
        }()
        _ = {
         if key == parent?.payload?.link {
           payloadValue = payloadValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.referer?.link {
           refererValue = refererValue.merged(with: { deserialize(__dictValue, transform: URL.init(stringToEncode:)) })
          }
        }()
        _ = {
         if key == parent?.scopeId?.link {
           scopeIdValue = scopeIdValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.typed?.link {
           typedValue = typedValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTypedTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.url?.link {
           urlValue = urlValue.merged(with: { deserialize(__dictValue, transform: URL.init(stringToEncode:)) })
          }
        }()
        _ = {
         if key == parent?.visibilityDuration?.link {
           visibilityDurationValue = visibilityDurationValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.visibilityDurationValidator) })
          }
        }()
        _ = {
         if key == parent?.visibilityPercentage?.link {
           visibilityPercentageValue = visibilityPercentageValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.visibilityPercentageValidator) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { downloadCallbacksValue = downloadCallbacksValue.merged(with: { parent.downloadCallbacks?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { typedValue = typedValue.merged(with: { parent.typed?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      downloadCallbacksValue.errorsOrWarnings?.map { .nestedObjectError(field: "download_callbacks", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) },
      logIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_id", error: $0) },
      logLimitValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_limit", error: $0) },
      payloadValue.errorsOrWarnings?.map { .nestedObjectError(field: "payload", error: $0) },
      refererValue.errorsOrWarnings?.map { .nestedObjectError(field: "referer", error: $0) },
      scopeIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "scope_id", error: $0) },
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
      downloadCallbacks: { downloadCallbacksValue.value }(),
      isEnabled: { isEnabledValue.value }(),
      logId: { logIdNonNil }(),
      logLimit: { logLimitValue.value }(),
      payload: { payloadValue.value }(),
      referer: { refererValue.value }(),
      scopeId: { scopeIdValue.value }(),
      typed: { typedValue.value }(),
      url: { urlValue.value }(),
      visibilityDuration: { visibilityDurationValue.value }(),
      visibilityPercentage: { visibilityPercentageValue.value }()
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
      scopeId: merged.scopeId,
      typed: merged.typed?.tryResolveParent(templates: templates),
      url: merged.url,
      visibilityDuration: merged.visibilityDuration,
      visibilityPercentage: merged.visibilityPercentage
    )
  }
}
