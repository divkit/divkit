import CoreGraphics
import VGSL

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

  func font(family: String, weight: Int, size: CGFloat) -> Font
}

extension DivFontProvider {
  public func font(weight: DivFontWeight = .regular, size: CGFloat) -> Font {
    font(family: "", weight: weight, size: size)
  }

  public func font(family: String, weight: Int, size: CGFloat) -> Font {
    let divFontWeight: DivFontWeight = switch weight {
    case 1..<350:
      .light
    case 350..<450:
      .regular
    case 450..<600:
      .medium
    case 600...Int.max:
      .bold
    default:
      .regular
    }
    return font(family: family, weight: divFontWeight, size: size)
  }
}
