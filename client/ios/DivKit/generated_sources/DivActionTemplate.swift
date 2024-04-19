// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivActionTemplate: TemplateValue {
  public final class MenuItemTemplate: TemplateValue {
    public let action: Field<DivActionTemplate>?
    public let actions: Field<[DivActionTemplate]>?
    public let text: Field<Expression<String>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        action: dictionary.getOptionalField("action", templateToType: templateToType),
        actions: dictionary.getOptionalArray("actions", templateToType: templateToType),
        text: dictionary.getOptionalExpressionField("text")
      )
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

    private static func resolveOnlyLinks(context: TemplatesContext, parent: MenuItemTemplate?) -> DeserializationResult<DivAction.MenuItem> {
      let actionValue = parent?.action?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let actionsValue = parent?.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let textValue = parent?.text?.resolveValue(context: context) ?? .noValue
      var errors = mergeErrors(
        actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
        actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
        textValue.errorsOrWarnings?.map { .nestedObjectError(field: "text", error: $0) }
      )
      if case .noValue = textValue {
        errors.append(.requiredFieldIsMissing(field: "text"))
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

    public static func resolveValue(context: TemplatesContext, parent: MenuItemTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAction.MenuItem> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var actionValue: DeserializationResult<DivAction> = .noValue
      var actionsValue: DeserializationResult<[DivAction]> = .noValue
      var textValue: DeserializationResult<Expression<String>> = parent?.text?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "action":
          actionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionValue)
        case "actions":
          actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionsValue)
        case "text":
          textValue = deserialize(__dictValue).merged(with: textValue)
        case parent?.action?.link:
          actionValue = actionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
        case parent?.actions?.link:
          actionsValue = actionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
        case parent?.text?.link:
          textValue = textValue.merged(with: { deserialize(__dictValue) })
        default: break
        }
      }
      if let parent = parent {
        actionValue = actionValue.merged(with: { parent.action?.resolveOptionalValue(context: context, useOnlyLinks: true) })
        actionsValue = actionsValue.merged(with: { parent.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      }
      var errors = mergeErrors(
        actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
        actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
        textValue.errorsOrWarnings?.map { .nestedObjectError(field: "text", error: $0) }
      )
      if case .noValue = textValue {
        errors.append(.requiredFieldIsMissing(field: "text"))
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

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> MenuItemTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> MenuItemTemplate {
      let merged = try mergedWithParent(templates: templates)

      return MenuItemTemplate(
        action: merged.action?.tryResolveParent(templates: templates),
        actions: merged.actions?.tryResolveParent(templates: templates),
        text: merged.text
      )
    }
  }

  public let downloadCallbacks: Field<DivDownloadCallbacksTemplate>?
  public let isEnabled: Field<Expression<Bool>>? // default value: true
  public let logId: Field<Expression<String>>?
  public let logUrl: Field<Expression<URL>>?
  public let menuItems: Field<[MenuItemTemplate]>?
  public let payload: Field<[String: Any]>?
  public let referer: Field<Expression<URL>>?
  public let typed: Field<DivActionTypedTemplate>?
  public let url: Field<Expression<URL>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      downloadCallbacks: dictionary.getOptionalField("download_callbacks", templateToType: templateToType),
      isEnabled: dictionary.getOptionalExpressionField("is_enabled"),
      logId: dictionary.getOptionalExpressionField("log_id"),
      logUrl: dictionary.getOptionalExpressionField("log_url", transform: URL.init(string:)),
      menuItems: dictionary.getOptionalArray("menu_items", templateToType: templateToType),
      payload: dictionary.getOptionalField("payload"),
      referer: dictionary.getOptionalExpressionField("referer", transform: URL.init(string:)),
      typed: dictionary.getOptionalField("typed", templateToType: templateToType),
      url: dictionary.getOptionalExpressionField("url", transform: URL.init(string:))
    )
  }

  init(
    downloadCallbacks: Field<DivDownloadCallbacksTemplate>? = nil,
    isEnabled: Field<Expression<Bool>>? = nil,
    logId: Field<Expression<String>>? = nil,
    logUrl: Field<Expression<URL>>? = nil,
    menuItems: Field<[MenuItemTemplate]>? = nil,
    payload: Field<[String: Any]>? = nil,
    referer: Field<Expression<URL>>? = nil,
    typed: Field<DivActionTypedTemplate>? = nil,
    url: Field<Expression<URL>>? = nil
  ) {
    self.downloadCallbacks = downloadCallbacks
    self.isEnabled = isEnabled
    self.logId = logId
    self.logUrl = logUrl
    self.menuItems = menuItems
    self.payload = payload
    self.referer = referer
    self.typed = typed
    self.url = url
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionTemplate?) -> DeserializationResult<DivAction> {
    let downloadCallbacksValue = parent?.downloadCallbacks?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let isEnabledValue = parent?.isEnabled?.resolveOptionalValue(context: context) ?? .noValue
    let logIdValue = parent?.logId?.resolveValue(context: context) ?? .noValue
    let logUrlValue = parent?.logUrl?.resolveOptionalValue(context: context, transform: URL.init(string:)) ?? .noValue
    let menuItemsValue = parent?.menuItems?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let payloadValue = parent?.payload?.resolveOptionalValue(context: context) ?? .noValue
    let refererValue = parent?.referer?.resolveOptionalValue(context: context, transform: URL.init(string:)) ?? .noValue
    let typedValue = parent?.typed?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let urlValue = parent?.url?.resolveOptionalValue(context: context, transform: URL.init(string:)) ?? .noValue
    var errors = mergeErrors(
      downloadCallbacksValue.errorsOrWarnings?.map { .nestedObjectError(field: "download_callbacks", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) },
      logIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_id", error: $0) },
      logUrlValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_url", error: $0) },
      menuItemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "menu_items", error: $0) },
      payloadValue.errorsOrWarnings?.map { .nestedObjectError(field: "payload", error: $0) },
      refererValue.errorsOrWarnings?.map { .nestedObjectError(field: "referer", error: $0) },
      typedValue.errorsOrWarnings?.map { .nestedObjectError(field: "typed", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
    )
    if case .noValue = logIdValue {
      errors.append(.requiredFieldIsMissing(field: "log_id"))
    }
    guard
      let logIdNonNil = logIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivAction(
      downloadCallbacks: downloadCallbacksValue.value,
      isEnabled: isEnabledValue.value,
      logId: logIdNonNil,
      logUrl: logUrlValue.value,
      menuItems: menuItemsValue.value,
      payload: payloadValue.value,
      referer: refererValue.value,
      typed: typedValue.value,
      url: urlValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAction> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var downloadCallbacksValue: DeserializationResult<DivDownloadCallbacks> = .noValue
    var isEnabledValue: DeserializationResult<Expression<Bool>> = parent?.isEnabled?.value() ?? .noValue
    var logIdValue: DeserializationResult<Expression<String>> = parent?.logId?.value() ?? .noValue
    var logUrlValue: DeserializationResult<Expression<URL>> = parent?.logUrl?.value() ?? .noValue
    var menuItemsValue: DeserializationResult<[DivAction.MenuItem]> = .noValue
    var payloadValue: DeserializationResult<[String: Any]> = parent?.payload?.value() ?? .noValue
    var refererValue: DeserializationResult<Expression<URL>> = parent?.referer?.value() ?? .noValue
    var typedValue: DeserializationResult<DivActionTyped> = .noValue
    var urlValue: DeserializationResult<Expression<URL>> = parent?.url?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "download_callbacks":
        downloadCallbacksValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDownloadCallbacksTemplate.self).merged(with: downloadCallbacksValue)
      case "is_enabled":
        isEnabledValue = deserialize(__dictValue).merged(with: isEnabledValue)
      case "log_id":
        logIdValue = deserialize(__dictValue).merged(with: logIdValue)
      case "log_url":
        logUrlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: logUrlValue)
      case "menu_items":
        menuItemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.MenuItemTemplate.self).merged(with: menuItemsValue)
      case "payload":
        payloadValue = deserialize(__dictValue).merged(with: payloadValue)
      case "referer":
        refererValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: refererValue)
      case "typed":
        typedValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTypedTemplate.self).merged(with: typedValue)
      case "url":
        urlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: urlValue)
      case parent?.downloadCallbacks?.link:
        downloadCallbacksValue = downloadCallbacksValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDownloadCallbacksTemplate.self) })
      case parent?.isEnabled?.link:
        isEnabledValue = isEnabledValue.merged(with: { deserialize(__dictValue) })
      case parent?.logId?.link:
        logIdValue = logIdValue.merged(with: { deserialize(__dictValue) })
      case parent?.logUrl?.link:
        logUrlValue = logUrlValue.merged(with: { deserialize(__dictValue, transform: URL.init(string:)) })
      case parent?.menuItems?.link:
        menuItemsValue = menuItemsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.MenuItemTemplate.self) })
      case parent?.payload?.link:
        payloadValue = payloadValue.merged(with: { deserialize(__dictValue) })
      case parent?.referer?.link:
        refererValue = refererValue.merged(with: { deserialize(__dictValue, transform: URL.init(string:)) })
      case parent?.typed?.link:
        typedValue = typedValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTypedTemplate.self) })
      case parent?.url?.link:
        urlValue = urlValue.merged(with: { deserialize(__dictValue, transform: URL.init(string:)) })
      default: break
      }
    }
    if let parent = parent {
      downloadCallbacksValue = downloadCallbacksValue.merged(with: { parent.downloadCallbacks?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      menuItemsValue = menuItemsValue.merged(with: { parent.menuItems?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      typedValue = typedValue.merged(with: { parent.typed?.resolveOptionalValue(context: context, useOnlyLinks: true) })
    }
    var errors = mergeErrors(
      downloadCallbacksValue.errorsOrWarnings?.map { .nestedObjectError(field: "download_callbacks", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) },
      logIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_id", error: $0) },
      logUrlValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_url", error: $0) },
      menuItemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "menu_items", error: $0) },
      payloadValue.errorsOrWarnings?.map { .nestedObjectError(field: "payload", error: $0) },
      refererValue.errorsOrWarnings?.map { .nestedObjectError(field: "referer", error: $0) },
      typedValue.errorsOrWarnings?.map { .nestedObjectError(field: "typed", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
    )
    if case .noValue = logIdValue {
      errors.append(.requiredFieldIsMissing(field: "log_id"))
    }
    guard
      let logIdNonNil = logIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivAction(
      downloadCallbacks: downloadCallbacksValue.value,
      isEnabled: isEnabledValue.value,
      logId: logIdNonNil,
      logUrl: logUrlValue.value,
      menuItems: menuItemsValue.value,
      payload: payloadValue.value,
      referer: refererValue.value,
      typed: typedValue.value,
      url: urlValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionTemplate(
      downloadCallbacks: merged.downloadCallbacks?.tryResolveParent(templates: templates),
      isEnabled: merged.isEnabled,
      logId: merged.logId,
      logUrl: merged.logUrl,
      menuItems: merged.menuItems?.tryResolveParent(templates: templates),
      payload: merged.payload,
      referer: merged.referer,
      typed: merged.typed?.tryResolveParent(templates: templates),
      url: merged.url
    )
  }
}
