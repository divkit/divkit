import CoreGraphics
import Foundation

import CommonCorePublic
import LayoutKit

public final class DivActionURLHandler {
  public typealias UpdateCardAction = (UpdateReason) -> Void
  public typealias ShowTooltipAction = (TooltipInfo) -> Void
  public typealias PerformTimerAction = (
    _ cardId: DivCardID,
    _ timerId: String,
    _ action: DivTimerAction
  ) -> Void

  @frozen
  public enum UpdateReason {
    public enum AffectedCards: Equatable {
      case all
      case specific(Set<DivCardID>)
    }

    case patch(DivCardID, DivPatch)
    case timer(DivCardID)
    case state(DivCardID)
    case variable(AffectedCards)
  }

  private let stateUpdater: DivStateUpdater
  private let blockStateStorage: DivBlockStateStorage
  private let patchProvider: DivPatchProvider
  private let variableUpdater: DivVariableUpdater
  private let updateCard: UpdateCardAction
  private let showTooltip: ShowTooltipAction?
  private let tooltipActionPerformer: TooltipActionPerformer?
  private let performTimerAction: PerformTimerAction
  private let persistentValuesStorage: DivPersistentValuesStorage

  public init(
    stateUpdater: DivStateUpdater,
    blockStateStorage: DivBlockStateStorage,
    patchProvider: DivPatchProvider,
    variableUpdater: DivVariableUpdater,
    updateCard: @escaping UpdateCardAction,
    showTooltip: ShowTooltipAction?,
    tooltipActionPerformer: TooltipActionPerformer?,
    performTimerAction: @escaping PerformTimerAction = { _, _, _ in },
    persistentValuesStorage: DivPersistentValuesStorage
  ) {
    self.stateUpdater = stateUpdater
    self.blockStateStorage = blockStateStorage
    self.patchProvider = patchProvider
    self.variableUpdater = variableUpdater
    self.updateCard = updateCard
    self.showTooltip = showTooltip
    self.tooltipActionPerformer = tooltipActionPerformer
    self.performTimerAction = performTimerAction
    self.persistentValuesStorage = persistentValuesStorage
  }

  public func canHandleURL(_ url: URL) -> Bool {
    url.scheme == DivActionIntent.scheme
  }

  public func handleURL(
    _ url: URL,
    cardId: DivCardID,
    completion: @escaping (Result<Void, Error>) -> Void = { _ in }
  ) -> Bool {
    guard let intent = DivActionIntent(url: url) else {
      return false
    }

    switch intent {
    case let .showTooltip(id, multiple):
      let tooltipInfo = TooltipInfo(id: id, showsOnStart: false, multiple: multiple)
      guard showTooltip == nil else {
        showTooltip?(tooltipInfo)
        break
      }
      tooltipActionPerformer?.showTooltip(info: tooltipInfo)
    case let .hideTooltip(id):
      guard showTooltip == nil else {
        break
      }
      tooltipActionPerformer?.hideTooltip(id: id)
    case let .download(patchUrl):
      patchProvider.getPatch(
        url: patchUrl,
        completion: { [unowned self] in
          self.applyPatch(cardId: cardId, result: $0, completion: completion)
        }
      )
    case let .setState(divStatePath, lifetime):
      stateUpdater.set(
        path: divStatePath,
        cardId: cardId,
        lifetime: lifetime
      )
      updateCard(.state(cardId))
    case let .setVariable(name, value):
      variableUpdater.update(
        cardId: cardId,
        name: DivVariableName(rawValue: name),
        value: value
      )
    case let .setCurrentItem(id, index):
      setCurrentItem(id: id, cardId: cardId, index: index)
      updateCard(.state(cardId))
    case let .setNextItem(id, overflow):
      setNextItem(id: id, cardId: cardId, overflow: overflow)
      updateCard(.state(cardId))
    case let .setPreviousItem(id, overflow):
      setPreviousItem(id: id, cardId: cardId, overflow: overflow)
      updateCard(.state(cardId))
    case let .video(id: id, action: action):
      blockStateStorage.setState(
        id: id,
        cardId: cardId,
        state: VideoBlockViewState(state: action == .play ? .playing : .paused)
      )
      updateCard(.state(cardId))
    case let .timer(timerId, action):
      performTimerAction(cardId, timerId, action)
    case let .setStoredValue(storedValue):
      persistentValuesStorage.set(value: storedValue)
    }

    return true
  }

  private func applyPatch(
    cardId: DivCardID,
    result: Result<DivPatch, Error>,
    completion: @escaping (Result<Void, Error>) -> Void
  ) {
    switch result {
    case let .success(patch):
      updateCard(.patch(cardId, patch))
      completion(.success(()))
    case let .failure(error):
      completion(.failure(error))
    }
  }

  private func setCurrentItem(id: String, cardId: DivCardID, index: Int) {
    switch blockStateStorage.getStateUntyped(id, cardId: cardId) {
    case let galleryState as GalleryViewState:
      setGalleryCurrentItem(
        id: id,
        cardId: cardId,
        index: index,
        itemsCount: galleryState.itemsCount
      )
    case let pagerState as PagerViewState:
      setPagerCurrentItem(
        id: id,
        cardId: cardId,
        index: index,
        numberOfPages: pagerState.numberOfPages
      )
    case let tabsState as TabViewState:
      setTabsCurrentItem(id: id, cardId: cardId, index: index, countOfPages: tabsState.countOfPages)
    default:
      return
    }
  }

  private func setNextItem(id: String, cardId: DivCardID, overflow: OverflowMode) {
    switch blockStateStorage.getStateUntyped(id, cardId: cardId) {
    case let galleryState as GalleryViewState:
      let index = getNextIndex(
        current: galleryState.currentItemIndex,
        count: galleryState.itemsCount,
        overflow: overflow
      )
      setGalleryCurrentItem(
        id: id,
        cardId: cardId,
        index: index,
        itemsCount: galleryState.itemsCount
      )
    case let pagerState as PagerViewState:
      let nextIndex = getNextIndex(
        current: Int(pagerState.currentPage),
        count: pagerState.numberOfPages,
        overflow: overflow
      )
      setPagerCurrentItem(
        id: id,
        cardId: cardId,
        index: nextIndex,
        numberOfPages: pagerState.numberOfPages
      )
    case let tabsState as TabViewState:
      let nextIndex = getNextIndex(
        current: Int(tabsState.selectedPageIndex),
        count: tabsState.countOfPages,
        overflow: overflow
      )
      setTabsCurrentItem(
        id: id,
        cardId: cardId,
        index: nextIndex,
        countOfPages: tabsState.countOfPages
      )
    default:
      return
    }
  }

  private func getNextIndex(
    current: Int,
    count: Int,
    overflow: OverflowMode
  ) -> Int {
    switch overflow {
    case .ring:
      return (current + 1) % count
    case .clamp:
      return min(current + 1, count - 1)
    }
  }

  private func setPreviousItem(id: String, cardId: DivCardID, overflow: OverflowMode) {
    switch blockStateStorage.getStateUntyped(id, cardId: cardId) {
    case let galleryState as GalleryViewState:
      let index = getPreviousIndex(
        current: galleryState.currentItemIndex,
        count: galleryState.itemsCount,
        overflow: overflow
      )
      setGalleryCurrentItem(
        id: id,
        cardId: cardId,
        index: index,
        itemsCount: galleryState.itemsCount
      )
    case let pagerState as PagerViewState:
      let prevIndex = getPreviousIndex(
        current: Int(pagerState.currentPage),
        count: pagerState.numberOfPages,
        overflow: overflow
      )
      setPagerCurrentItem(
        id: id,
        cardId: cardId,
        index: prevIndex,
        numberOfPages: pagerState.numberOfPages
      )
    case let tabsState as TabViewState:
      let prevIndex = getPreviousIndex(
        current: Int(tabsState.selectedPageIndex),
        count: tabsState.countOfPages,
        overflow: overflow
      )
      setTabsCurrentItem(
        id: id,
        cardId: cardId,
        index: prevIndex,
        countOfPages: tabsState.countOfPages
      )
    default:
      return
    }
  }

  private func getPreviousIndex(
    current: Int,
    count: Int,
    overflow: OverflowMode
  ) -> Int {
    switch overflow {
    case .ring:
      return (count + (current - 1)) % count
    case .clamp:
      return max(0, current - 1)
    }
  }

  private func setGalleryCurrentItem(
    id: String,
    cardId: DivCardID,
    index: Int,
    itemsCount: Int
  ) {
    blockStateStorage.setState(
      id: id,
      cardId: cardId,
      state: GalleryViewState(
        contentPageIndex: CGFloat(max(0, index)),
        itemsCount: itemsCount
      )
    )
  }

  private func setPagerCurrentItem(id: String, cardId: DivCardID, index: Int, numberOfPages: Int) {
    let clampedIndex = clamp(index, min: 0, max: numberOfPages - 1)
    guard clampedIndex == index else {
      return
    }

    blockStateStorage.setState(
      id: id,
      cardId: cardId,
      state: PagerViewState(
        numberOfPages: numberOfPages,
        currentPage: clampedIndex
      )
    )
  }

  private func setTabsCurrentItem(id: String, cardId: DivCardID, index: Int, countOfPages: Int) {
    blockStateStorage.setState(
      id: id,
      cardId: cardId,
      state: TabViewState(
        selectedPageIndex: CGFloat(max(0, index)),
        countOfPages: countOfPages
      )
    )
  }
}

extension GalleryViewState {
  fileprivate var currentItemIndex: Int {
    switch contentPosition {
    case let .offset(_, firstVisibleItemsIndex):
      return firstVisibleItemsIndex
    case let .paging(index):
      return Int(index)
    }
  }
}
