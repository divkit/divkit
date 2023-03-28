// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics

import BaseTinyPublic

public protocol FontSpecifying: AnyObject {
  func font(weight: FontWeight, size: CGFloat) -> Font
}

public class FontSpecifiers {
  public let text: FontSpecifying
  public let display: FontSpecifying

  public init(
    text: FontSpecifying,
    display: FontSpecifying
  ) {
    self.text = text
    self.display = display
  }

  public func font(family: FontFamily, weight: FontWeight, size: CGFloat) -> Font {
    switch family {
    case .YSDisplay:
      return display.font(weight: weight, size: size)
    case .YSText:
      return text.font(weight: weight, size: size)
    }
  }
}

extension FontSpecifiers {
  public func textFont(weight: FontWeight, size: CGFloat) -> Font {
    font(family: .YSText, weight: weight, size: size)
  }
}
