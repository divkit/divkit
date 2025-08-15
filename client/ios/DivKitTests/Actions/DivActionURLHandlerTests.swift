@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import XCTest

final class DivActionURLHandlerTests: XCTestCase {
  private var actionHandler: DivActionHandler!
  private let blockStateStorage = DivBlockStateStorage()

  override func setUp() {
    actionHandler = DivActionHandler(
      blockStateStorage: blockStateStorage
    )
  }

  func test_setNextItemAction() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.secondElementState,
      mode: .next(step: nil, overflow: .clamp)
    )
  }

  func test_setNextItemActionWithRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.lastElementState,
      afterState: SetItemAction.firstElementState,
      mode: .next(step: nil, overflow: .ring)
    )
  }

  func test_setNextItem_WithStep() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.thirdElementState,
      mode: .next(step: 2, overflow: .clamp)
    )
  }

  func test_setNextItem_WithStepAndRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.lastElementState,
      afterState: SetItemAction.secondElementState,
      mode: .next(step: 2, overflow: .ring)
    )
  }

  func test_setNextItemActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStates,
      afterState: SetItemAction.emptyStates,
      mode: .next(step: nil, overflow: .clamp)
    )
  }

  func test_setPreviousItemAction() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementState,
      afterState: SetItemAction.firstElementState,
      mode: .previous(step: nil, overflow: .clamp)
    )
  }

  func test_setPreviousItemActionWithRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.lastElementState,
      mode: .previous(step: nil, overflow: .ring)
    )
  }

  func test_setPreviousItem_WithStep() {
    setItemTestCases(
      beforeStates: SetItemAction.thirdElementState,
      afterState: SetItemAction.firstElementState,
      mode: .previous(step: 2, overflow: .clamp)
    )
  }

  func test_setPreviousItem_WithStepAndRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.thirdElementState,
      mode: .previous(step: 8, overflow: .ring)
    )
  }

  func test_setPreviousItemActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStates,
      afterState: SetItemAction.emptyStates,
      mode: .previous(step: nil, overflow: .clamp)
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

  func test_setScrollForwardAction() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementStateWithOffset,
      afterState: SetItemAction.secondElementStateWithOffset,
      mode: .forward(10, .clamp)
    )
  }

  func test_setScrollForwardActionWithClampOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementStateWithOffset,
      afterState: SetItemAction.endStateWithOffset,
      mode: .forward(30, .clamp)
    )
  }

  func test_setScrollForwardActionWithRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementStateWithOffset,
      afterState: SetItemAction.secondElementStateWithOffset,
      mode: .forward(30, .ring)
    )
  }

  func test_setScrollForwardActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStatesWithOffset,
      afterState: SetItemAction.emptyStatesWithOffset,
      mode: .forward(30, .ring)
    )
  }

  func test_setScrollBackwardAction() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementStateWithOffset,
      afterState: SetItemAction.firstElementStateWithOffset,
      mode: .backward(10, .clamp)
    )
  }

  func test_setScrollBackwardActionWithClampOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementStateWithOffset,
      afterState: SetItemAction.firstElementStateWithOffset,
      mode: .backward(40, .clamp)
    )
  }

  func test_setScrollBackwardActionWithRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementStateWithOffset,
      afterState: SetItemAction.firstElementStateWithOffset,
      mode: .backward(30, .ring)
    )
  }

  func test_setScrollBackwardActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStatesWithOffset,
      afterState: SetItemAction.emptyStatesWithOffset,
      mode: .backward(30, .ring)
    )
  }

  func test_setScrollToPositionAction() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementStateWithOffset,
      afterState: SetItemAction.secondElementStateWithOffset,
      mode: .position(10)
    )
  }

  func test_setScrollToPositionActionWithOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementStateWithOffset,
      afterState: SetItemAction.endStateWithOffset,
      mode: .position(30)
    )
  }

  func test_setScrollToPositionActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStatesWithOffset,
      afterState: SetItemAction.emptyStatesWithOffset,
      mode: .position(10)
    )
  }

  func test_setScrollToStartAction() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementStateWithOffset,
      afterState: SetItemAction.firstElementStateWithOffset,
      mode: .start
    )
  }

  func test_setScrollToStartActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStatesWithOffset,
      afterState: SetItemAction.emptyStatesWithOffset,
      mode: .start
    )
  }

  func test_setScrollToEndAction() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementStateWithOffset,
      afterState: SetItemAction.endStateWithOffset,
      mode: .end
    )
  }

  func test_setScrollToEndActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStatesWithOffset,
      afterState: SetItemAction.emptyStatesWithOffset,
      mode: .end
    )
  }

  private func setItemTestCases(
    beforeStates: [ElementState],
    afterState: [ElementState],
    mode: SetItemAction.Mode
  ) {
    for states in zip(beforeStates, afterState) {
      switch states {
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
    blockStateStorage.setState(id: elementId, cardId: cardId, state: beforeState)
    actionHandler.handle(
      divAction(logId: "test", url: SetItemAction.makeURL(mode: mode)),
      path: cardId.path,
      source: .tap,
      sender: nil
    )
    XCTAssertEqual(blockStateStorage.getState(elementId, cardId: cardId), afterState)
  }
}

private enum SetItemAction {
  enum Mode {
    case next(step: Int?, overflow: OverflowMode)
    case previous(step: Int?, overflow: OverflowMode)
    case current(Int)
    case forward(Int, OverflowMode)
    case backward(Int, OverflowMode)
    case position(Int)
    case start
    case end
  }

  static let firstElementState: [ElementState] = [
    PagerViewState(numberOfPages: 10, currentPage: 0, animated: true),
    GalleryViewState(contentPageIndex: 0, itemsCount: 10, animated: true),
    TabViewState(selectedPageIndex: 0, countOfPages: 10),
  ]

  static let secondElementState: [ElementState] = [
    PagerViewState(numberOfPages: 10, currentPage: 1, animated: true),
    GalleryViewState(contentPageIndex: 1, itemsCount: 10, animated: true),
    TabViewState(selectedPageIndex: 1, countOfPages: 10),
  ]

  static let thirdElementState: [ElementState] = [
    PagerViewState(numberOfPages: 10, currentPage: 2, animated: true),
    GalleryViewState(contentPageIndex: 2, itemsCount: 10, animated: true),
    TabViewState(selectedPageIndex: 2, countOfPages: 10),
  ]

  static let lastElementState: [ElementState] = [
    PagerViewState(numberOfPages: 10, currentPage: 9, animated: true),
    GalleryViewState(contentPageIndex: 9, itemsCount: 10, animated: true),
    TabViewState(selectedPageIndex: 9, countOfPages: 10),
  ]

  static let emptyStates: [ElementState] = [
    PagerViewState(numberOfPages: 0, currentPage: 0, animated: true),
    GalleryViewState(contentPageIndex: 0, itemsCount: 0, animated: true),
    TabViewState(selectedPageIndex: 0, countOfPages: 0),
  ]

  static let firstElementStateWithOffset: [ElementState] = [
    GalleryViewState(
      contentPosition: .offset(0),
      itemsCount: 3,
      isScrolling: false,
      scrollRange: 20,
      animated: true
    ),
  ]

  static let secondElementStateWithOffset: [ElementState] = [
    GalleryViewState(
      contentPosition: .offset(10),
      itemsCount: 3,
      isScrolling: false,
      scrollRange: 20,
      animated: true
    ),
  ]

  static let endStateWithOffset: [ElementState] = [
    GalleryViewState(
      contentPosition: .offset(20),
      itemsCount: 3,
      isScrolling: false,
      scrollRange: 20,
      animated: true
    ),
  ]

  static let emptyStatesWithOffset: [ElementState] = [
    GalleryViewState(
      contentPosition: .offset(0),
      itemsCount: 0,
      isScrolling: false,
      scrollRange: 0,
      animated: true
    ),
    GalleryViewState(
      contentPosition: .offset(0),
      itemsCount: 0,
      isScrolling: false,
      scrollRange: nil,
      animated: true
    ),
  ]

  static func makeURL(mode: Mode) -> String {
    switch mode {
    case let .next(step, overflow):
      let url = "div-action://set_next_item?id=\(elementId)&overflow=\(overflow)"
      if let step {
        return url + "&step=\(step)"
      }
      return url
    case let .previous(step, overflow):
      let url = "div-action://set_previous_item?id=\(elementId)&overflow=\(overflow)"
      if let step {
        return url + "&step=\(step)"
      }
      return url
    case let .current(item):
      return "div-action://set_current_item?id=\(elementId)&item=\(item)"
    case let .forward(step, overflow):
      return "div-action://scroll_forward?id=\(elementId)&step=\(Int(step))&overflow=\(overflow)"
    case let .backward(step, overflow):
      return "div-action://scroll_backward?id=\(elementId)&step=\(Int(step))&overflow=\(overflow)"
    case let .position(step):
      return "div-action://scroll_to_position?id=\(elementId)&step=\(Int(step))"
    case .start:
      return "div-action://scroll_to_start?id=\(elementId)"
    case .end:
      return "div-action://scroll_to_end?id=\(elementId)"
    }
  }
}

private let cardId: DivCardID = "cardId"
private let elementId: String = "element"
