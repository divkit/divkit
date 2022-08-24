import CoreGraphics
import UIKit

import BaseUI

final class YSFontProvider: FontSpecifying {
  static let mediumFontName = "YSText-Medium"
  static let regularFontName = "YSText-Regular"

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
      return YSFontProvider.regularFontName
    case .medium, .semibold:
      return YSFontProvider.mediumFontName
    case .bold:
      return "YSText-Bold"
    }
  }
}
