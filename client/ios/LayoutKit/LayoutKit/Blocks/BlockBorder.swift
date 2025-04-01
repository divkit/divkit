import CoreGraphics
import Foundation
import VGSL

public struct BlockBorder: Equatable {
  public enum Style {
    case solid
    case dashed

    public static let `default` = BlockBorder.Style.solid
  }

  public let style: Style
  public let color: Color
  public let width: CGFloat

  public init(
    style: Style = .default,
    color: Color,
    width: CGFloat = 1
  ) {
    self.style = style
    self.color = color
    self.width = width
  }
}
