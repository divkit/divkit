// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics

import BaseTinyPublic

public var fontSpecifiers = FontSpecifiers(
  text: systemFontSpecifier,
  display: systemFontSpecifier
)

private let systemFontSpecifier = SystemFontSpecifier()

private final class SystemFontSpecifier: FontSpecifying {
  func font(weight: FontWeight, size: CGFloat) -> Font {
    .systemFont(ofSize: size, weight: weight.cast())
  }
}

extension FontWeight {
  fileprivate func cast() -> Font.Weight {
    switch self {
    case .bold:
      return .bold
    case .semibold:
      return .semibold
    case .medium:
      return .medium
    case .regular:
      return .regular
    case .light:
      return .light
    }
  }
}
