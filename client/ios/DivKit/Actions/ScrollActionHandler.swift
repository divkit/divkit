import Foundation
import LayoutKit
import VGSL

final class ScrollActionHandler {
  private static let scrollableDivTypes: Set<String> = [
    DivGallery.type,
    DivPager.type,
    DivTabs.type,
  ]

  private let blockStateStorage: DivBlockStateStorage
  private let updateCard: DivActionHandler.UpdateCardAction
  private let pathResolver: ActionPathResolver

  init(
    blockStateStorage: DivBlockStateStorage,
    pathResolver: ActionPathResolver,
    updateCard: @escaping DivActionHandler.UpdateCardAction
  ) {
    self.blockStateStorage = blockStateStorage
    self.updateCard = updateCard
    self.pathResolver = pathResolver
  }

  func handle(
    _ action: DivActionScrollBy,
    context: DivActionHandlingContext
  ) {
    let expressionResolver = context.expressionResolver
    guard let id = action.resolveId(expressionResolver) else {
      return
    }

    let overflow: OverflowMode = switch action.resolveOverflow(expressionResolver) {
    case .clamp: .clamp
    case .ring: .ring
    }

    let itemCount = action.resolveItemCount(expressionResolver)
    let animated = action.resolveAnimated(expressionResolver)

    pathResolver.resolve(
      id: id,
      divTypes: Self.scrollableDivTypes,
      context: context
    ) { [self] path in
      if itemCount == 0 {
        let offset = action.resolveOffset(expressionResolver)
        scrollToOffset(
          context: context,
          path: path,
          offset: offset,
          isRelative: true,
          overflow: overflow,
          animated: animated
        )
      } else {
        scrollToNextItem(
          context: context,
          path: path,
          step: itemCount,
          overflow: overflow,
          animated: animated
        )
      }
    }
  }

  func handle(
    _ action: DivActionScrollTo,
    context: DivActionHandlingContext
  ) {
    let expressionResolver = context.expressionResolver
    guard let id = action.resolveId(expressionResolver) else {
      return
    }

    let animated = action.resolveAnimated(expressionResolver)

    pathResolver.resolve(
      id: id,
      divTypes: Self.scrollableDivTypes,
      context: context
    ) { [self] path in
      switch action.destination {
      case let .indexDestination(destination):
        if let index = destination.resolveValue(expressionResolver) {
          scrollToItem(
            context: context,
            path: path,
            index: index,
            animated: animated
          )
        }
      case let .offsetDestination(destination):
        if let value = destination.resolveValue(expressionResolver) {
          scrollToOffset(
            context: context,
            path: path,
            offset: value,
            animated: animated
          )
        }
      case .startDestination:
        scrollToStart(
          context: context,
          path: path,
          animated: animated
        )
      case .endDestination:
        scrollToEnd(
          context: context,
          path: path,
          animated: animated
        )
      }
    }
  }

  func handleScrollAction(
    context: DivActionHandlingContext,
    id: String,
    scrollAction: DivActionIntent.Scroll
  ) {
    pathResolver.resolve(
      id: id,
      divTypes: Self.scrollableDivTypes,
      context: context
    ) { [self] path in
      switch scrollAction {
      case let .setCurrentItem(index):
        scrollToItem(
          context: context,
          path: path,
          index: index,
          animated: true
        )
      case let .setNextItem(step, overflow):
        scrollToNextItem(
          context: context,
          path: path,
          step: step,
          overflow: overflow,
          animated: true
        )
      case let .setPreviousItem(step, overflow):
        scrollToNextItem(
          context: context,
          path: path,
          step: -step,
          overflow: overflow,
          animated: true
        )
      case let .scroll(mode):
        switch mode {
        case .start:
          scrollToStart(
            context: context,
            path: path,
            animated: true
          )
        case .end:
          scrollToEnd(
            context: context,
            path: path,
            animated: true
          )
        case let .forward(offset, overflow):
          scrollToOffset(
            context: context,
            path: path,
            offset: offset,
            isRelative: true,
            overflow: overflow,
            animated: true
          )
        case let .backward(offset, overflow):
          scrollToOffset(
            context: context,
            path: path,
            offset: -offset,
            isRelative: true,
            overflow: overflow,
            animated: true
          )
        case let .position(position):
          scrollToOffset(
            context: context,
            path: path,
            offset: position,
            animated: true
          )
        }
      }
    }
  }

  private func scrollToItem(
    context: DivActionHandlingContext,
    path: UIElementPath,
    index: Int,
    animated: Bool
  ) {
    guard let state = getState(path) else {
      DivKitLogger.error("Unexpected state type for \(path)")
      return
    }

    let clampedIndex = state.normalizeIndex(value: index)

    guard index == clampedIndex else { return }

    scrollToItemInternal(
      state: state,
      context: context,
      path: path,
      clampedIndex: clampedIndex,
      animated: animated
    )
  }

  private func scrollToNextItem(
    context: DivActionHandlingContext,
    path: UIElementPath,
    step: Int,
    overflow: OverflowMode,
    animated: Bool
  ) {
    guard let state = getState(path) else {
      DivKitLogger.error("Unexpected state type for \(path)")
      return
    }

    let index = state.normalizeCurrentIndex(overflow: overflow, withIncrement: step)

    let direction: ScrollNavigationDirection = if step > 0 {
      .forward
    } else if step < 0 {
      .backward
    } else {
      .none
    }

    scrollToItemInternal(
      state: state,
      context: context,
      path: path,
      clampedIndex: index,
      animated: animated,
      direction: direction
    )
  }

  private func scrollToOffset(
    context: DivActionHandlingContext,
    path: UIElementPath,
    offset: Int,
    isRelative: Bool = false,
    overflow: OverflowMode = .clamp,
    animated: Bool
  ) {
    let cardId = context.cardId
    guard let state: GalleryViewState = blockStateStorage.getState(path),
          case let .offset(currentPosition, _) = state.contentPosition else {
      DivKitLogger.error("Unexpected state type for \(path)")
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

    blockStateStorage.setPendingState(
      path,
      state: GalleryViewState(
        contentPosition: .offset(position),
        itemsCount: state.itemsCount,
        isScrolling: state.isScrolling,
        scrollRange: range,
        animated: animated
      )
    )
    updateCard(.state(cardId))
  }

  private func scrollToStart(
    context: DivActionHandlingContext,
    path: UIElementPath,
    animated: Bool
  ) {
    // should update offset for gallery
    if let state: GalleryViewState = blockStateStorage.getState(path),
       case .offset = state.contentPosition {
      scrollToOffset(
        context: context,
        path: path,
        offset: 0,
        animated: animated
      )
      return
    }

    guard let state = getState(path) else {
      DivKitLogger.error("Unexpected state type for \(path)")
      return
    }

    scrollToItemInternal(
      state: state,
      context: context,
      path: path,
      clampedIndex: 0,
      animated: animated
    )
  }

  private func scrollToEnd(
    context: DivActionHandlingContext,
    path: UIElementPath,
    animated: Bool
  ) {
    // should update offset for gallery
    if let state: GalleryViewState = blockStateStorage.getState(path),
       case .offset = state.contentPosition {
      if let scrollRange = state.scrollRange {
        scrollToOffset(
          context: context,
          path: path,
          offset: Int(scrollRange),
          animated: animated
        )
      }
      return
    }

    guard let state = getState(path) else {
      DivKitLogger.error("Unexpected state type for \(path)")
      return
    }
    scrollToItemInternal(
      state: state,
      context: context,
      path: path,
      clampedIndex: state.itemsCount - 1,
      animated: animated
    )
  }

  private func getState(_ path: UIElementPath) -> CollectionTypeViewState? {
    blockStateStorage.getStateUntyped(path) as? CollectionTypeViewState
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
    overflow: OverflowMode,
    isInfiniteScrollable: Bool
  ) -> Int {
    guard size > 0 else {
      return 0
    }

    switch overflow {
    case .ring:
      let remainder = value % size
      return if value > 0 {
        remainder
      } else if remainder == 0 {
        0
      } else {
        size + remainder
      }
    case .clamp:
      return if isInfiniteScrollable {
        value
      } else {
        max(0, min(value, size - 1))
      }
    }
  }

  private func scrollToItemInternal(
    state: CollectionTypeViewState,
    context: DivActionHandlingContext,
    path: UIElementPath,
    clampedIndex: Int,
    animated: Bool,
    direction: ScrollNavigationDirection = .none
  ) {
    blockStateStorage.setPendingState(
      path,
      state: state.makeState(
        clampedIndex,
        animated,
        direction
      )
    )

    updateCard(.state(context.cardId))
  }
}

protocol CollectionTypeViewState: ElementState {
  var itemsCount: Int { get }
  var currentItemIndex: Int { get }
  var animated: Bool { get }

  var makeState: (_ clampedIndex: Int, _ animated: Bool, _ direction: ScrollNavigationDirection)
    -> Self { get }
  func normalizeCurrentIndex(overflow: OverflowMode, withIncrement increment: Int) -> Int
}

extension CollectionTypeViewState {
  func normalizeIndex(value: Int, overflow: OverflowMode = .clamp) -> Int {
    switch overflow {
    case .ring:
      let remainder = value % itemsCount
      return if value > 0 {
        remainder
      } else if remainder == 0 {
        0
      } else {
        itemsCount + remainder
      }
    case .clamp:
      return clamp(
        value,
        min: 0,
        max: max(0, itemsCount - 1)
      )
    }
  }

  func normalizeCurrentIndex(overflow: OverflowMode, withIncrement increment: Int = 0) -> Int {
    normalizeIndex(
      value: currentItemIndex + increment,
      overflow: overflow
    )
  }
}

extension GalleryViewState: CollectionTypeViewState {
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

  var makeState: (_ clampedIndex: Int, _ animated: Bool, _ direction: ScrollNavigationDirection)
    -> GalleryViewState {
    { clampedIndex, animated, _ in
      GalleryViewState(
        contentPosition: .paging(index: CGFloat(clampedIndex)),
        itemsCount: itemsCount,
        isScrolling: isScrolling,
        scrollRange: scrollRange,
        animated: animated
      )
    }
  }
}

extension PagerViewState: CollectionTypeViewState {
  var itemsCount: Int {
    numberOfPages
  }

  var currentItemIndex: Int {
    Int(currentPage)
  }

  var makeState: (_ clampedIndex: Int, _ animated: Bool, _ direction: ScrollNavigationDirection)
    -> PagerViewState {
    { clampedIndex, animated, direction in
      PagerViewState(
        numberOfPages: itemsCount,
        currentPage: clampedIndex,
        animated: animated,
        isInfiniteScrollable: self.isInfiniteScrollable,
        navigationDirection: direction
      )
    }
  }

  func normalizeCurrentIndex(overflow: OverflowMode, withIncrement increment: Int = 0) -> Int {
    let value = currentItemIndex + increment
    if isInfiniteScrollable {
      // Storage keeps real page indices 0 ..< numberOfPages only. Unchecked `value` used to write
      // out-of-range pages; always using caller `overflow` with `.clamp` pinned the last page so
      // `scroll_to` / scroll-by actions never advanced the carousel. Ring matches infinite pager
      // UX.
      return normalizeIndex(value: value, overflow: .ring)
    }
    return normalizeIndex(value: value, overflow: overflow)
  }
}

extension TabViewState: CollectionTypeViewState {
  // Animation for Tabs is not supported
  var animated: Bool { true }

  var itemsCount: Int {
    countOfPages
  }

  var currentItemIndex: Int {
    Int(selectedPageIndex)
  }

  var makeState: (_ clampedIndex: Int, _ animated: Bool, _ direction: ScrollNavigationDirection)
    -> TabViewState {
    { clampedIndex, _, _ in
      TabViewState(
        selectedPageIndex: CGFloat(clampedIndex),
        countOfPages: itemsCount
      )
    }
  }
}
