// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivAction: @unchecked Sendable {
  public final class MenuItem: Sendable {
    public let action: DivAction?
    public let actions: [DivAction]?
    public let text: Expression<String>

    public func resolveText(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(text)
    }

    public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
      self.init(
        action: try dictionary.getOptionalField("action", transform: { (dict: [String: Any]) in try DivAction(dictionary: dict, context: context) }),
        actions: try dictionary.getOptionalArray("actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
        text: try dictionary.getExpressionField("text", context: context)
      )
    }

    init(
      action: DivAction? = nil,
      actions: [DivAction]? = nil,
      text: Expression<String>
    ) {
      self.action = action
      self.actions = actions
      self.text = text
    }
  }

  public let downloadCallbacks: DivDownloadCallbacks?
  public let isEnabled: Expression<Bool> // default value: true
  public let logId: Expression<String>
  public let logUrl: Expression<URL>?
  public let menuItems: [MenuItem]?
  public let payload: [String: Any]?
  public let referer: Expression<URL>?
  public let scopeId: String?
  public let typed: DivActionTyped?
  public let url: Expression<URL>?

  public func resolveIsEnabled(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(isEnabled) ?? true
  }

  public func resolveLogId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(logId)
  }

  public func resolveLogUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveUrl(logUrl)
  }

  public func resolveReferer(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveUrl(referer)
  }

  public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveUrl(url)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      downloadCallbacks: try dictionary.getOptionalField("download_callbacks", transform: { (dict: [String: Any]) in try DivDownloadCallbacks(dictionary: dict, context: context) }),
      isEnabled: try dictionary.getOptionalExpressionField("is_enabled", context: context),
      logId: try dictionary.getExpressionField("log_id", context: context),
      logUrl: try dictionary.getOptionalExpressionField("log_url", transform: URL.makeFromNonEncodedString, context: context),
      menuItems: try dictionary.getOptionalArray("menu_items", transform: { (dict: [String: Any]) in try? DivAction.MenuItem(dictionary: dict, context: context) }),
      payload: try dictionary.getOptionalField("payload", context: context),
      referer: try dictionary.getOptionalExpressionField("referer", transform: URL.makeFromNonEncodedString, context: context),
      scopeId: try dictionary.getOptionalField("scope_id", context: context),
      typed: try dictionary.getOptionalField("typed", transform: { (dict: [String: Any]) in try DivActionTyped(dictionary: dict, context: context) }),
      url: try dictionary.getOptionalExpressionField("url", transform: URL.makeFromNonEncodedString, context: context)
    )
  }

  init(
    downloadCallbacks: DivDownloadCallbacks? = nil,
    isEnabled: Expression<Bool>? = nil,
    logId: Expression<String>,
    logUrl: Expression<URL>? = nil,
    menuItems: [MenuItem]? = nil,
    payload: [String: Any]? = nil,
    referer: Expression<URL>? = nil,
    scopeId: String? = nil,
    typed: DivActionTyped? = nil,
    url: Expression<URL>? = nil
  ) {
    self.downloadCallbacks = downloadCallbacks
    self.isEnabled = isEnabled ?? .value(true)
    self.logId = logId
    self.logUrl = logUrl
    self.menuItems = menuItems
    self.payload = payload
    self.referer = referer
    self.scopeId = scopeId
    self.typed = typed
    self.url = url
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DivAction: Equatable {
  public static func ==(lhs: DivAction, rhs: DivAction) -> Bool {
    guard
      lhs.downloadCallbacks == rhs.downloadCallbacks,
      lhs.isEnabled == rhs.isEnabled,
      lhs.logId == rhs.logId
    else {
      return false
    }
    guard
      lhs.logUrl == rhs.logUrl,
      lhs.menuItems == rhs.menuItems,
      lhs.referer == rhs.referer
    else {
      return false
    }
    guard
      lhs.scopeId == rhs.scopeId,
      lhs.typed == rhs.typed,
      lhs.url == rhs.url
    else {
      return false
    }
    return true
  }
}
#endif

extension DivAction: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["download_callbacks"] = downloadCallbacks?.toDictionary()
    result["is_enabled"] = isEnabled.toValidSerializationValue()
    result["log_id"] = logId.toValidSerializationValue()
    result["log_url"] = logUrl?.toValidSerializationValue()
    result["menu_items"] = menuItems?.map { $0.toDictionary() }
    result["payload"] = payload
    result["referer"] = referer?.toValidSerializationValue()
    result["scope_id"] = scopeId
    result["typed"] = typed?.toDictionary()
    result["url"] = url?.toValidSerializationValue()
    return result
  }
}

#if DEBUG
extension DivAction.MenuItem: Equatable {
  public static func ==(lhs: DivAction.MenuItem, rhs: DivAction.MenuItem) -> Bool {
    guard
      lhs.action == rhs.action,
      lhs.actions == rhs.actions,
      lhs.text == rhs.text
    else {
      return false
    }
    return true
  }
}
#endif

extension DivAction.MenuItem: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["action"] = action?.toDictionary()
    result["actions"] = actions?.map { $0.toDictionary() }
    result["text"] = text.toValidSerializationValue()
    return result
  }
}
