import Foundation
import LayoutKit
import VGSL

final class ScrollActionHandler {
  private let blockStateStorage: DivBlockStateStorage
  private let updateCard: DivActionHandler.UpdateCardAction

  init(
    blockStateStorage: DivBlockStateStorage,
    updateCard: @escaping DivActionHandler.UpdateCardAction
  ) {
    self.blockStateStorage = blockStateStorage
    self.updateCard = updateCard
  }

  func handle(_ action: DivActionScrollBy, context: DivActionHandlingContext) {
    let expressionResolver = context.expressionResolver
    guard let id = action.resolveId(expressionResolver) else {
      return
    }

    let overflow: OverflowMode = switch action.resolveOverflow(expressionResolver) {
    case .clamp: .clamp
    case .ring: .ring
    }

    let itemCount = action.resolveItemCount(expressionResolver)
    let cardId = context.path.cardId
    if itemCount == 0 {
      let offset = action.resolveOffset(expressionResolver)
      scrollToOffset(
        cardId: cardId,
        id: id,
        offset: offset,
        isRelative: true,
        overflow: overflow
      )
    } else {
      scrollToNextItem(cardId: cardId, id: id, step: itemCount, overflow: overflow)
    }
  }

  func handle(_ action: DivActionScrollTo, context: DivActionHandlingContext) {
    let expressionResolver = context.expressionResolver
    guard let id = action.resolveId(expressionResolver) else {
      return
    }

    let cardId = context.path.cardId
    switch action.destination {
    case let .indexDestination(destination):
      if let index = destination.resolveValue(expressionResolver) {
        scrollToItem(cardId: cardId, id: id, index: index)
      }
    case let .offsetDestination(destination):
      if let value = destination.resolveValue(expressionResolver) {
        scrollToOffset(cardId: cardId, id: id, offset: value)
      }
    case .startDestination:
      scrollToStart(cardId: cardId, id: id)
    case .endDestination:
      scrollToEnd(cardId: cardId, id: id)
    }
  }

  func scrollToItem(cardId: DivCardID, id: String, index: Int) {
    guard let state = getState(cardId: cardId, id: id) else {
      DivKitLogger.error("Unexpected state type for \(id)")
      return
    }

    let clampedIndex = clamp(index, min: 0, max: max(0, state.itemsCount - 1))
    guard clampedIndex == index else {
      return
    }

    blockStateStorage.setState(
      id: id,
      cardId: cardId,
      state: state.makeState(clampedIndex)
    )

    updateCard(.state(cardId))
  }

  func scrollToNextItem(
    cardId: DivCardID,
    id: String,
    step: Int,
    overflow: OverflowMode
  ) {
    guard let state = getState(cardId: cardId, id: id) else {
      DivKitLogger.error("Unexpected state type for \(id)")
      return
    }

    let index = normalizeIndex(
      value: state.currentItemIndex + step,
      size: state.itemsCount,
      overflow: overflow
    )
    scrollToItem(cardId: cardId, id: id, index: index)
  }

  func scrollToOffset(
    cardId: DivCardID,
    id: String,
    offset: Int,
    isRelative: Bool = false,
    overflow: OverflowMode = .clamp
  ) {
    guard let state: GalleryViewState = blockStateStorage.getState(id, cardId: cardId),
          case let .offset(currentPosition, _) = state.contentPosition else {
      DivKitLogger.error("Unexpected state type for \(id)")
      return
    }

    guard let range = state.scrollRange else {
      return
    }

    let position = normalizeOffset(
      value: isRelative ? currentPosition + CGFloat(offset) : CGFloat(offset),
      size: range,
      overflow: overflow
    )
    blockStateStorage.setState(
      id: id,
      cardId: cardId,
      state: GalleryViewState(
        contentPosition: .offset(position),
        itemsCount: state.itemsCount,
        isScrolling: state.isScrolling,
        scrollRange: range
      )
    )
    updateCard(.state(cardId))
  }

  func scrollToStart(cardId: DivCardID, id: String) {
    // should update offset for gallery
    if let state: GalleryViewState = blockStateStorage.getState(id, cardId: cardId),
       case .offset = state.contentPosition {
      scrollToOffset(cardId: cardId, id: id, offset: 0)
      return
    }

    guard getState(cardId: cardId, id: id) != nil else {
      DivKitLogger.error("Unexpected state type for \(id)")
      return
    }
    scrollToItem(cardId: cardId, id: id, index: 0)
  }

  func scrollToEnd(cardId: DivCardID, id: String) {
    // should update offset for gallery
    if let state: GalleryViewState = blockStateStorage.getState(id, cardId: cardId),
       case .offset = state.contentPosition {
      if let scrollRange = state.scrollRange {
        scrollToOffset(cardId: cardId, id: id, offset: Int(scrollRange))
      }
      return
    }

    guard let state = getState(cardId: cardId, id: id) else {
      DivKitLogger.error("Unexpected state type for \(id)")
      return
    }
    scrollToItem(cardId: cardId, id: id, index: state.itemsCount - 1)
  }

  private func getState(cardId: DivCardID, id: String) -> GalleryTypeViewState? {
    blockStateStorage.getStateUntyped(id, cardId: cardId) as? GalleryTypeViewState
  }

  private func normalizeOffset(
    value: CGFloat,
    size: CGFloat,
    overflow: OverflowMode
  ) -> CGFloat {
    guard size > 0 else {
      return 0
    }
    switch overflow {
    case .ring:
      if value > 0 {
        return value.truncatingRemainder(dividingBy: size)
      }
      let remainder = (-value).truncatingRemainder(dividingBy: size)
      return remainder == 0 ? 0 : size - remainder
    case .clamp:
      return max(0, min(value, size))
    }
  }

  private func normalizeIndex(
    value: Int,
    size: Int,
    overflow: OverflowMode
  ) -> Int {
    guard size > 0 else {
      return 0
    }
    switch overflow {
    case .ring:
      if value > 0 {
        return value % size
      }
      let remainder = -value % size
      return remainder == 0 ? 0 : size - remainder
    case .clamp:
      return max(0, min(value, size - 1))
    }
  }
}

fileprivate protocol GalleryTypeViewState: ElementState {
  var itemsCount: Int { get }
  var currentItemIndex: Int { get }

  var makeState: (_ clampedIndex: Int) -> Self { get }
}

extension GalleryViewState: GalleryTypeViewState {
  var currentItemIndex: Int {
    switch contentPosition {
    case let .offset(_, firstVisibleItemsIndex):
      firstVisibleItemsIndex
    case let .paging(index):
      Int(index)
    }
  }

  var currentOffset: CGFloat {
    switch contentPosition {
    case let .offset(value, _):
      return value
    case let .paging(index):
      guard let scrollRange, itemsCount > 0 else {
        return 0
      }
      return scrollRange / CGFloat(itemsCount - 1) * index
    }
  }

  var makeState: (_ clampedIndex: Int) -> GalleryViewState {
    { clampedIndex in
      GalleryViewState(
        contentPosition: .paging(index: CGFloat(clampedIndex)),
        itemsCount: itemsCount,
        isScrolling: isScrolling,
        scrollRange: scrollRange
      )
    }
  }
}

extension PagerViewState: GalleryTypeViewState {
  var itemsCount: Int {
    numberOfPages
  }

  var currentItemIndex: Int {
    Int(currentPage)
  }

  var makeState: (_ clampedIndex: Int) -> PagerViewState {
    { clampedIndex in
      PagerViewState(
        numberOfPages: itemsCount,
        currentPage: clampedIndex
      )
    }
  }
}

extension TabViewState: GalleryTypeViewState {
  var itemsCount: Int {
    countOfPages
  }

  var currentItemIndex: Int {
    Int(selectedPageIndex)
  }

  var makeState: (_ clampedIndex: Int) -> TabViewState {
    { clampedIndex in
      TabViewState(
        selectedPageIndex: CGFloat(clampedIndex),
        countOfPages: itemsCount
      )
    }
  }
}
