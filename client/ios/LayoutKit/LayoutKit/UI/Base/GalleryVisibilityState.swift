import CoreGraphics
import Foundation

public struct GalleryVisibilityState: Equatable {
  public struct Item: Equatable {
    public let state: VisibilityState
    public let index: Int

    public init(state: VisibilityState, index: Int) {
      self.state = state
      self.index = index
    }
  }

  public let visibleItems: [Item]
  public let selectedItemIndex: CGFloat?
  public let isChangingContentOffsetDueToUserActions: Bool

  public init(
    visibleItems: [Item],
    selectedItemIndex: CGFloat?,
    isChangingContentOffsetDueToUserActions: Bool
  ) {
    self.visibleItems = visibleItems
    self.selectedItemIndex = selectedItemIndex
    self.isChangingContentOffsetDueToUserActions = isChangingContentOffsetDueToUserActions
  }

  public func intersected(with visibleBounds: CGRect) -> GalleryVisibilityState {
    let items: [Item] = visibleItems
      .map { .init(state: $0.state.intersected(with: visibleBounds), index: $0.index) }
      .filter { $0.state.visibility > 0 }
    return .init(
      visibleItems: items,
      selectedItemIndex: selectedItemIndex,
      isChangingContentOffsetDueToUserActions: isChangingContentOffsetDueToUserActions
    )
  }
}
