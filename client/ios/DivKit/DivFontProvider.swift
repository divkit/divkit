import CoreGraphics

import BasePublic
import BaseUIPublic

public protocol DivFontProvider {
  func font(family: String, weight: DivFontWeight, size: CGFloat) -> Font
}

extension DivFontProvider {
  public func font(weight: DivFontWeight = .regular, size: CGFloat) -> Font {
    font(family: "", weight: weight, size: size)
  }
}

final class DefaultFontProvider: DivFontProvider {
  func font(family: String, weight: DivFontWeight, size: CGFloat) -> Font {
    let fontSpecifier = family == "display" ? fontSpecifiers.display : fontSpecifiers.text
    return fontSpecifier.font(weight: weight.baseUIFontWeight, size: size)
  }
}

extension DivFontWeight {
  fileprivate var baseUIFontWeight: BaseUIPublic.FontWeight {
    switch self {
    case .light:
      return .light
    case .regular:
      return .regular
    case .medium:
      return .medium
    case .bold:
      return .bold
    }
  }
}
