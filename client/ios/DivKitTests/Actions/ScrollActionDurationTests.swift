@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import Serialization
import UIKit
import VGSL
import XCTest

final class ScrollActionDurationTests: ScrollActionTestCase {
  func test_ScrollByAcrossRebuilds_BeforeTabsLayout_PreservesBothRequests() throws {
    setState(tabState(index: 0, itemCount: 5))
    let (view, model) = try makeScrollTestTabs(
      path: elementPath, clock: TestScrollAnimationClock(), observer: blockStateStorage, frame: .zero
    )

    handleScrollBy(itemCount: 1, animated: false)
    try applyPendingStateAndRebind(view, model: model)
    handleScrollBy(itemCount: 1, animated: false)
    try applyPendingStateAndRebind(view, model: model)
    view.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
    view.layoutIfNeeded()

    let state: TabViewState = getState()
    XCTAssertEqual(state.selectedPageIndex, 2)
    XCTAssertNil(state.scrollRequest)
    XCTAssertEqual((view.visibleBoundsTrackingSubviews[0] as! UIScrollView).contentOffset.x, 200)
  }

  func test_ScrollByAfterInitialTabsLayout_DoesNotReplayPreviousRequest() throws {
    setState(tabState(index: 0, itemCount: 5))
    let (view, model) = try makeScrollTestTabs(
      path: elementPath, clock: TestScrollAnimationClock(), observer: blockStateStorage, frame: .zero
    )
    handleScrollBy(itemCount: 1, animated: false)
    try applyPendingStateAndRebind(view, model: model)
    view.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
    view.layoutIfNeeded()

    handleScrollBy(itemCount: 1, animated: false)
    try applyPendingStateAndRebind(view, model: model)

    XCTAssertEqual((view.visibleBoundsTrackingSubviews[0] as! UIScrollView).contentOffset.x, 200)
  }

  func test_BatchedTabsScroll_UsesFinalTargetAndLastDuration() throws {
    let clock = TestScrollAnimationClock()
    let (view, model) = try makeTabs(clock: clock)

    handleScrollTo(end, duration: .value(2000))
    handleScrollBy(itemCount: -1, duration: .value(1000))
    try applyPendingStateAndRebind(view, model: model)
    clock.advance(to: 1)

    let result: TabViewState = getState()
    XCTAssertEqual(result.selectedPageIndex, 3)
    XCTAssertNil(result.scrollRequest)
    XCTAssertEqual((view.visibleBoundsTrackingSubviews[0] as! UIScrollView).contentOffset.x, 300)
  }

  func test_ScrollTo_PreservesDuration() throws {
    let action = try DivActionScrollTo(
      dictionary: [
        "id": "element_id",
        "destination": ["type": "end"],
        "duration": 2000,
      ],
      context: ParsingContext()
    )

    XCTAssertEqual(action.toDictionary()["duration"] as? Int, 2000)
  }

  func test_ScrollBy_DuringTimedTabsTransition_UsesActualPageWithHighestPageHeight() throws {
    let clock = TestScrollAnimationClock()
    let (view, model) = try makeTabs(clock: clock)
    handleScrollTo(end, duration: .value(2000))
    try applyPendingStateAndRebind(view, model: model)
    clock.advance(to: 1)

    handleScrollBy(itemCount: -1, duration: .value(2000))
    try applyPendingStateAndRebind(view, model: model)
    clock.advance(to: 3)

    let result: TabViewState = getState()
    XCTAssertEqual(result.selectedPageIndex, 1)
    XCTAssertEqual(view.selectedPageIndex, 1)
  }

  func test_ScrollBy_BeforeFirstTimedTabsFrame_UsesActualPage() throws {
    let clock = TestScrollAnimationClock()
    let (view, model) = try makeTabs(clock: clock)
    handleScrollTo(end, duration: .value(2000))
    try applyPendingStateAndRebind(view, model: model)

    handleScrollBy(itemCount: -1, duration: .value(2000))
    try applyPendingStateAndRebind(view, model: model)
    clock.advance(to: 2)

    let result: TabViewState = getState()
    XCTAssertEqual(result.selectedPageIndex, 0)
    XCTAssertEqual(view.selectedPageIndex, 0)
  }

  func test_ScrollBy_PreservesDuration() throws {
    let action = try DivActionScrollBy(
      dictionary: ["id": "element_id", "item_count": 1, "duration": 2000],
      context: ParsingContext()
    )

    XCTAssertEqual(action.toDictionary()["duration"] as? Int, 2000)
  }

  func test_ScrollTo_Duration_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollTo(end, duration: .value(2000))

    let state: GalleryViewState = getState()
    XCTAssertEqual(state.contentPosition, .offset(1000))
    XCTAssertEqual(state.scrollRequest?.duration, 2)
  }

  func test_ScrollBy_Duration_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollBy(offset: 200, duration: .value(2000))

    let state: GalleryViewState = getState()
    XCTAssertEqual(state.scrollRequest?.delta, .offset(200, wraps: false))
    XCTAssertEqual(state.scrollRequest?.duration, 2)
  }

  func test_ScrollTo_Duration_Pager() {
    setState(pagerState(index: 0, itemCount: 5))

    handleScrollTo(end, duration: .value(2000))

    assertScrollStateEqual(
      getState(),
      PagerViewState(
        numberOfPages: 5,
        currentPage: 4,
        animated: true,
        scrollRequest: ScrollRequest(duration: 2)
      )
    )
  }

  func test_ScrollBy_Duration_Pager() {
    setState(pagerState(index: 0, itemCount: 5))

    handleScrollBy(itemCount: 2, duration: .value(2000))

    assertScrollStateEqual(
      getState(),
      PagerViewState(
        numberOfPages: 5,
        currentPage: 0,
        animated: true,
        scrollRequest: ScrollRequest(duration: 2)
      )
    )
  }

  func test_ScrollTo_Duration_Tabs() {
    setState(tabState(index: 0, itemCount: 5))

    handleScrollTo(end, duration: .value(2000))

    assertScrollStateEqual(
      getState(),
      TabViewState(selectedPageIndex: 4, countOfPages: 5, scrollRequest: ScrollRequest(duration: 2))
    )
  }

  func test_ScrollBy_Duration_Tabs() {
    setState(tabState(index: 0, itemCount: 5))

    handleScrollBy(itemCount: 2, duration: .value(2000))

    assertScrollStateEqual(
      getState(),
      TabViewState(selectedPageIndex: 0, countOfPages: 5, scrollRequest: ScrollRequest(duration: 2))
    )
  }

  func test_ScrollTo_ZeroDuration_DisablesAnimation() {
    setState(pagerState(index: 0, itemCount: 5))

    handleScrollTo(end, duration: .value(0))

    assertScrollStateEqual(
      getState(),
      PagerViewState(
        numberOfPages: 5,
        currentPage: 4,
        animated: false,
        scrollRequest: ScrollRequest(duration: 0)
      )
    )
  }

  func test_ScrollTo_NonAnimatedWithDuration_Tabs() {
    setState(tabState(index: 0, itemCount: 5))

    handleScrollTo(end, animated: false, duration: .value(2000))

    assertScrollStateEqual(
      getState(),
      TabViewState(
        selectedPageIndex: 4,
        countOfPages: 5,
        animated: false,
        scrollRequest: ScrollRequest(duration: 2)
      )
    )
  }

  func test_ScrollBy_DurationExpression_UsesCurrentVariable() throws {
    setState(pagerState(index: 0, itemCount: 5))
    variablesStorage.set(cardId: cardId, variables: ["scroll_duration": .integer(1000)])
    let action = try DivActionScrollBy(
      dictionary: ["id": "element_id", "item_count": 1, "duration": "@{scroll_duration}"],
      context: ParsingContext()
    )
    variablesStorage.set(cardId: cardId, variables: ["scroll_duration": .integer(2000)])

    handler.handle(
      divAction(logId: "scroll_with_duration", typed: .divActionScrollBy(action)),
      path: cardId.path, source: .callback, sender: nil
    )

    assertScrollStateEqual(
      getState(),
      PagerViewState(
        numberOfPages: 5,
        currentPage: 0,
        animated: true,
        scrollRequest: ScrollRequest(duration: 2)
      )
    )
  }

  private func makeTabs(
    clock: TestScrollAnimationClock
  ) throws -> (TabContentsView, TabContentsViewModel) {
    setState(TabViewState(selectedPageIndex: 0, countOfPages: 5))
    return try makeScrollTestTabs(path: elementPath, clock: clock, observer: blockStateStorage)
  }

  private func applyPendingStateAndRebind(
    _ view: TabContentsView,
    model: TabContentsViewModel
  ) throws {
    let state = try XCTUnwrap(blockStateStorage.takePendingState(elementPath) as? TabViewState)
    blockStateStorage.setState(path: elementPath, state: state)
    configureTabs(view, model: model, state: state)
  }

  private func configureTabs(
    _ view: TabContentsView,
    model: TabContentsViewModel,
    state: TabViewState
  ) {
    view.configure(
      model: model,
      state: state,
      observer: blockStateStorage,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
  }
}
