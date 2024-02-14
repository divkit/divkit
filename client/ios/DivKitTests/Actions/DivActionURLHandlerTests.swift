@testable import DivKit
@testable import LayoutKit

import XCTest

final class DivActionURLHandlerTests: XCTestCase {
  private var actionHandler: DivActionURLHandler!
  private let blockStateStorage = DivBlockStateStorage()

  override func setUp() {
    actionHandler = DivActionURLHandler(
      stateUpdater: DefaultDivStateManagement(),
      blockStateStorage: blockStateStorage,
      patchProvider: MockPatchProvider(),
      variableUpdater: DivVariablesStorage(),
      updateCard: { _ in },
      showTooltip: nil,
      tooltipActionPerformer: nil,
      persistentValuesStorage: DivPersistentValuesStorage()
    )
  }

  func test_setNextItemAction() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.secondElementState,
      mode: .next(.clamp)
    )
  }

  func test_setNextItemActionWithRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.lastElementsStates,
      afterState: SetItemAction.firstElementState,
      mode: .next(.ring)
    )
  }

  func test_setNextItemActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStates,
      afterState: SetItemAction.emptyStates,
      mode: .next(.clamp)
    )
  }

  func test_setPreviousItemAction() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementState,
      afterState: SetItemAction.firstElementState,
      mode: .previous(.clamp)
    )
  }

  func test_setPreviousItemActionWithRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.lastElementsStates,
      mode: .previous(.ring)
    )
  }

  func test_setPreviousItemActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStates,
      afterState: SetItemAction.emptyStates,
      mode: .previous(.clamp)
    )
  }

  func test_setCurrentItemAction() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.firstElementState,
      mode: .current(0)
    )
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.secondElementState,
      mode: .current(1)
    )
  }

  func test_setCurrentItemActionWithOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.firstElementState,
      mode: .current(50)
    )
  }

  private func setItemTestCases(
    beforeStates: [ElementState],
    afterState: [ElementState],
    mode: SetItemAction.Mode
  ) {
    zip(beforeStates, afterState).forEach {
      switch $0 {
      case let (before as GalleryViewState, after as GalleryViewState):
        setItemTestCase(beforeState: before, afterState: after, mode: mode)
      case let (before as PagerViewState, after as PagerViewState):
        setItemTestCase(beforeState: before, afterState: after, mode: mode)
      case let (before as TabViewState, after as TabViewState):
        setItemTestCase(beforeState: before, afterState: after, mode: mode)
      case (_, _):
        assertionFailure("Unsupported element state")
      }
    }
  }

  private func setItemTestCase<State: ElementState & Equatable>(
    beforeState: State,
    afterState: State,
    mode: SetItemAction.Mode
  ) {
    blockStateStorage.setState(id: elementID, cardId: cardID, state: beforeState)
    let _ = actionHandler.handleURL(SetItemAction.makeURL(mode: mode), cardId: cardID)
    XCTAssertEqual(blockStateStorage.getState(elementID, cardId: cardID), afterState)
  }
}

private enum SetItemAction {
  enum Mode {
    enum Overflow: String {
      case clamp
      case ring
    }

    case next(Overflow)
    case previous(Overflow)
    case current(Int)
  }

  static let firstElementState: [ElementState] = [
    PagerViewState(numberOfPages: 10, currentPage: 0),
    GalleryViewState(contentPageIndex: 0, itemsCount: 10),
    TabViewState(selectedPageIndex: 0, countOfPages: 10),
  ]

  static let secondElementState: [ElementState] = [
    PagerViewState(numberOfPages: 10, currentPage: 1),
    GalleryViewState(contentPageIndex: 1, itemsCount: 10),
    TabViewState(selectedPageIndex: 1, countOfPages: 10),
  ]

  static let lastElementsStates: [ElementState] = [
    PagerViewState(numberOfPages: 10, currentPage: 9),
    GalleryViewState(contentPageIndex: 9, itemsCount: 10),
    TabViewState(selectedPageIndex: 9, countOfPages: 10),
  ]

  static let emptyStates: [ElementState] = [
    PagerViewState(numberOfPages: 0, currentPage: 0),
    GalleryViewState(contentPageIndex: 0, itemsCount: 0),
    TabViewState(selectedPageIndex: 0, countOfPages: 0),
  ]

  static func makeURL(mode: Mode) -> URL {
    switch mode {
    case let .next(overflow):
      url("div-action://set_next_item?id=\(elementID)&overflow=\(overflow)")
    case let .previous(overflow):
      url("div-action://set_previous_item?id=\(elementID)&overflow=\(overflow)")
    case let .current(item):
      url("div-action://set_current_item?id=\(elementID)&item=\(item)")
    }
  }
}

private let cardID: DivCardID = "cardID"
private let elementID: String = "element"
