import Foundation
import VGSL

public struct MarksConfigurationModel: Equatable {
  let minValue: CGFloat
  let maxValue: CGFloat
  let activeMark: RoundedRectangle
  let inactiveMark: RoundedRectangle
  let layoutDirection: UserInterfaceLayoutDirection

  public init(
    minValue: CGFloat,
    maxValue: CGFloat,
    activeMark: RoundedRectangle,
    inactiveMark: RoundedRectangle,
    layoutDirection: UserInterfaceLayoutDirection
  ) {
    self.minValue = minValue
    self.maxValue = maxValue
    self.activeMark = activeMark
    self.inactiveMark = inactiveMark
    self.layoutDirection = layoutDirection
  }

  public static let empty = Self(
    minValue: 0,
    maxValue: 0,
    activeMark: .empty,
    inactiveMark: .empty,
    layoutDirection: .leftToRight
  )
}

extension MarksConfigurationModel {
  public struct RoundedRectangle: Equatable {
    var size: CGSize
    let cornerRadius: CGFloat
    let color: Color
    let borderWidth: CGFloat
    let borderColor: Color

    public init(
      size: CGSize,
      cornerRadius: CGFloat,
      color: Color,
      borderWidth: CGFloat,
      borderColor: Color
    ) {
      self.size = size
      self.cornerRadius = cornerRadius
      self.color = color
      self.borderWidth = borderWidth
      self.borderColor = borderColor
    }

    public static let empty = RoundedRectangle(
      size: CGSize.zero,
      cornerRadius: 0,
      color: .clear,
      borderWidth: 0,
      borderColor: .clear
    )
  }
}
