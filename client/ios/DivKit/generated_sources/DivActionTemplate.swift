// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivActionTemplate: TemplateValue, TemplateDeserializable {
  public final class MenuItemTemplate: TemplateValue, TemplateDeserializable {
    public let action: Field<DivActionTemplate>?
    public let actions: Field<[DivActionTemplate]>? // at least 1 elements
    public let text: Field<Expression<String>>? // at least 1 char

    public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
      do {
        self.init(
          action: try dictionary.getOptionalField("action", templateToType: templateToType),
          actions: try dictionary.getOptionalArray("actions", templateToType: templateToType),
          text: try dictionary.getOptionalField("text")
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "menu_item_template." + field, representation: representation)
      }
    }

    init(
      action: Field<DivActionTemplate>? = nil,
      actions: Field<[DivActionTemplate]>? = nil,
      text: Field<Expression<String>>? = nil
    ) {
      self.action = action
      self.actions = actions
      self.text = text
    }

    private static func resolveOnlyLinks(context: Context, parent: MenuItemTemplate?) -> DeserializationResult<DivAction.MenuItem> {
      let actionValue = parent?.action?.resolveOptionalValue(context: context, validator: ResolvedValue.actionValidator, useOnlyLinks: true) ?? .noValue
      let actionsValue = parent?.actions?.resolveOptionalValue(context: context, validator: ResolvedValue.actionsValidator, useOnlyLinks: true) ?? .noValue
      let textValue = parent?.text?.resolveValue(context: context, validator: ResolvedValue.textValidator) ?? .noValue
      var errors = mergeErrors(
        actionValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "action", level: .warning)) },
        actionsValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "actions", level: .warning)) },
        textValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "text", level: .error)) }
      )
      if case .noValue = textValue {
        errors.append(.right(FieldError(fieldName: "text", level: .error, error: .requiredFieldIsMissing)))
      }
      guard
        let textNonNil = textValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivAction.MenuItem(
        action: actionValue.value,
        actions: actionsValue.value,
        text: textNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: Context, parent: MenuItemTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAction.MenuItem> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var actionValue: DeserializationResult<DivAction> = .noValue
      var actionsValue: DeserializationResult<[DivAction]> = .noValue
      var textValue: DeserializationResult<Expression<String>> = parent?.text?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "action":
          actionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionValidator, type: DivActionTemplate.self).merged(with: actionValue)
        case "actions":
          actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionsValidator, type: DivActionTemplate.self).merged(with: actionsValue)
        case "text":
          textValue = deserialize(__dictValue, validator: ResolvedValue.textValidator).merged(with: textValue)
        case parent?.action?.link:
          actionValue = actionValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionValidator, type: DivActionTemplate.self))
        case parent?.actions?.link:
          actionsValue = actionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionsValidator, type: DivActionTemplate.self))
        case parent?.text?.link:
          textValue = textValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.textValidator))
        default: break
        }
      }
      if let parent = parent {
        actionValue = actionValue.merged(with: parent.action?.resolveOptionalValue(context: context, validator: ResolvedValue.actionValidator, useOnlyLinks: true))
        actionsValue = actionsValue.merged(with: parent.actions?.resolveOptionalValue(context: context, validator: ResolvedValue.actionsValidator, useOnlyLinks: true))
      }
      var errors = mergeErrors(
        actionValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "action", level: .warning)) },
        actionsValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "actions", level: .warning)) },
        textValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "text", level: .error)) }
      )
      if case .noValue = textValue {
        errors.append(.right(FieldError(fieldName: "text", level: .error, error: .requiredFieldIsMissing)))
      }
      guard
        let textNonNil = textValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivAction.MenuItem(
        action: actionValue.value,
        actions: actionsValue.value,
        text: textNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: Templates) throws -> MenuItemTemplate {
      return self
    }

    public func resolveParent(templates: Templates) throws -> MenuItemTemplate {
      let merged = try mergedWithParent(templates: templates)

      return MenuItemTemplate(
        action: merged.action?.tryResolveParent(templates: templates),
        actions: merged.actions?.tryResolveParent(templates: templates),
        text: merged.text
      )
    }
  }

  public let downloadCallbacks: Field<DivDownloadCallbacksTemplate>?
  public let logId: Field<String>? // at least 1 char
  public let logUrl: Field<Expression<URL>>?
  public let menuItems: Field<[MenuItemTemplate]>? // at least 1 elements
  public let payload: Field<[String: Any]>?
  public let referer: Field<Expression<URL>>?
  public let url: Field<Expression<URL>>?

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        downloadCallbacks: try dictionary.getOptionalField("download_callbacks", templateToType: templateToType),
        logId: try dictionary.getOptionalField("log_id"),
        logUrl: try dictionary.getOptionalField("log_url", transform: URL.init(string:)),
        menuItems: try dictionary.getOptionalArray("menu_items", templateToType: templateToType),
        payload: try dictionary.getOptionalField("payload"),
        referer: try dictionary.getOptionalField("referer", transform: URL.init(string:)),
        url: try dictionary.getOptionalField("url", transform: URL.init(string:))
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-action_template." + field, representation: representation)
    }
  }

  init(
    downloadCallbacks: Field<DivDownloadCallbacksTemplate>? = nil,
    logId: Field<String>? = nil,
    logUrl: Field<Expression<URL>>? = nil,
    menuItems: Field<[MenuItemTemplate]>? = nil,
    payload: Field<[String: Any]>? = nil,
    referer: Field<Expression<URL>>? = nil,
    url: Field<Expression<URL>>? = nil
  ) {
    self.downloadCallbacks = downloadCallbacks
    self.logId = logId
    self.logUrl = logUrl
    self.menuItems = menuItems
    self.payload = payload
    self.referer = referer
    self.url = url
  }

  private static func resolveOnlyLinks(context: Context, parent: DivActionTemplate?) -> DeserializationResult<DivAction> {
    let downloadCallbacksValue = parent?.downloadCallbacks?.resolveOptionalValue(context: context, validator: ResolvedValue.downloadCallbacksValidator, useOnlyLinks: true) ?? .noValue
    let logIdValue = parent?.logId?.resolveValue(context: context, validator: ResolvedValue.logIdValidator) ?? .noValue
    let logUrlValue = parent?.logUrl?.resolveOptionalValue(context: context, transform: URL.init(string:), validator: ResolvedValue.logUrlValidator) ?? .noValue
    let menuItemsValue = parent?.menuItems?.resolveOptionalValue(context: context, validator: ResolvedValue.menuItemsValidator, useOnlyLinks: true) ?? .noValue
    let payloadValue = parent?.payload?.resolveOptionalValue(context: context, validator: ResolvedValue.payloadValidator) ?? .noValue
    let refererValue = parent?.referer?.resolveOptionalValue(context: context, transform: URL.init(string:), validator: ResolvedValue.refererValidator) ?? .noValue
    let urlValue = parent?.url?.resolveOptionalValue(context: context, transform: URL.init(string:), validator: ResolvedValue.urlValidator) ?? .noValue
    var errors = mergeErrors(
      downloadCallbacksValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "download_callbacks", level: .warning)) },
      logIdValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "log_id", level: .error)) },
      logUrlValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "log_url", level: .warning)) },
      menuItemsValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "menu_items", level: .warning)) },
      payloadValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "payload", level: .warning)) },
      refererValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "referer", level: .warning)) },
      urlValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "url", level: .warning)) }
    )
    if case .noValue = logIdValue {
      errors.append(.right(FieldError(fieldName: "log_id", level: .error, error: .requiredFieldIsMissing)))
    }
    guard
      let logIdNonNil = logIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivAction(
      downloadCallbacks: downloadCallbacksValue.value,
      logId: logIdNonNil,
      logUrl: logUrlValue.value,
      menuItems: menuItemsValue.value,
      payload: payloadValue.value,
      referer: refererValue.value,
      url: urlValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivActionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAction> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var downloadCallbacksValue: DeserializationResult<DivDownloadCallbacks> = .noValue
    var logIdValue: DeserializationResult<String> = parent?.logId?.value(validatedBy: ResolvedValue.logIdValidator) ?? .noValue
    var logUrlValue: DeserializationResult<Expression<URL>> = parent?.logUrl?.value() ?? .noValue
    var menuItemsValue: DeserializationResult<[DivAction.MenuItem]> = .noValue
    var payloadValue: DeserializationResult<[String: Any]> = parent?.payload?.value(validatedBy: ResolvedValue.payloadValidator) ?? .noValue
    var refererValue: DeserializationResult<Expression<URL>> = parent?.referer?.value() ?? .noValue
    var urlValue: DeserializationResult<Expression<URL>> = parent?.url?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "download_callbacks":
        downloadCallbacksValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.downloadCallbacksValidator, type: DivDownloadCallbacksTemplate.self).merged(with: downloadCallbacksValue)
      case "log_id":
        logIdValue = deserialize(__dictValue, validator: ResolvedValue.logIdValidator).merged(with: logIdValue)
      case "log_url":
        logUrlValue = deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.logUrlValidator).merged(with: logUrlValue)
      case "menu_items":
        menuItemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.menuItemsValidator, type: DivActionTemplate.MenuItemTemplate.self).merged(with: menuItemsValue)
      case "payload":
        payloadValue = deserialize(__dictValue, validator: ResolvedValue.payloadValidator).merged(with: payloadValue)
      case "referer":
        refererValue = deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.refererValidator).merged(with: refererValue)
      case "url":
        urlValue = deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.urlValidator).merged(with: urlValue)
      case parent?.downloadCallbacks?.link:
        downloadCallbacksValue = downloadCallbacksValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.downloadCallbacksValidator, type: DivDownloadCallbacksTemplate.self))
      case parent?.logId?.link:
        logIdValue = logIdValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.logIdValidator))
      case parent?.logUrl?.link:
        logUrlValue = logUrlValue.merged(with: deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.logUrlValidator))
      case parent?.menuItems?.link:
        menuItemsValue = menuItemsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.menuItemsValidator, type: DivActionTemplate.MenuItemTemplate.self))
      case parent?.payload?.link:
        payloadValue = payloadValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.payloadValidator))
      case parent?.referer?.link:
        refererValue = refererValue.merged(with: deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.refererValidator))
      case parent?.url?.link:
        urlValue = urlValue.merged(with: deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.urlValidator))
      default: break
      }
    }
    if let parent = parent {
      downloadCallbacksValue = downloadCallbacksValue.merged(with: parent.downloadCallbacks?.resolveOptionalValue(context: context, validator: ResolvedValue.downloadCallbacksValidator, useOnlyLinks: true))
      menuItemsValue = menuItemsValue.merged(with: parent.menuItems?.resolveOptionalValue(context: context, validator: ResolvedValue.menuItemsValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      downloadCallbacksValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "download_callbacks", level: .warning)) },
      logIdValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "log_id", level: .error)) },
      logUrlValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "log_url", level: .warning)) },
      menuItemsValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "menu_items", level: .warning)) },
      payloadValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "payload", level: .warning)) },
      refererValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "referer", level: .warning)) },
      urlValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "url", level: .warning)) }
    )
    if case .noValue = logIdValue {
      errors.append(.right(FieldError(fieldName: "log_id", level: .error, error: .requiredFieldIsMissing)))
    }
    guard
      let logIdNonNil = logIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivAction(
      downloadCallbacks: downloadCallbacksValue.value,
      logId: logIdNonNil,
      logUrl: logUrlValue.value,
      menuItems: menuItemsValue.value,
      payload: payloadValue.value,
      referer: refererValue.value,
      url: urlValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivActionTemplate {
    return self
  }

  public func resolveParent(templates: Templates) throws -> DivActionTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionTemplate(
      downloadCallbacks: merged.downloadCallbacks?.tryResolveParent(templates: templates),
      logId: merged.logId,
      logUrl: merged.logUrl,
      menuItems: merged.menuItems?.tryResolveParent(templates: templates),
      payload: merged.payload,
      referer: merged.referer,
      url: merged.url
    )
  }
}
