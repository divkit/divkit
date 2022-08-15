import CoreGraphics
import Foundation

import CommonCore
import LayoutKit

public final class DivActionURLHandler {
  public typealias UpdateCardAction = (DivCardID, DivPatch) -> Void
  public typealias ShowTooltipAction = (TooltipInfo) -> Void

  private let stateUpdater: DivStateUpdater
  private let blockStateStorage: DivBlockStateStorage
  private let patchProvider: DivPatchProvider
  private let variableUpdater: DivVariableUpdater
  private let updateCard: UpdateCardAction
  private let showTooltip: ShowTooltipAction

  public init(
    stateUpdater: DivStateUpdater,
    blockStateStorage: DivBlockStateStorage,
    patchProvider: DivPatchProvider,
    variableUpdater: DivVariableUpdater,
    updateCard: @escaping UpdateCardAction,
    showTooltip: @escaping ShowTooltipAction
  ) {
    self.stateUpdater = stateUpdater
    self.blockStateStorage = blockStateStorage
    self.patchProvider = patchProvider
    self.variableUpdater = variableUpdater
    self.updateCard = updateCard
    self.showTooltip = showTooltip
  }

  public func handleURL(
    _ url: URL,
    cardId: DivCardID?,
    completion: @escaping (Result<Void, Error>) -> Void = { _ in }
  ) -> Bool {
    guard let intent = DivActionIntent(url: url) else {
      return false
    }

    switch intent {
    case let .showTooltip(id, multiple):
      showTooltip(TooltipInfo(id: id, showsOnStart: false, multiple: multiple))
    case .hideTooltip:
      return false
    case let .download(patchUrl):
      guard let cardId = cardId else {
        return false
      }
      patchProvider.getPatch(
        url: patchUrl,
        completion: { [unowned self] in
          self.applyPatch(cardId: cardId, result: $0, completion: completion)
        }
      )
    case let .setState(divStatePath, lifetime):
      guard let cardId = cardId else {
        return false
      }
      stateUpdater.set(
        path: divStatePath,
        cardId: cardId,
        lifetime: lifetime
      )
    case let .setVariable(name, value):
      guard let cardId = cardId else {
        return false
      }
      variableUpdater.update(
        cardId: cardId,
        name: DivVariableName(rawValue: name),
        value: value
      )
    case let .setCurrentItem(id, index):
      setCurrentItem(id: id, index: index)
    case let .setNextItem(id):
      setNextItem(id: id)
    case let .setPreviousItem(id):
      setPreviousItem(id: id)
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
      updateCard(cardId, patch)
      completion(.success(()))
    case let .failure(error):
      completion(.failure(error))
    }
  }

  private func setCurrentItem(id: String, index: Int) {
    switch blockStateStorage.getStateUntyped(id) {
    case let galleryState as GalleryViewState:
      switch galleryState.contentPosition {
      case .offset:
        return
      case .paging:
        setGalleryCurrentItem(id: id, index: Int(index))
      }
    case let pagerState as PagerViewState:
      setPagerCurrentItem(
        id: id,
        index: index,
        numberOfPages: pagerState.numberOfPages
      )
    case is TabViewState:
      setTabsCurrentItem(id: id, index: index)
    default:
      return
    }
  }

  private func setNextItem(id: String) {
    switch blockStateStorage.getStateUntyped(id) {
    case let galleryState as GalleryViewState:
      switch galleryState.contentPosition {
      case .offset:
        return
      case let .paging(index):
        setGalleryCurrentItem(id: id, index: Int(index) + 1)
      }
    case let pagerState as PagerViewState:
      setPagerCurrentItem(
        id: id,
        index: Int(pagerState.currentPage) + 1,
        numberOfPages: pagerState.numberOfPages
      )
    case let tabsState as TabViewState:
      setTabsCurrentItem(id: id, index: Int(tabsState.selectedPageIndex) + 1)
    default:
      return
    }
  }

  private func setPreviousItem(id: String) {
    switch blockStateStorage.getStateUntyped(id) {
    case let galleryState as GalleryViewState:
      switch galleryState.contentPosition {
      case .offset:
        return
      case let .paging(index):
        setGalleryCurrentItem(id: id, index: Int(index) - 1)
      }
    case let pagerState as PagerViewState:
      setPagerCurrentItem(
        id: id,
        index: Int(pagerState.currentPage) - 1,
        numberOfPages: pagerState.numberOfPages
      )
    case let tabsState as TabViewState:
      setTabsCurrentItem(id: id, index: Int(tabsState.selectedPageIndex) - 1)
    default:
      return
    }
  }

  private func setGalleryCurrentItem(id: String, index: Int) {
    blockStateStorage.setState(
      id: id,
      state: GalleryViewState(contentPageIndex: CGFloat(max(0, index)))
    )
  }

  private func setPagerCurrentItem(id: String, index: Int, numberOfPages: Int) {
    let clampedIndex = clamp(index, min: 0, max: numberOfPages - 1)
    guard clampedIndex == index else {
      return
    }

    blockStateStorage.setState(
      id: id,
      state: PagerViewState(
        numberOfPages: numberOfPages,
        currentPage: clampedIndex
      )
    )
  }

  private func setTabsCurrentItem(id: String, index: Int) {
    blockStateStorage.setState(
      id: id,
      state: TabViewState(selectedPageIndex: CGFloat(max(0, index)))
    )
  }
}
