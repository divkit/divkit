import BaseTinyPublic
import Foundation

public struct MarksConfiguration: Equatable {
  let minValue: CGFloat
  let maxValue: CGFloat
  var horizontalInset: CGFloat = 0
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

  public static let empty = MarksConfiguration(
    minValue: 0,
    maxValue: 0,
    activeMark: .empty,
    inactiveMark: .empty,
    layoutDirection: .leftToRight
  )
}

extension MarksConfiguration {
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

  public static func ==(lhs: MarksConfiguration, rhs: MarksConfiguration) -> Bool {
    lhs.minValue == rhs.minValue &&
      lhs.maxValue == rhs.maxValue &&
      lhs.horizontalInset == rhs.horizontalInset &&
      lhs.inactiveMark == rhs.inactiveMark &&
      lhs.activeMark == rhs.activeMark &&
      lhs.layoutDirection == rhs.layoutDirection
  }
}
