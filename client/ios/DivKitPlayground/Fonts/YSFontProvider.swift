import DivKit
import UIKit

final class YSFontProvider: DivFontProvider {
  static let mediumFontName = "YSText-Medium"
  static let regularFontName = "YSText-Regular"

  func font(family: String, weight: DivFontWeight, size: CGFloat) -> UIFont {
    if family == robotoFlexFont {
      return UIFont(name: "RobotoFlex-Regular", size: size)!
    }
    return UIFont(name: weight.fontName, size: size)!
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

private let robotoFlexFont = "roboto_flex"
