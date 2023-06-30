import UIKit

import DivKit

final class YSFontProvider: DivFontProvider {
  static let mediumFontName = "YSText-Medium"
  static let regularFontName = "YSText-Regular"

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
      return YSFontProvider.regularFontName
    case .medium:
      return YSFontProvider.mediumFontName
    case .bold:
      return "YSText-Bold"
    }
  }
}
