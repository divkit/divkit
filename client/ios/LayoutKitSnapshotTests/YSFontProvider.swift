import UIKit
import VGSL

final class YSFontProvider: FontSpecifying {
  func font(weight: FontWeight, size: CGFloat) -> UIFont {
    UIFont(name: weight.fontName, size: size)!
  }
}

extension FontWeight {
  fileprivate var fontName: String {
    switch self {
    case .light:
      "YSText-Light"
    case .regular:
      "YSText-Regular"
    case .medium, .semibold:
      "YSText-Medium"
    case .bold:
      "YSText-Bold"
    }
  }
}
