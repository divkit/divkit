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
    let scrollDuration = action.resolveDuration(expressionResolver).map { TimeInterval($0) / 1000 }
    let animated = action.resolveAnimated(expressionResolver) && scrollDuration != 0

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
          animated: animated,
          scrollDuration: scrollDuration
        )
      } else {
        scrollToNextItem(
          context: context,
          path: path,
          step: itemCount,
          overflow: overflow,
          animated: animated,
          scrollDuration: scrollDuration
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

    let scrollDuration = action.resolveDuration(expressionResolver).map { TimeInterval($0) / 1000 }
    let animated = action.resolveAnimated(expressionResolver) && scrollDuration != 0

    pathResolver.resolve(
      id: id,
      divTypes: Self.scrollableDivTypes,
      context: context
    ) { [self] path in
      scrollTo(
        action: action,
        path: path,
        context: context,
        animated: animated,
        scrollDuration: scrollDuration
      )
    }
  }

  func handleScrollAction(
    context: DivActionHandlingContext,
    id: String,
    scrollAction: DivActionIntent.Scroll,
    animated: Bool
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
          animated: animated
        )
      case let .setNextItem(step, overflow):
        scrollToNextItem(
          context: context,
          path: path,
          step: step,
          overflow: overflow,
          animated: animated
        )
      case let .setPreviousItem(step, overflow):
        scrollToNextItem(
          context: context,
          path: path,
          step: -step,
          overflow: overflow,
          animated: animated
        )
      case let .scroll(mode):
        switch mode {
        case .start:
          scrollToStart(
            context: context,
            path: path,
            animated: animated
          )
        case .end:
          scrollToEnd(
            context: context,
            path: path,
            animated: animated
          )
        case let .forward(offset, overflow):
          scrollToOffset(
            context: context,
            path: path,
            offset: offset,
            isRelative: true,
            overflow: overflow,
            animated: animated
          )
        case let .backward(offset, overflow):
          scrollToOffset(
            context: context,
            path: path,
            offset: -offset,
            isRelative: true,
            overflow: overflow,
            animated: animated
          )
        case let .position(position):
          scrollToOffset(
            context: context,
            path: path,
            offset: position,
            animated: animated
          )
        }
      }
    }
  }

  private func scrollTo(
    action: DivActionScrollTo,
    path: UIElementPath,
    context: DivActionHandlingContext,
    animated: Bool,
    scrollDuration: TimeInterval?
  ) {
    let expressionResolver = context.expressionResolver
    switch action.destination {
    case let .indexDestination(destination):
      if let index = destination.resolveValue(expressionResolver) {
        scrollToItem(
          context: context,
          path: path,
          index: index,
          animated: animated,
          scrollDuration: scrollDuration
        )
      }
    case let .offsetDestination(destination):
      if let value = destination.resolveValue(expressionResolver) {
        scrollToOffset(
          context: context,
          path: path,
          offset: value,
          animated: animated,
          scrollDuration: scrollDuration
        )
      }
    case .startDestination:
      scrollToStart(
        context: context,
        path: path,
        animated: animated,
        scrollDuration: scrollDuration
      )
    case .endDestination:
      scrollToEnd(
        context: context,
        path: path,
        animated: animated,
        scrollDuration: scrollDuration
      )
    case .itemIdDestination:
      DivKitLogger.error("Scroll action destination not supported: item_id")
    }
  }

  private func scrollToItem(
    context: DivActionHandlingContext,
    path: UIElementPath,
    index: Int,
    animated: Bool,
    scrollDuration: TimeInterval? = nil
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
      animated: animated,
      scrollDuration: scrollDuration
    )
  }

  private func scrollToNextItem(
    context: DivActionHandlingContext,
    path: UIElementPath,
    step: Int,
    overflow: OverflowMode,
    animated: Bool,
    scrollDuration: TimeInterval? = nil
  ) {
    guard let state = getState(path) else {
      DivKitLogger.error("Unexpected state type for \(path)")
      return
    }

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
      clampedIndex: state.currentItemIndex,
      animated: animated,
      scrollDuration: scrollDuration,
      direction: direction,
      scrollDelta: .items(step, wraps: overflow == .ring)
    )
  }

  private func scrollToOffset(
    context: DivActionHandlingContext,
    path: UIElementPath,
    offset: Int,
    isRelative: Bool = false,
    overflow: OverflowMode = .clamp,
    animated: Bool,
    scrollDuration: TimeInterval? = nil
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

    let position = isRelative ? currentPosition : clamp(CGFloat(offset), min: 0, max: max(0, range))

    blockStateStorage.setPendingState(
      path,
      state: GalleryViewState(
        contentPosition: .offset(position),
        itemsCount: state.itemsCount,
        isScrolling: false,
        scrollRange: range,
        animated: animated,
        scrollRequest: ScrollRequest(
          duration: scrollDuration,
          destination: isRelative ? .relative(.offset(CGFloat(offset), wraps: overflow == .ring))
            : .offset(position),
          preceding: (blockStateStorage.peekPendingState(path) as? GalleryViewState)?.scrollRequest
            ?? state.scrollRequest
        )
      )
    )
    updateCard(.state(cardId))
  }

  private func scrollToStart(
    context: DivActionHandlingContext,
    path: UIElementPath,
    animated: Bool,
    scrollDuration: TimeInterval? = nil
  ) {
    // should update offset for gallery
    if let state: GalleryViewState = blockStateStorage.getState(path),
       case .offset = state.contentPosition {
      scrollToOffset(
        context: context,
        path: path,
        offset: 0,
        animated: animated,
        scrollDuration: scrollDuration
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
      animated: animated,
      scrollDuration: scrollDuration
    )
  }

  private func scrollToEnd(
    context: DivActionHandlingContext,
    path: UIElementPath,
    animated: Bool,
    scrollDuration: TimeInterval? = nil
  ) {
    // should update offset for gallery
    if let state: GalleryViewState = blockStateStorage.getState(path),
       case .offset = state.contentPosition {
      if let scrollRange = state.scrollRange {
        scrollToOffset(
          context: context,
          path: path,
          offset: Int(scrollRange),
          animated: animated,
          scrollDuration: scrollDuration
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
      animated: animated,
      scrollDuration: scrollDuration
    )
  }

  private func getState(_ path: UIElementPath) -> CollectionTypeViewState? {
    blockStateStorage.getStateUntyped(path) as? CollectionTypeViewState
  }

  private func scrollToItemInternal(
    state: CollectionTypeViewState,
    context: DivActionHandlingContext,
    path: UIElementPath,
    clampedIndex: Int,
    animated: Bool,
    scrollDuration: TimeInterval? = nil,
    direction: ScrollNavigationDirection = .none,
    scrollDelta: ScrollDelta? = nil
  ) {
    blockStateStorage.setPendingState(
      path,
      state: state.makeState(
        index: clampedIndex,
        animated: animated,
        direction: direction,
        scrollRequest: ScrollRequest(
          duration: scrollDuration,
          destination: scrollDelta.map { .relative($0) } ?? .item(CGFloat(clampedIndex)),
          preceding: (blockStateStorage.peekPendingState(path) as? CollectionTypeViewState)?
            .scrollRequest ?? state.scrollRequest
        )
      )
    )

    updateCard(.state(context.cardId))
  }
}

protocol CollectionTypeViewState: ElementState {
  var itemsCount: Int { get }
  var currentItemIndex: Int { get }
  var animated: Bool { get }
  var scrollRequest: ScrollRequest? { get }

  func makeState(
    index: Int,
    animated: Bool,
    direction: ScrollNavigationDirection,
    scrollRequest: ScrollRequest?
  ) -> Self
}

extension CollectionTypeViewState {
  func normalizeIndex(value: Int) -> Int {
    clamp(value, min: 0, max: max(0, itemsCount - 1))
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

  func makeState(
    index: Int,
    animated: Bool,
    direction _: ScrollNavigationDirection,
    scrollRequest: ScrollRequest?
  ) -> GalleryViewState {
    GalleryViewState(
      contentPosition: .paging(index: CGFloat(index)),
      itemsCount: itemsCount,
      isScrolling: false,
      scrollRange: scrollRange,
      animated: animated,
      scrollRequest: scrollRequest
    )
  }
}

extension PagerViewState: CollectionTypeViewState {
  var itemsCount: Int {
    numberOfPages
  }

  var currentItemIndex: Int {
    Int(currentPage)
  }

  func makeState(
    index: Int,
    animated: Bool,
    direction: ScrollNavigationDirection,
    scrollRequest: ScrollRequest?
  ) -> PagerViewState {
    PagerViewState(
      numberOfPages: itemsCount,
      currentPage: index,
      animated: animated,
      scrollRequest: scrollRequest,
      isInfiniteScrollable: self.isInfiniteScrollable,
      navigationDirection: direction
    )
  }
}

extension TabViewState: CollectionTypeViewState {
  var itemsCount: Int {
    countOfPages
  }

  var currentItemIndex: Int {
    Int(selectedPageIndex)
  }

  func makeState(
    index: Int,
    animated: Bool,
    direction _: ScrollNavigationDirection,
    scrollRequest: ScrollRequest?
  ) -> TabViewState {
    TabViewState(
      selectedPageIndex: CGFloat(index),
      countOfPages: itemsCount,
      animated: animated,
      scrollRequest: scrollRequest
    )
  }
}
