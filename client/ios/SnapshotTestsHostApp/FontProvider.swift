import CoreGraphics
import UIKit

import BaseUI

final class FontProvider: FontSpecifying {
  func font(weight: FontWeight, size: CGFloat) -> UIFont {
    UIFont(name: weight.fontName, size: size)!
  }
}

extension FontWeight {
  fileprivate var fontName: String {
    switch self {
    case .light:
      return "YSText-Light"
    case .regular:
      return "YSText-Regular"
    case .medium, .semibold:
      return "YSText-Medium"
    case .bold:
      return "YSText-Bold"
    }
  }
}
