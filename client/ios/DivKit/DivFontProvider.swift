import CoreGraphics

import BasePublic
import BaseUIPublic

/// The `DivFontProvider` protocol is used to enable the transfer of custom fonts used in your
/// application. By implementing this protocol, you can specify a custom font provider object that
/// allows `DivKit` to access and utilize your desired fonts.
///
/// The `font(family:weight:size:)` method should be implemented to provide the appropriate `Font`
/// based on the specified font family, weight, and size.
///
/// By default, when a custom `DivFontProvider` is not specified, `DivKit` utilizes system fonts for
/// rendering text.
public protocol DivFontProvider {
  /// Retrieves a custom `Font` based on the specified font family, weight, and size.
  ///
  /// - Parameters:
  ///   - family: The font family.
  ///   - weight: The font weight.
  ///   - size: The font size.
  ///
  /// - Returns: A `Font` that matches the specified font family, weight, and size.
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
