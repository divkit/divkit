#if os(iOS)
@testable import LayoutKit
import UIKit
import VGSL
import XCTest

@MainActor
final class TabsScrollDurationViewTests: XCTestCase {
  func test_DirectionChange_CompletesTimedScrollTo() throws {
    let clock = TestScrollAnimationClock()
    let view = try makeTabs(model: tabsModel(), clock: clock)
    view.updateSelectedPageIndexIfNeeded(4, scrollDuration: 2)
    clock.advance(to: 1)

    try configureTabs(view, model: tabsModel(layoutDirection: .rightToLeft), state: TabViewState(
      selectedPageIndex: 4, countOfPages: 5
    ))

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 400, accuracy: 0.01)
    XCTAssertFalse(clock.isScheduled)
  }

  func test_DirectionChange_CompletesRelativeScrollWithoutReapplyingDelta() throws {
    let clock = TestScrollAnimationClock()
    let model = try tabsModel()
    let view = makeTabs(model: model, clock: clock)
    let request = TabViewState(
      selectedPageIndex: 0, countOfPages: 5, scrollRequest: ScrollRequest(
        duration: 2,
        delta: .items(4, wraps: false)
      )
    )
    configureTabs(view, model: model, state: request)
    clock.advance(to: 1)

    try configureTabs(view, model: tabsModel(layoutDirection: .rightToLeft), state: request)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 400, accuracy: 0.01)
    XCTAssertFalse(clock.isScheduled)
  }

  func test_RelativeRequestBeforeFirstLayout_UsesStoredPage() throws {
    let view = TabContentsView(scrollClock: TestScrollAnimationClock())
    let model = try tabsModel()

    configureTabs(view, model: model, state: TabViewState(
      selectedPageIndex: 2, countOfPages: 5, animated: false,
      scrollRequest: ScrollRequest(delta: .items(1, wraps: false))
    ))
    view.frame = scrollBounds
    view.layoutIfNeeded()

    XCTAssertEqual(view.selectedPageIndex, 3)
  }

  func test_TabsContentChange_PreservesTimedDestination() throws {
    let clock = TestScrollAnimationClock()
    let view = try makeTabs(model: tabsModel(), clock: clock)
    view.updateSelectedPageIndexIfNeeded(4, scrollDuration: 2)
    clock.advance(to: 1)

    try configureTabs(
      view, model: tabsModel(text: "Updated"),
      state: TabViewState(selectedPageIndex: 0, countOfPages: 5)
    )
    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 400, accuracy: 0.01)
    XCTAssertEqual(view.selectedPageIndex, 4)
  }

  func test_DynamicHeightTabsContentChange_PreservesTimedDestination() throws {
    let clock = TestScrollAnimationClock()
    let view = try makeTabs(model: tabsModel(pagesHeight: .bySelectedPage), clock: clock)
    view.updateSelectedPageIndexIfNeeded(4, scrollDuration: 2)
    clock.advance(to: 0.75)

    try configureTabs(
      view, model: tabsModel(text: "Updated", pagesHeight: .bySelectedPage),
      state: TabViewState(selectedPageIndex: 0, countOfPages: 5)
    )
    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 400, accuracy: 0.01)
    XCTAssertEqual(view.selectedPageIndex, 4)
  }

  func test_TabsContentChange_PreservesUIKitDestination() throws {
    let view = try makeTabs(model: tabsModel(), clock: TestScrollAnimationClock())
    let window = UIWindow(frame: scrollBounds)
    window.rootViewController = UIViewController()
    window.rootViewController?.view.addSubview(view)
    window.makeKeyAndVisible()
    defer { window.isHidden = true }
    let scrollView = view.scrollView
    scrollView.layoutIfNeeded()
    view.updateSelectedPageIndexIfNeeded(4)
    XCTAssertEqual(view.selectedPageIndex, 4)

    try configureTabs(
      view, model: tabsModel(text: "Updated"),
      state: TabViewState(selectedPageIndex: 0, countOfPages: 5)
    )

    XCTAssertEqual(view.selectedPageIndex, 4)
  }

  func test_TabsRelativeRequest_RebindAfterCompletionKeepsResolvedDestination() throws {
    let model = try tabsModel()
    let view = makeTabs(model: model, clock: TestScrollAnimationClock())
    let request = TabViewState(
      selectedPageIndex: 4, countOfPages: 5, animated: false,
      scrollRequest: ScrollRequest(delta: .items(1, wraps: false))
    )
    configureTabs(view, model: model, state: request)

    configureTabs(view, model: model, state: request)

    XCTAssertEqual(view.selectedPageIndex, 1)
  }

  func test_TabsConfiguredBeforeLayout_PreservesSelectedPage() throws {
    let view = TabContentsView(scrollClock: TestScrollAnimationClock())
    let model = try tabsModel()

    configureTabs(view, model: model, state: TabViewState(selectedPageIndex: 2, countOfPages: 5))

    XCTAssertEqual(view.selectedPageIndex, 2)
  }

  func test_TabsConfiguredBeforeLayout_DoesNotReportScrollCompletion() throws {
    let view = TabContentsView(scrollClock: TestScrollAnimationClock())
    let observer = TabScrollObserver()

    try view.configure(
      model: tabsModel(),
      state: TabViewState(selectedPageIndex: 2, countOfPages: 5),
      observer: observer, overscrollDelegate: nil, renderingDelegate: nil
    )

    XCTAssertTrue(observer.states.isEmpty)
  }

  func test_Tabs_AnimateSelectionForTwoSeconds() throws {
    let clock = TestScrollAnimationClock()
    let model = try tabsModel()
    let view = makeTabs(model: model, clock: clock)
    let scrollView = view.scrollView

    configureTabs(
      view,
      model: model,
      state: tabsTarget(index: 4)
    )
    clock.advance(to: 1)

    XCTAssertEqual(scrollView.contentOffset.x, 200, accuracy: 0.01)
    clock.advance(to: 2)
    XCTAssertEqual(scrollView.contentOffset.x, 400, accuracy: 0.01)
    XCTAssertEqual(view.selectedPageIndex, 4)
  }

  func test_TabsNewAction_InterruptsCurrentTransition() throws {
    let clock = TestScrollAnimationClock()
    let model = try tabsModel()
    let view = makeTabs(model: model, clock: clock)
    configureTabs(
      view,
      model: model,
      state: tabsTarget(index: 4)
    )
    clock.advance(to: 1)

    configureTabs(
      view,
      model: model,
      state: tabsTarget(index: 0)
    )
    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 100, accuracy: 0.01)
    clock.advance(to: 3)
    XCTAssertEqual(scrollView.contentOffset.x, 0, accuracy: 0.01)
    XCTAssertEqual(view.selectedPageIndex, 0)
  }

  func test_TabsNonAnimatedAction_SwitchesInstantly() throws {
    let clock = TestScrollAnimationClock()
    let model = try tabsModel()
    let view = makeTabs(model: model, clock: clock)

    configureTabs(
      view,
      model: model,
      state: TabViewState(
        selectedPageIndex: 4,
        countOfPages: 5,
        animated: false,
        scrollRequest: ScrollRequest(duration: 2)
      )
    )

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 400, accuracy: 0.01)
    XCTAssertEqual(view.selectedPageIndex, 4)
    XCTAssertFalse(clock.isScheduled)
  }

  func test_TabsInstantAction_ToSameTargetCancelsAnimation() throws {
    let clock = TestScrollAnimationClock()
    let model = try tabsModel()
    let view = makeTabs(model: model, clock: clock)
    configureTabs(
      view,
      model: model,
      state: tabsTarget(index: 4)
    )

    configureTabs(
      view,
      model: model,
      state: TabViewState(
        selectedPageIndex: 4,
        countOfPages: 5,
        animated: false,
        scrollRequest: ScrollRequest(duration: 0)
      )
    )
    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 400, accuracy: 0.01)
    XCTAssertFalse(clock.isScheduled)
  }

  func test_TabsReconfigure_DoesNotRestartAnimation() throws {
    let clock = TestScrollAnimationClock()
    let model = try tabsModel()
    let view = makeTabs(model: model, clock: clock)
    let request = tabsTarget(index: 4)
    configureTabs(view, model: model, state: request)
    clock.advance(to: 0.5)

    configureTabs(view, model: model, state: request)
    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 400, accuracy: 0.01)
    XCTAssertFalse(clock.isScheduled)
  }

  func test_TabsSelectedPageHeightChange_PreservesTimedTransition() throws {
    let clock = TestScrollAnimationClock()
    let model = try TabContentsViewModel(
      pages: [
        scrollPage().makeTabPage(with: UIElementPath("short_page")),
        TextBlock(
          widthTrait: .fixed(100), heightTrait: .fixed(200),
          text: NSAttributedString(string: "Tall page")
        ).makeTabPage(with: UIElementPath("tall_page")),
      ],
      pagesHeight: .bySelectedPage,
      path: UIElementPath("tabs")
    )
    let view = makeTabs(model: model, clock: clock)
    configureTabs(view, model: model, state: TabViewState(
      selectedPageIndex: 1, countOfPages: 2, scrollRequest: ScrollRequest(duration: 2)
    ))
    clock.advance(to: 1)

    view.bounds.size.height = 150
    view.layoutIfNeeded()
    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 100, accuracy: 0.01)
    XCTAssertEqual(view.selectedPageIndex, 1)
  }

  func test_TabsBoundsChange_SnapsToNearestPage() throws {
    let clock = TestScrollAnimationClock()
    let model = try tabsModel()
    let view = makeTabs(model: model, clock: clock)
    configureTabs(
      view,
      model: model,
      state: tabsTarget(index: 4)
    )
    clock.advance(to: 0.75)

    view.bounds.size.width = 200
    view.layoutIfNeeded()

    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 200, accuracy: 0.01)
    XCTAssertEqual(view.selectedPageIndex, 1)
    XCTAssertFalse(clock.isScheduled)
  }
}

private func tabsTarget(index: CGFloat) -> TabViewState {
  TabViewState(
    selectedPageIndex: index, countOfPages: 5, scrollRequest: ScrollRequest(duration: 2)
  )
}

private final class TabScrollObserver: ElementStateObserver {
  private(set) var states: [TabViewState] = []

  func elementStateChanged(_ state: ElementState, forPath _: UIElementPath) {
    if let state = state as? TabViewState {
      states.append(state)
    }
  }
}

private func tabsModel(
  text: String = "Page",
  pagesHeight: TabbedPagesHeightMode = .byHighestPage,
  layoutDirection: UserInterfaceLayoutDirection = .leftToRight
) throws -> TabContentsViewModel {
  try TabContentsViewModel(
    pages: (0..<5).map { scrollPage(text: text).makeTabPage(with: UIElementPath("page_\($0)")) },
    pagesHeight: pagesHeight,
    path: UIElementPath("tabs"),
    layoutDirection: layoutDirection
  )
}

@MainActor
private func makeTabs(
  model: TabContentsViewModel,
  clock: TestScrollAnimationClock
) -> TabContentsView {
  let view = TabContentsView(scrollClock: clock)
  view.frame = scrollBounds
  configureTabs(view, model: model, state: TabViewState(selectedPageIndex: 0, countOfPages: 5))
  view.layoutIfNeeded()
  return view
}

@MainActor
private func configureTabs(
  _ view: TabContentsView,
  model: TabContentsViewModel,
  state: TabViewState
) {
  view.configure(
    model: model,
    state: state,
    observer: nil,
    overscrollDelegate: nil,
    renderingDelegate: nil
  )
}
#endif
