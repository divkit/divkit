import CoreGraphics
import VGSL

final class DefaultFontProvider: DivFontProvider {
  func font(family: String, weight: DivFontWeight, size: CGFloat) -> Font {
    let fontSpecifier = family == "display" ? fontSpecifiers.display : fontSpecifiers.text
    return fontSpecifier.font(weight: weight.vgslFontWeight, size: size)
  }
}

extension DivFontWeight {
  fileprivate var vgslFontWeight: FontWeight {
    switch self {
    case .light:
      .light
    case .regular:
      .regular
    case .medium:
      .medium
    case .bold:
      .bold
    }
  }
}
