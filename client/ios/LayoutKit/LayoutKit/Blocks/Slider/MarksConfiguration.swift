import Foundation
import VGSL

public struct MarksConfiguration: Equatable {
  public static let empty = MarksConfiguration(
    modelConfiguration: .empty,
    horizontalInset: 0
  )

  let modelConfiguration: MarksConfigurationModel
  let horizontalInset: CGFloat
}
