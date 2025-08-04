import CoreGraphics
import Foundation
import VGSL

public struct TabViewState: ElementState, Equatable {
  public static let `default` = TabViewState(selectedPageIndex: 0, countOfPages: 0)

  public let selectedPageIndex: CGFloat
  public let countOfPages: Int

  public init(selectedPageIndex: CGFloat, countOfPages: Int) {
    self.selectedPageIndex = selectedPageIndex
    self.countOfPages = countOfPages
  }
}
