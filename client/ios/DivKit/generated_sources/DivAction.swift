// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivAction {
  public final class MenuItem {
    public let action: DivAction?
    public let actions: [DivAction]? // at least 1 elements
    public let text: Expression<String> // at least 1 char

    public func resolveText(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: text, initializer: { $0 })
    }

    static let actionValidator: AnyValueValidator<DivAction> =
      makeNoOpValueValidator()

    static let actionsValidator: AnyArrayValueValidator<DivAction> =
      makeArrayValidator(minItems: 1)

    static let textValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

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
  public let logId: String // at least 1 char
  public let logUrl: Expression<URL>?
  public let menuItems: [MenuItem]? // at least 1 elements
  public let payload: [String: Any]?
  public let referer: Expression<URL>?
  public let url: Expression<URL>?

  public func resolveLogUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveStringBasedValue(expression: logUrl, initializer: URL.init(string:))
  }

  public func resolveReferer(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveStringBasedValue(expression: referer, initializer: URL.init(string:))
  }

  public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveStringBasedValue(expression: url, initializer: URL.init(string:))
  }

  static let downloadCallbacksValidator: AnyValueValidator<DivDownloadCallbacks> =
    makeNoOpValueValidator()

  static let logIdValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let logUrlValidator: AnyValueValidator<URL> =
    makeNoOpValueValidator()

  static let menuItemsValidator: AnyArrayValueValidator<DivAction.MenuItem> =
    makeArrayValidator(minItems: 1)

  static let payloadValidator: AnyValueValidator<[String: Any]> =
    makeNoOpValueValidator()

  static let refererValidator: AnyValueValidator<URL> =
    makeNoOpValueValidator()

  static let urlValidator: AnyValueValidator<URL> =
    makeNoOpValueValidator()

  init(
    downloadCallbacks: DivDownloadCallbacks? = nil,
    logId: String,
    logUrl: Expression<URL>? = nil,
    menuItems: [MenuItem]? = nil,
    payload: [String: Any]? = nil,
    referer: Expression<URL>? = nil,
    url: Expression<URL>? = nil
  ) {
    self.downloadCallbacks = downloadCallbacks
    self.logId = logId
    self.logUrl = logUrl
    self.menuItems = menuItems
    self.payload = payload
    self.referer = referer
    self.url = url
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DivAction: Equatable {
  public static func ==(lhs: DivAction, rhs: DivAction) -> Bool {
    guard
      lhs.downloadCallbacks == rhs.downloadCallbacks,
      lhs.logId == rhs.logId,
      lhs.logUrl == rhs.logUrl
    else {
      return false
    }
    guard
      lhs.menuItems == rhs.menuItems,
      lhs.referer == rhs.referer,
      lhs.url == rhs.url
    else {
      return false
    }
    return true
  }
}
#endif

extension DivAction: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["download_callbacks"] = downloadCallbacks?.toDictionary()
    result["log_id"] = logId
    result["log_url"] = logUrl?.toValidSerializationValue()
    result["menu_items"] = menuItems?.map { $0.toDictionary() }
    result["payload"] = payload
    result["referer"] = referer?.toValidSerializationValue()
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
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["action"] = action?.toDictionary()
    result["actions"] = actions?.map { $0.toDictionary() }
    result["text"] = text.toValidSerializationValue()
    return result
  }
}
