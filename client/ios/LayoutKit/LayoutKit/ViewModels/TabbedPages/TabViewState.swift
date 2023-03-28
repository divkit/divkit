import CoreGraphics
import Foundation

import CommonCorePublic

public struct TabViewState: ElementState, Equatable {
  public let selectedPageIndex: CGFloat
  public let countOfPages: Int

  public static let `default` = TabViewState(selectedPageIndex: 0, countOfPages: 0)

  public init(selectedPageIndex: CGFloat, countOfPages: Int) {
    self.selectedPageIndex = selectedPageIndex
    self.countOfPages = countOfPages
  }
}
