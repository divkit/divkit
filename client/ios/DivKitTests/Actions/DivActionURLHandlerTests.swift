@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import Foundation
import LayoutKit
import Testing

@Suite
struct DivActionURLHandlerTests {
  private let actionHandler: DivActionHandler
  private let blockStateStorage = DivBlockStateStorage()

  init() {
    let idToPath = IdToPath()
    idToPath.add(elementPath, forId: elementId, viewId: hostViewId)
    actionHandler = DivActionHandler(
      blockStateStorage: blockStateStorage,
      idToPath: idToPath
    )
  }

  @Test
  func setNextItemAction() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.firstElementState,
      mode: .next(step: nil, overflow: .clamp),
      expectedDelta: .items(1, wraps: false)
    )
  }

  @Test
  func setNextItemActionWithRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.lastElementState,
      afterState: SetItemAction.lastElementState,
      mode: .next(step: nil, overflow: .ring),
      expectedDelta: .items(1, wraps: true)
    )
  }

  @Test
  func setNextItem_WithStep() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.firstElementState,
      mode: .next(step: 2, overflow: .clamp),
      expectedDelta: .items(2, wraps: false)
    )
  }

  @Test
  func setNextItem_WithStepAndRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.lastElementState,
      afterState: SetItemAction.lastElementState,
      mode: .next(step: 2, overflow: .ring),
      expectedDelta: .items(2, wraps: true)
    )
  }

  @Test
  func setNextItemActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStates,
      afterState: SetItemAction.emptyStates,
      mode: .next(step: nil, overflow: .clamp),
      expectedDelta: .items(1, wraps: false)
    )
  }

  @Test
  func setPreviousItemAction() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementState,
      afterState: SetItemAction.secondElementState,
      mode: .previous(step: nil, overflow: .clamp),
      expectedDelta: .items(-1, wraps: false)
    )
  }

  @Test
  func setPreviousItemActionWithRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.firstElementState,
      mode: .previous(step: nil, overflow: .ring),
      expectedDelta: .items(-1, wraps: true)
    )
  }

  @Test
  func setPreviousItem_WithStep() {
    setItemTestCases(
      beforeStates: SetItemAction.thirdElementState,
      afterState: SetItemAction.thirdElementState,
      mode: .previous(step: 2, overflow: .clamp),
      expectedDelta: .items(-2, wraps: false)
    )
  }

  @Test
  func setPreviousItem_WithStepAndRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.firstElementState,
      mode: .previous(step: 8, overflow: .ring),
      expectedDelta: .items(-8, wraps: true)
    )
  }

  @Test
  func setPreviousItemActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStates,
      afterState: SetItemAction.emptyStates,
      mode: .previous(step: nil, overflow: .clamp),
      expectedDelta: .items(-1, wraps: false)
    )
  }

  @Test
  func setCurrentItemAction() {
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

  @Test
  func setCurrentItemActionWithOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementState,
      afterState: SetItemAction.firstElementState,
      mode: .current(50)
    )
  }

  @Test
  func setScrollForwardAction() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementStateWithOffset,
      afterState: SetItemAction.firstElementStateWithOffset,
      mode: .forward(10, .clamp),
      expectedDelta: .offset(10, wraps: false)
    )
  }

  @Test
  func setScrollForwardActionWithClampOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementStateWithOffset,
      afterState: SetItemAction.firstElementStateWithOffset,
      mode: .forward(30, .clamp),
      expectedDelta: .offset(30, wraps: false)
    )
  }

  @Test
  func setScrollForwardActionWithRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementStateWithOffset,
      afterState: SetItemAction.firstElementStateWithOffset,
      mode: .forward(30, .ring),
      expectedDelta: .offset(30, wraps: true)
    )
  }

  @Test
  func setScrollForwardActionWithEmptyState() {
    setItemTestCase(
      beforeState: SetItemAction.zeroRangeState,
      afterState: SetItemAction.zeroRangeState,
      mode: .forward(30, .ring),
      expectedDelta: .offset(30, wraps: true)
    )
  }

  @Test
  func setScrollForwardActionWithoutRange() {
    setItemTestCase(
      beforeState: SetItemAction.unknownRangeState,
      afterState: SetItemAction.unknownRangeState,
      mode: .forward(30, .ring)
    )
  }

  @Test
  func setScrollBackwardAction() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementStateWithOffset,
      afterState: SetItemAction.secondElementStateWithOffset,
      mode: .backward(10, .clamp),
      expectedDelta: .offset(-10, wraps: false)
    )
  }

  @Test
  func setScrollBackwardActionWithClampOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementStateWithOffset,
      afterState: SetItemAction.secondElementStateWithOffset,
      mode: .backward(40, .clamp),
      expectedDelta: .offset(-40, wraps: false)
    )
  }

  @Test
  func setScrollBackwardActionWithRingOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementStateWithOffset,
      afterState: SetItemAction.secondElementStateWithOffset,
      mode: .backward(30, .ring),
      expectedDelta: .offset(-30, wraps: true)
    )
  }

  @Test
  func setScrollBackwardActionWithEmptyState() {
    setItemTestCase(
      beforeState: SetItemAction.zeroRangeState,
      afterState: SetItemAction.zeroRangeState,
      mode: .backward(30, .ring),
      expectedDelta: .offset(-30, wraps: true)
    )
  }

  @Test
  func setScrollBackwardActionWithoutRange() {
    setItemTestCase(
      beforeState: SetItemAction.unknownRangeState,
      afterState: SetItemAction.unknownRangeState,
      mode: .backward(30, .ring)
    )
  }

  @Test
  func setScrollToPositionAction() {
    setItemTestCases(
      beforeStates: SetItemAction.firstElementStateWithOffset,
      afterState: SetItemAction.secondElementStateWithOffset,
      mode: .position(10)
    )
  }

  @Test
  func setScrollToPositionActionWithOverflow() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementStateWithOffset,
      afterState: SetItemAction.endStateWithOffset,
      mode: .position(30)
    )
  }

  @Test
  func setScrollToPositionActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStatesWithOffset,
      afterState: SetItemAction.emptyStatesWithOffset,
      mode: .position(10)
    )
  }

  @Test
  func setScrollToStartAction() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementStateWithOffset,
      afterState: SetItemAction.firstElementStateWithOffset,
      mode: .start
    )
  }

  @Test
  func setScrollToStartActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStatesWithOffset,
      afterState: SetItemAction.emptyStatesWithOffset,
      mode: .start
    )
  }

  @Test
  func setScrollToEndAction() {
    setItemTestCases(
      beforeStates: SetItemAction.secondElementStateWithOffset,
      afterState: SetItemAction.endStateWithOffset,
      mode: .end
    )
  }

  @Test
  func setScrollToEndActionWithEmptyState() {
    setItemTestCases(
      beforeStates: SetItemAction.emptyStatesWithOffset,
      afterState: SetItemAction.emptyStatesWithOffset,
      mode: .end
    )
  }

  @Test
  func scrollAction_GalleryWithoutAnimation() {
    let cases: [(SetItemAction.Mode, GalleryViewState.Position, ScrollDelta?)] = [
      (.current(2), .paging(index: 2), nil),
      (.next(step: 1, overflow: .clamp), .paging(index: 1), .items(1, wraps: false)),
      (.previous(step: 1, overflow: .clamp), .paging(index: 1), .items(-1, wraps: false)),
      (.forward(10, .clamp), .offset(10), .offset(10, wraps: false)),
      (.backward(10, .clamp), .offset(10), .offset(-10, wraps: false)),
      (.position(5), .offset(5), nil),
      (.start, .offset(0), nil),
      (.end, .offset(20), nil),
    ]
    for (mode, position, delta) in cases {
      setItemTestCase(
        beforeState: GalleryViewState(
          contentPosition: .offset(10, firstVisibleItemIndex: 1),
          itemsCount: 3,
          isScrolling: false,
          scrollRange: 20,
          animated: true
        ),
        afterState: GalleryViewState(
          contentPosition: position,
          itemsCount: 3,
          isScrolling: false,
          scrollRange: 20,
          animated: false
        ),
        mode: mode,
        animated: false,
        expectedDelta: delta
      )
    }
  }

  @Test
  func scrollAction_PagerWithoutAnimation() {
    let cases: [(SetItemAction.Mode, Int, ScrollDelta?)] = [
      (.current(2), 2, nil),
      (.next(step: 1, overflow: .clamp), 1, .items(1, wraps: false)),
      (.previous(step: 1, overflow: .clamp), 1, .items(-1, wraps: false)),
      (.start, 0, nil),
      (.end, 2, nil),
    ]
    for (mode, page, delta) in cases {
      setItemTestCase(
        beforeState: PagerViewState(numberOfPages: 3, currentPage: 1, animated: true),
        afterState: PagerViewState(numberOfPages: 3, currentPage: page, animated: false),
        mode: mode,
        animated: false,
        expectedDelta: delta
      )
    }
  }

  private func setItemTestCases(
    beforeStates: [ElementState],
    afterState: [ElementState],
    mode: SetItemAction.Mode,
    expectedDelta: ScrollDelta? = nil
  ) {
    for states in zip(beforeStates, afterState) {
      switch states {
      case let (before as GalleryViewState, after as GalleryViewState):
        setItemTestCase(
          beforeState: before,
          afterState: after,
          mode: mode,
          expectedDelta: expectedDelta
        )
      case let (before as PagerViewState, after as PagerViewState):
        setItemTestCase(
          beforeState: before,
          afterState: after,
          mode: mode,
          expectedDelta: expectedDelta
        )
      case let (before as TabViewState, after as TabViewState):
        setItemTestCase(
          beforeState: before,
          afterState: after,
          mode: mode,
          expectedDelta: expectedDelta
        )
      case (_, _):
        Issue.record("Unsupported element state")
      }
    }
  }

  private func setItemTestCase<State: ScrollStateForTesting>(
    beforeState: State,
    afterState: State,
    mode: SetItemAction.Mode,
    animated: Bool? = nil,
    expectedDelta: ScrollDelta? = nil
  ) {
    blockStateStorage.setState(path: elementPath, state: beforeState)
    actionHandler.handle(
      divAction(logId: "test", url: SetItemAction.makeURL(mode: mode, animated: animated)),
      path: cardId.path,
      source: .tap,
      sender: nil
    )
    let result: State? = blockStateStorage.getState(elementPath)
    #expect(result?.withoutScrollRequest == afterState.withoutScrollRequest)
    expectScrollRequest(result, delta: expectedDelta)
  }

  private func expectScrollRequest(_ state: ElementState?, delta: ScrollDelta?) {
    let request: (ScrollRequest?, ScrollDelta?)
    switch state {
    case let state as GalleryViewState:
      request = (state.scrollRequest, state.scrollRequest?.delta)
    case let state as PagerViewState:
      request = (state.scrollRequest, state.scrollRequest?.delta)
    case let state as TabViewState:
      request = (state.scrollRequest, state.scrollRequest?.delta)
    default:
      Issue.record("Unsupported element state")
      return
    }
    #expect(request.1 == delta)
    if delta != nil {
      #expect(request.0 != nil)
    }
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

  static let zeroRangeState = GalleryViewState(
    contentPosition: .offset(0), itemsCount: 0, isScrolling: false,
    scrollRange: 0, animated: true
  )

  static let unknownRangeState = GalleryViewState(
    contentPosition: .offset(0), itemsCount: 0, isScrolling: false,
    scrollRange: nil, animated: true
  )

  static let emptyStatesWithOffset: [ElementState] = [zeroRangeState, unknownRangeState]

  static func makeURL(mode: Mode, animated: Bool? = nil) -> String {
    var url: String
    switch mode {
    case let .next(step, overflow):
      url = "div-action://set_next_item?id=\(elementId)&overflow=\(overflow)"
      if let step {
        url += "&step=\(step)"
      }
    case let .previous(step, overflow):
      url = "div-action://set_previous_item?id=\(elementId)&overflow=\(overflow)"
      if let step {
        url += "&step=\(step)"
      }
    case let .current(item):
      url = "div-action://set_current_item?id=\(elementId)&item=\(item)"
    case let .forward(step, overflow):
      url = "div-action://scroll_forward?id=\(elementId)&step=\(Int(step))&overflow=\(overflow)"
    case let .backward(step, overflow):
      url = "div-action://scroll_backward?id=\(elementId)&step=\(Int(step))&overflow=\(overflow)"
    case let .position(step):
      url = "div-action://scroll_to_position?id=\(elementId)&step=\(Int(step))"
    case .start:
      url = "div-action://scroll_to_start?id=\(elementId)"
    case .end:
      url = "div-action://scroll_to_end?id=\(elementId)"
    }
    if let animated {
      url += "&animated=\(animated)"
    }
    return url
  }
}

private let cardId: DivCardID = "cardId"
private let hostViewId = DivViewId(cardId: cardId)
private let elementId: String = "element"
private let elementPath = cardId.path + elementId
