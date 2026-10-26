import CoreGraphics
import Foundation
import VGSL

public struct TabViewState: ElementState, Equatable {
  public static let `default` = TabViewState(selectedPageIndex: 0, countOfPages: 0)

  public let selectedPageIndex: CGFloat
  public let countOfPages: Int
  public let animated: Bool
  /// Identity distinguishes commands with the same destination.
  public let scrollRequest: ScrollRequest?

  public init(
    selectedPageIndex: CGFloat,
    countOfPages: Int,
    animated: Bool = true,
    scrollRequest: ScrollRequest? = nil
  ) {
    self.selectedPageIndex = selectedPageIndex
    self.countOfPages = countOfPages
    self.animated = animated
    self.scrollRequest = scrollRequest
  }

  public static func ==(lhs: TabViewState, rhs: TabViewState) -> Bool {
    lhs.selectedPageIndex == rhs.selectedPageIndex
      && lhs.countOfPages == rhs.countOfPages
      && lhs.animated == rhs.animated
      && lhs.scrollRequest == rhs.scrollRequest
  }
}
