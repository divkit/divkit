import UIKit

import BaseUIPublic
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
      return "YSText-Light"
    case .regular:
      return "YSText-Regular"
    case .medium:
      return "YSText-Medium"
    case .bold:
      return "YSText-Bold"
    }
  }
}
