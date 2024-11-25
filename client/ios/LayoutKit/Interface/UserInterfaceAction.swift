import Foundation

import VGSL

public struct UserInterfaceAction: Equatable, Codable {
  @frozen
  public enum Payload: Equatable {
    case empty // means just analytics logging
    case url(URL)
    case menu(Menu)
    case divAction(params: DivActionParams)
  }

  public struct DivActionParams: Equatable {
    public let action: JSONObject
    public let path: UIElementPath
    public let source: DivActionSource
    public let url: URL?
    public let localValues: [String: AnyHashable]

    public var cardId: String {
      path.root
    }

    /// Deprecated.
    public init(
      action: JSONObject,
      cardId: String,
      source: DivActionSource,
      url: URL?,
      localValues: [String: AnyHashable] = [:]
    ) {
      self.action = action
      self.path = UIElementPath(cardId)
      self.source = source
      self.url = url
      self.localValues = localValues
    }

    public init(
      action: JSONObject,
      path: UIElementPath,
      source: DivActionSource,
      url: URL?,
      localValues: [String: AnyHashable] = [:]
    ) {
      self.action = action
      self.path = path
      self.source = source
      self.url = url
      self.localValues = localValues
    }
  }

  @frozen
  public enum DivActionSource: String {
    case tap
    case visibility
    case disappear
    case timer
    case trigger
    case callback
  }

  public let payload: Payload
  public let path: UIElementPath
  public let accessibilityElement: AccessibilityElement?

  public var url: URL? {
    payload.url
  }

  public init(
    payload: Payload,
    path: UIElementPath,
    accessibilityElement: AccessibilityElement?
  ) {
    self.payload = payload
    self.path = path
    self.accessibilityElement = accessibilityElement
  }

  public init(
    payload: Payload,
    path: UIElementPath
  ) {
    self.init(
      payload: payload,
      path: path,
      accessibilityElement: nil
    )
  }

  public init(
    url: URL,
    path: UIElementPath,
    accessibilityElement: AccessibilityElement? = nil
  ) {
    self.init(
      payload: .url(url),
      path: path,
      accessibilityElement: accessibilityElement
    )
  }

  public init(
    menu: Menu,
    path: UIElementPath,
    accessibilityElement: AccessibilityElement? = nil
  ) {
    self.init(
      payload: .menu(menu),
      path: path,
      accessibilityElement: accessibilityElement
    )
  }

  public init(path: UIElementPath, accessibilityElement: AccessibilityElement? = nil) {
    self.init(payload: .empty, path: path, accessibilityElement: accessibilityElement)
  }
}

extension UserInterfaceAction.Payload: Codable {
  private enum Kind: String {
    case empty
    case url
    case menu
    case divAction
  }

  private enum CodingKeys: String, CodingKey {
    case kind
    case url
    case menu
    case json
    case path
    case divActionSource
  }

  private var kind: Kind {
    switch self {
    case .empty:
      .empty
    case .url:
      .url
    case .menu:
      .menu
    case .divAction:
      .divAction
    }
  }

  public init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)

    let kindString = try container.decode(String.self, forKey: .kind)
    let kind = Kind(rawValue: kindString)

    switch kind {
    case .none:
      throw DecodingError.dataCorrupted(
        DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "")
      )
    case .empty?:
      self = .empty
    case .url?:
      let url = try container.decode(URL.self, forKey: .url)
      self = .url(url)
    case .menu?:
      let menu = try container.decode(Menu.self, forKey: .menu)
      self = .menu(menu)
    case .divAction?:
      let source = try container.decodeIfPresent(String.self, forKey: .divActionSource)
      let params = try UserInterfaceAction.DivActionParams(
        action: container.decode(JSONObject.self, forKey: .json),
        path: UIElementPath.parse(container.decode(String.self, forKey: .path)),
        source: UserInterfaceAction.DivActionSource(rawValue: source ?? "") ?? .tap,
        url: container.decodeIfPresent(URL.self, forKey: .url),
        localValues: [:]
      )
      self = .divAction(params: params)
    }
  }

  public func encode(to encoder: Encoder) throws {
    var container = encoder.container(keyedBy: CodingKeys.self)

    try container.encode(kind.rawValue, forKey: .kind)

    switch self {
    case .empty:
      break
    case let .url(url):
      try container.encode(url, forKey: .url)
    case let .menu(menu):
      try container.encode(menu, forKey: .menu)
    case let .divAction(params):
      try container.encode(params.action, forKey: .json)
      try container.encode(params.path.description, forKey: .path)
      try container.encode(params.source.rawValue, forKey: .divActionSource)
      try container.encodeIfPresent(params.url, forKey: .url)
    }
  }
}

extension UserInterfaceAction.Payload: CustomDebugStringConvertible {
  public var debugDescription: String {
    switch self {
    case .empty:
      "Empty"
    case let .url(value):
      "URL: \(value)"
    case let .menu(value):
      "Menu: \(value)"
    case let .divAction(params):
      "DivAction: \(params.action)"
    }
  }
}

extension URL? {
  public func action(
    with path: UIElementPath,
    accessibilityElement: AccessibilityElement? = nil
  ) -> UserInterfaceAction? {
    flatMap { $0.action(with: path, accessibilityElement: accessibilityElement) }
  }
}

extension URL {
  public func action(
    with path: UIElementPath,
    accessibilityElement: AccessibilityElement? = nil
  ) -> UserInterfaceAction {
    UserInterfaceAction(
      url: self,
      path: path,
      accessibilityElement: accessibilityElement
    )
  }
}

extension UserInterfaceAction.Payload {
  public enum Error: Swift.Error {
    case notAnURL
  }

  public var url: URL? {
    switch self {
    case let .url(value):
      value
    case let .divAction(params):
      params.source == .visibility ? nil : params.url
    case .empty, .menu:
      nil
    }
  }

  public func getComposedURLs() throws -> [URL] {
    switch self {
    case let .url(url):
      return [url]
    case let .divAction(params):
      guard let url = params.url else {
        throw Error.notAnURL
      }
      return [url]
    case .empty:
      return []
    case .menu:
      throw Error.notAnURL
    }
  }

  public var divActionParams: UserInterfaceAction.DivActionParams? {
    switch self {
    case let .divAction(params):
      params
    case .empty, .menu, .url:
      nil
    }
  }
}
