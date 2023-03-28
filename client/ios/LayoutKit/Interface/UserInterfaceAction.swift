import Foundation

import BaseTinyPublic
import BaseUIPublic

public struct UserInterfaceAction: Equatable, Codable {
  @frozen
  public enum Payload: Equatable {
    case empty // means just analytics logging
    case url(URL)
    case menu(Menu)
    case json(JSONObject)
    indirect case composite(Payload, Payload)
    case divAction(params: DivActionParams)
  }

  public struct DivActionParams: Equatable {
    public let action: JSONObject
    public let cardId: String
    public let source: DivActionSource
    public let url: URL?

    public init(
      action: JSONObject,
      cardId: String,
      source: DivActionSource,
      url: URL?
    ) {
      self.action = action
      self.cardId = cardId
      self.source = source
      self.url = url
    }
  }

  @frozen
  public enum DivActionSource: String {
    case tap
    case visibility
    case custom
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

  public init(
    json: JSONObject,
    path: UIElementPath,
    accessibilityElement: AccessibilityElement? = nil
  ) {
    self.init(
      payload: .json(json),
      path: path,
      accessibilityElement: accessibilityElement
    )
  }

  public init(
    payloads: [Payload],
    path: UIElementPath,
    accessibilityElement: AccessibilityElement? = nil
  ) {
    let payload: Payload = payloads.reduce(.empty) {
      if $0 == .empty {
        return $1
      } else if $1 == .empty {
        return $0
      }

      return .composite($0, $1)
    }
    self.init(
      payload: payload,
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
    case json
    case divAction
    case composite
  }

  private enum CodingKeys: String, CodingKey {
    case kind
    case url
    case menu
    case json
    case cardId
    case firstPayload
    case secondPayload
    case divActionSource
  }

  private var kind: Kind {
    switch self {
    case .empty:
      return .empty
    case .url:
      return .url
    case .menu:
      return .menu
    case .json:
      return .json
    case .composite:
      return .composite
    case .divAction:
      return .divAction
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
    case .json?:
      let json = try container.decode(JSONObject.self, forKey: .json)
      self = .json(json)
    case .composite?:
      let first = try container.decode(type(of: self).self, forKey: .firstPayload)
      let second = try container.decode(type(of: self).self, forKey: .secondPayload)
      self = .composite(first, second)
    case .divAction?:
      let source = try container.decodeIfPresent(String.self, forKey: .divActionSource)
      let params = UserInterfaceAction.DivActionParams(
        action: try container.decode(JSONObject.self, forKey: .json),
        cardId: try container.decode(String.self, forKey: .cardId),
        source: UserInterfaceAction.DivActionSource(rawValue: source ?? "") ?? .tap,
        url: try container.decodeIfPresent(URL.self, forKey: .url)
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
    case let .json(json):
      try container.encode(json, forKey: .json)
    case let .composite(first, second):
      try container.encode(first, forKey: .firstPayload)
      try container.encode(second, forKey: .secondPayload)
    case let .divAction(params):
      try container.encode(params.action, forKey: .json)
      try container.encode(params.cardId, forKey: .cardId)
      try container.encode(params.source.rawValue, forKey: .divActionSource)
      try container.encodeIfPresent(params.url, forKey: .url)
    }
  }
}

extension UserInterfaceAction.Payload: CustomDebugStringConvertible {
  public var debugDescription: String {
    switch self {
    case .empty:
      return "Empty"
    case let .url(value):
      return "URL: \(value)"
    case let .menu(value):
      return "Menu: \(value)"
    case let .json(value):
      return "JSON: \(value))"
    case let .divAction(params):
      return "DivAction: \(params.action)"
    case let .composite(lhs, rhs):
      return """
      Composite [
      \(lhs.debugDescription.indented())
      \(rhs.debugDescription.indented())
      ]
      """
    }
  }
}

extension Optional where Wrapped == URL {
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
      return value
    case let .divAction(params):
      return params.source == .visibility ? nil : params.url
    case let .composite(lhs, rhs):
      let lhsURL = lhs.url
      let rhsURL = rhs.url
      assert(
        lhsURL == nil || rhsURL == nil,
        "Multiple URLs in payload are not supported"
      )
      return lhsURL ?? rhsURL
    case .empty,
         .menu,
         .json:
      return nil
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
    case let .composite(lPayload, rPayload):
      return try lPayload.getComposedURLs() + rPayload.getComposedURLs()
    case .empty:
      return []
    case .menu, .json:
      throw Error.notAnURL
    }
  }
}
