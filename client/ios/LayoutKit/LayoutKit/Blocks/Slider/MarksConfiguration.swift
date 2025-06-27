import Foundation
import VGSL

public struct MarksConfiguration: Equatable {
  let modelConfiguration: MarksConfigurationModel
  let horizontalInset: CGFloat

  public static let empty = MarksConfiguration(
    modelConfiguration: .empty,
    horizontalInset: 0
  )
}
