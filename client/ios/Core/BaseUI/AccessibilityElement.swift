// Copyright 2020 Yandex LLC. All rights reserved.

public struct AccessibilityElement: Equatable, Codable {
  public enum Traits: String, Codable {
    case button
    case header
    case link
    case image
    case staticText
    case searchField
    case tabBar
    case switchButton
    case updatesFrequently
    case none
  }

  public struct Strings: Equatable, Codable {
    public let label: String?
    public let hint: String?
    public var value: String?
    public let identifier: String?

    public init(
      label: [String?] = [],
      hint: String? = nil,
      value: String? = nil,
      identifier: String? = nil
    ) {
      self.init(
        label: label.compactMap { $0 }.joined(separator: " "),
        hint: hint,
        value: value,
        identifier: identifier
      )
    }

    public init(
      label: String? = nil,
      hint: String? = nil,
      value: String? = nil,
      identifier: String? = nil
    ) {
      self.label = label
      self.hint = hint
      self.value = value
      self.identifier = identifier
    }

    public static let empty = Strings(label: "")
  }

  public let traits: Traits
  public var strings: Strings
  public var enabled: Bool
  public var selected: Bool
  public var startsMediaSession: Bool
  public var hideElementWithChildren: Bool

  public init(
    traits: Traits,
    strings: Strings,
    enabled: Bool = true,
    selected: Bool = false,
    startsMediaSession: Bool = false,
    hideElementWithChildren: Bool = false
  ) {
    self.traits = traits
    self.strings = strings
    self.enabled = enabled
    self.selected = selected
    self.hideElementWithChildren = hideElementWithChildren
    self.startsMediaSession = startsMediaSession
  }
}

extension AccessibilityElement {
  public static func button(label: String?) -> AccessibilityElement? {
    label.map {
      AccessibilityElement(
        traits: .button,
        strings: Strings(label: $0)
      )
    }
  }

  public static func button(label: String) -> AccessibilityElement {
    AccessibilityElement(
      traits: .button,
      strings: Strings(label: label)
    )
  }

  public static func button(
    strings: Strings?,
    startsMediaSession: Bool = false
  ) -> AccessibilityElement? {
    strings
      .map {
        AccessibilityElement(traits: .button, strings: $0, startsMediaSession: startsMediaSession)
      }
  }

  public static func button(
    strings: Strings,
    enabled: Bool = true,
    selected: Bool = false,
    startsMediaSession: Bool = false
  ) -> AccessibilityElement {
    AccessibilityElement(
      traits: .button,
      strings: strings,
      enabled: enabled,
      selected: selected,
      startsMediaSession: startsMediaSession
    )
  }

  public static func header(label: String?) -> AccessibilityElement? {
    label.map {
      AccessibilityElement(
        traits: .header,
        strings: Strings(label: $0)
      )
    }
  }

  public static func header(strings: Strings?) -> AccessibilityElement? {
    strings.map { AccessibilityElement(traits: .header, strings: $0) }
  }

  public static func link(label: String?) -> AccessibilityElement? {
    label.map {
      AccessibilityElement(
        traits: .link,
        strings: Strings(label: $0)
      )
    }
  }

  public static func link(strings: Strings?) -> AccessibilityElement? {
    strings.map { AccessibilityElement(traits: .link, strings: $0) }
  }

  public static func image(label: String?) -> AccessibilityElement? {
    label.map {
      AccessibilityElement(
        traits: .image,
        strings: Strings(label: $0)
      )
    }
  }

  public static func staticText(label: String?) -> AccessibilityElement? {
    label.map {
      AccessibilityElement(
        traits: .staticText,
        strings: Strings(label: $0)
      )
    }
  }

  public static func staticText(strings: Strings) -> AccessibilityElement {
    AccessibilityElement(traits: .staticText, strings: strings)
  }

  public static func tabBar(label: String?) -> AccessibilityElement? {
    label.map {
      AccessibilityElement(
        traits: .tabBar,
        strings: Strings(label: $0)
      )
    }
  }

  public static func none(label: String?) -> AccessibilityElement? {
    label.map {
      AccessibilityElement(
        traits: .none,
        strings: Strings(label: $0)
      )
    }
  }

  public static func none(strings: Strings, selected: Bool = false) -> AccessibilityElement {
    AccessibilityElement(traits: .none, strings: strings, selected: selected)
  }

  public static func switchButton(
    label: String,
    hint: String? = nil,
    isOn: Bool,
    isEnabled: Bool = true,
    identifier: String? = nil
  ) -> AccessibilityElement {
    AccessibilityElement(
      traits: .switchButton,
      strings: Strings(
        label: label,
        hint: hint,
        value: isOn ? "1" : "0",
        identifier: identifier
      ),
      enabled: isEnabled
    )
  }
}
