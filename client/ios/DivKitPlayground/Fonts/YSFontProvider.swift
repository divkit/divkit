import DivKit
import UIKit

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
      "YSText-Light"
    case .regular:
      YSFontProvider.regularFontName
    case .medium:
      YSFontProvider.mediumFontName
    case .bold:
      "YSText-Bold"
    }
  }
}
