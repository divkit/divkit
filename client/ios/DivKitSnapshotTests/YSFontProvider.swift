import UIKit

import DivKit

final class YSFontProvider: DivFontProvider {
  func font(family _: String, weight: DivFontWeight, size: CGFloat) -> UIFont {
    UIFont(name: weight.fontName, size: size)!
  }
}

extension DivFontWeight {
  fileprivate var fontName: String {
    switch self {
    case .light:
      "YSText-Light"
    case .regular:
      "YSText-Regular"
    case .medium:
      "YSText-Medium"
    case .bold:
      "YSText-Bold"
    }
  }
}
