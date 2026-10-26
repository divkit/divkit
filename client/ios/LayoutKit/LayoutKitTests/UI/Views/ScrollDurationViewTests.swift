#if os(iOS)
@testable import LayoutKit
import UIKit
import VGSL
import XCTest

@MainActor
final class ScrollDurationViewTests: XCTestCase {
  func test_ImmediatePagingScroll_ReportsTargetWithoutAnimation() {
    let model = galleryModel(
      direction: .horizontal, scrollMode: .autoPaging(inertionEnabled: false)
    )
    let view = makeGallery(model: model, clock: TestScrollAnimationClock())
    let observer = ScrollPositionObserver()

    view.configure(
      model: model,
      state: GalleryViewState(
        contentPageIndex: 0, itemsCount: 5, animated: false,
        scrollRequest: ScrollRequest(delta: .items(1, wraps: false))
      ),
      observer: observer, overscrollDelegate: nil, renderingDelegate: nil
    )

    XCTAssertEqual(observer.state?.contentPosition, .paging(index: 1))
    XCTAssertEqual(observer.state?.animated, false)
  }

  func test_GalleryDirectionChange_CompletesRelativeTimedTarget() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: clock)
    let request = GalleryViewState(
      contentPageIndex: 0, itemsCount: 5, animated: true,
      scrollRequest: ScrollRequest(duration: 2, delta: .items(4, wraps: false))
    )
    configureGallery(view, model: model, state: request)
    clock.advance(to: 1)

    configureGallery(view, model: galleryModel(
      direction: .horizontal, layoutDirection: .rightToLeft
    ), state: request)

    XCTAssertEqual(view.scrollView.contentOffset.x, 400, accuracy: 0.01)
    XCTAssertFalse(clock.isScheduled)
  }

  func test_PagerFirstLayout_PerformsInitialSelectionOnce() async throws {
    let host = PagerSelectionHost()
    let block = try pagerBlock(
      model: galleryModel(direction: .horizontal),
      state: PagerViewState(numberOfPages: 5, currentPage: 0, animated: false)
    )

    try await layoutPager(block, in: host)

    XCTAssertEqual(host.selectedPages, [0])
    XCTAssertEqual(host.actionPaths, [UIElementPath("selected")])
  }

  func test_PagerDeferredRequest_PerformsTargetSelectionOnce() async throws {
    let host = PagerSelectionHost()
    let block = try pagerBlock(
      model: galleryModel(direction: .horizontal),
      state: PagerViewState(
        numberOfPages: 5, currentPage: 2, animated: false, scrollRequest: ScrollRequest()
      )
    )

    try await layoutPager(block, in: host)

    XCTAssertEqual(host.selectedPages, [2])
    XCTAssertEqual(host.actionPaths, [UIElementPath("selected")])
  }

  func test_PagerDragAfterImmediateRequest_DoesNotRepeatSelection() async throws {
    let host = PagerSelectionHost()
    let block = try pagerBlock(
      model: galleryModel(direction: .horizontal, scrollMode: .autoPaging(inertionEnabled: false)),
      state: PagerViewState(
        numberOfPages: 5, currentPage: 1, animated: false, scrollRequest: ScrollRequest()
      )
    )
    let view = try await layoutPager(block, in: host)
    let gallery = view.subviews[0] as! GalleryView
    let scrollView = gallery.scrollView

    scrollView.delegate?.scrollViewWillBeginDragging?(scrollView)
    scrollView.contentOffset.x += 2

    XCTAssertEqual(host.selectedPages, [1])
    XCTAssertEqual(host.actionPaths, [UIElementPath("selected")])
  }

  func test_PagerClampedImmediateRequestAfterDrag_DoesNotRepeatSelection() async throws {
    let host = PagerSelectionHost()
    let model = galleryModel(direction: .horizontal, scrollMode: .autoPaging(inertionEnabled: false))
    let block = try pagerBlock(model: model, state: PagerViewState(
      numberOfPages: 5, currentPage: 3, animated: false, scrollRequest: ScrollRequest()
    ))
    let view = try await layoutPager(block, in: host)
    let gallery = view.subviews[0] as! GalleryView
    let scrollView = gallery.scrollView
    scrollView.delegate?.scrollViewWillBeginDragging?(scrollView)
    scrollView.contentOffset.x = 360
    scrollView.contentOffset.x = 400
    scrollView.delegate?.scrollViewDidEndDragging?(scrollView, willDecelerate: false)
    let request = try pagerBlock(model: model, state: PagerViewState(
      numberOfPages: 5, currentPage: 4, animated: false,
      scrollRequest: ScrollRequest(delta: .items(1, wraps: false))
    ))

    request.configureBlockView(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)

    XCTAssertEqual(host.selectedPages, [3, 4])
    XCTAssertEqual(host.actionPaths, [UIElementPath("selected"), UIElementPath("selected")])
  }

  func test_HorizontalGallery_AnimatesOffsetForTwoSeconds() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: clock)
    let scrollView = view.scrollView

    configureGallery(view, model: model, state: galleryTarget(offset: 400))
    clock.advance(to: 1)

    XCTAssertEqual(scrollView.contentOffset.x, 200, accuracy: 0.01)
    clock.advance(to: 2)
    XCTAssertEqual(scrollView.contentOffset.x, 400, accuracy: 0.01)
    XCTAssertFalse(view.state.isScrolling)
  }

  func test_VerticalGallery_AnimatesOffsetForTwoSeconds() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .vertical)
    let view = makeGallery(model: model, clock: clock)
    let scrollView = view.scrollView

    configureGallery(view, model: model, state: galleryTarget(offset: 400))
    clock.advance(to: 1)

    XCTAssertEqual(scrollView.contentOffset.y, 200, accuracy: 0.01)
    clock.advance(to: 2)
    XCTAssertEqual(scrollView.contentOffset.y, 400, accuracy: 0.01)
    XCTAssertFalse(view.state.isScrolling)
  }

  func test_InfinitePagerRelativeClamp_WrapsAtLastPage() throws {
    let model = GalleryViewModel(
      items: (0..<9).map { _ in .init(crossAlignment: .leading, content: scrollPage()) },
      metrics: GalleryViewMetrics(gaps: Array(repeating: 0, count: 10)),
      scrollMode: .autoPaging(inertionEnabled: false),
      path: UIElementPath("pager"), bufferSize: 2, infiniteScroll: true
    )
    let view = try makePager(
      model: model,
      state: PagerViewState(
        numberOfPages: 5,
        currentPage: 4,
        animated: false,
        isInfiniteScrollable: true
      )
    )
    let request = try pagerBlock(model: model, state: PagerViewState(
      numberOfPages: 5, currentPage: 4, animated: false, scrollRequest: ScrollRequest(delta: .items(
        1,
        wraps: false
      )), isInfiniteScrollable: true
    ))

    request.configureBlockView(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)

    let gallery = view.subviews[0] as! GalleryView
    XCTAssertEqual(gallery.state.contentPosition, .paging(index: 2))
  }

  func test_InfinitePagerInterruption_PreservesFractionalPositionAcrossBuffer() {
    let clock = TestScrollAnimationClock()
    let model = GalleryViewModel(
      items: (0..<9).map { _ in .init(crossAlignment: .leading, content: scrollPage()) },
      metrics: GalleryViewMetrics(gaps: Array(repeating: 0, count: 10)),
      scrollMode: .autoPaging(inertionEnabled: false),
      path: UIElementPath("pager"), bufferSize: 2, infiniteScroll: true
    )
    let view = makeGallery(model: model, clock: clock)
    configureGallery(view, model: model, state: GalleryViewState(
      contentPageIndex: 2, itemsCount: 9, animated: false
    ))
    configureGallery(view, model: model, state: GalleryViewState(
      contentPageIndex: 6, itemsCount: 9, animated: true,
      scrollRequest: ScrollRequest(duration: 2)
    ))
    clock.advance(to: 1.6)
    let scrollView = view.scrollView
    let offsetBeforeInterruption = scrollView.contentOffset.x

    configureGallery(view, model: model, state: GalleryViewState(
      contentPageIndex: 2, itemsCount: 9, animated: true,
      scrollRequest: ScrollRequest(duration: 2)
    ))

    XCTAssertEqual(offsetBeforeInterruption - scrollView.contentOffset.x, 500, accuracy: 0.01)
  }

  func test_TimedGallery_ReportsActualPositionBeforeFirstFrame() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: clock)
    let observer = ScrollPositionObserver()

    view.configure(
      model: model, state: galleryTarget(offset: 400),
      observer: observer, overscrollDelegate: nil, renderingDelegate: nil
    )

    XCTAssertEqual(observer.state?.contentPosition, .offset(0))
  }

  func test_ReusedGallery_ReportsScrollRangeForNewPath() {
    let horizontalModel = galleryModel(direction: .horizontal)
    let view = makeGallery(model: horizontalModel, clock: TestScrollAnimationClock())
    let observer = ScrollPositionObserver()
    let verticalModel = GalleryViewModel(
      items: horizontalModel.items,
      metrics: GalleryViewMetrics(gaps: Array(repeating: 0, count: 6)),
      scrollMode: .default,
      path: UIElementPath("vertical_gallery"),
      direction: .vertical
    )

    view.configure(
      model: verticalModel,
      state: GalleryViewState(contentOffset: 0, itemsCount: 5),
      observer: observer, overscrollDelegate: nil, renderingDelegate: nil
    )
    view.layoutIfNeeded()

    XCTAssertEqual(observer.state?.scrollRange, 400)
    XCTAssertEqual(observer.path, UIElementPath("vertical_gallery"))
  }

  func test_GalleryReconfigure_DoesNotRestartAnimation() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: clock)
    let request = galleryTarget(offset: 400)
    configureGallery(view, model: model, state: request)
    clock.advance(to: 1)

    configureGallery(view, model: model, state: request)
    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 400, accuracy: 0.01)
    XCTAssertFalse(clock.isScheduled)
  }

  func test_GalleryInstantAction_AtCurrentOffsetCancelsAnimation() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: clock)
    configureGallery(view, model: model, state: galleryTarget(offset: 400))
    clock.advance(to: 1)
    let request = GalleryViewState(
      contentPosition: view.state.contentPosition,
      itemsCount: 5,
      isScrolling: false,
      scrollRange: 400,
      animated: false,
      scrollRequest: ScrollRequest(duration: 0)
    )

    configureGallery(view, model: model, state: request)
    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 200, accuracy: 0.01)
    XCTAssertFalse(clock.isScheduled)
  }

  func test_GalleryModelChange_CancelsAnimation() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: clock)
    configureGallery(view, model: model, state: galleryTarget(offset: 400))
    clock.advance(to: 1)

    configureGallery(
      view,
      model: galleryModel(direction: .vertical),
      state: GalleryViewState(contentOffset: 0, itemsCount: 5)
    )

    XCTAssertFalse(clock.isScheduled)
    XCTAssertFalse(view.state.isScrolling)
    XCTAssertEqual(view.state.contentPosition, .offset(0))
  }

  func test_GalleryBoundsChange_CancelsOldTrajectory() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: clock)
    configureGallery(view, model: model, state: galleryTarget(offset: 400))
    clock.advance(to: 1)

    view.bounds.size.width = 200
    view.layoutIfNeeded()
    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 200, accuracy: 0.01)
    XCTAssertFalse(clock.isScheduled)
  }

  func test_AutoPagingGalleryBoundsChange_SnapsToNearestPage() {
    assertPagingGalleryResize(
      scrollMode: .autoPaging(inertionEnabled: false), expectedOffset: 50
    )
  }

  func test_FixedPagingGalleryBoundsChange_SnapsToNearestPage() {
    assertPagingGalleryResize(scrollMode: .fixedPaging(pageSize: 100), expectedOffset: 100)
  }

  func test_TimedGallery_SuppressesIntermediateStates() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: clock)
    let observer = ScrollPositionObserver()

    view.configure(
      model: model,
      state: galleryTarget(offset: 400),
      observer: observer,
      overscrollDelegate: nil,
      renderingDelegate: nil,
      observerNotificationMode: .suppressIntermediateScrollStates
    )
    clock.advance(to: 1)

    XCTAssertNil(observer.state)
  }

  func test_GalleryContentChange_PreservesTimedDestination() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: clock)
    configureGallery(view, model: model, state: galleryTarget(offset: 400))
    clock.advance(to: 1)

    configureGallery(
      view, model: galleryModel(direction: .horizontal, text: "Updated"),
      state: GalleryViewState(contentOffset: 0, itemsCount: 5)
    )
    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 400, accuracy: 0.01)
    XCTAssertFalse(view.state.isScrolling)
  }

  func test_GalleryRelativeOffset_UsesCurrentPositionDuringTimedScroll() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: clock)
    configureGallery(view, model: model, state: galleryTarget(offset: 400))
    clock.advance(to: 1)

    configureGallery(view, model: model, state: GalleryViewState(
      contentPosition: .offset(400), itemsCount: 5, isScrolling: false,
      animated: true, scrollRequest: ScrollRequest(duration: 2, delta: .offset(100, wraps: false))
    ))
    clock.advance(to: 3)

    XCTAssertEqual(view.state.contentPosition, .offset(300, firstVisibleItemIndex: 3))
    XCTAssertFalse(view.state.isScrolling)
  }

  func test_GalleryRelativeItems_UsesCurrentPositionDuringTimedScroll() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: clock)
    configureGallery(view, model: model, state: galleryTarget(offset: 400))
    clock.advance(to: 1)

    configureGallery(view, model: model, state: GalleryViewState(
      contentPageIndex: 4, itemsCount: 5, animated: true,
      scrollRequest: ScrollRequest(duration: 2, delta: .items(-1, wraps: false))
    ))
    clock.advance(to: 3)

    XCTAssertEqual(view.state.contentPosition, .offset(100, firstVisibleItemIndex: 1))
    XCTAssertFalse(view.state.isScrolling)
  }

  func test_GalleryRelativeRequest_RebindAfterCompletionKeepsResolvedDestination() {
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: TestScrollAnimationClock())
    let request = GalleryViewState(
      contentPosition: .offset(400), itemsCount: 5, isScrolling: false,
      animated: false, scrollRequest: ScrollRequest(delta: .offset(100, wraps: false))
    )
    configureGallery(view, model: model, state: request)

    configureGallery(view, model: model, state: request)

    XCTAssertEqual(view.state.contentPosition, .offset(100, firstVisibleItemIndex: 1))
  }

  func test_GalleryRelativeOffset_RingWrapsFromActualPosition() {
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: TestScrollAnimationClock())

    configureGallery(view, model: model, state: GalleryViewState(
      contentPosition: .offset(100), itemsCount: 5, isScrolling: false,
      animated: false, scrollRequest: ScrollRequest(delta: .offset(-100, wraps: true))
    ))

    XCTAssertEqual(view.state.contentPosition, .offset(300, firstVisibleItemIndex: 3))
  }

  func test_PagerRelativeItems_AreForwardedToGallery() throws {
    let model = galleryModel(direction: .horizontal)
    let view = try makePager(
      model: model, state: PagerViewState(numberOfPages: 5, currentPage: 0, animated: false)
    )
    let block = try pagerBlock(model: model, state: PagerViewState(
      numberOfPages: 5, currentPage: 4, animated: false, scrollRequest: ScrollRequest(delta: .items(
        1,
        wraps: false
      ))
    ))

    block.configureBlockView(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)

    let gallery = view.subviews[0] as! GalleryView
    let scrollView = gallery.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 100, accuracy: 0.01)
  }

  func test_PagerRelativeItems_UseNearestPageDuringTimedScroll() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(
      direction: .horizontal,
      scrollMode: .autoPaging(inertionEnabled: false)
    )
    let view = makeGallery(model: model, clock: clock)
    let layoutFactory: GalleryView.LayoutFactory = { model, bounds in
      PagerViewLayout(
        model: model,
        layoutMode: .pageSize(RelativeValue(rawValue: 1)),
        boundsSize: bounds
      )
    }
    view.configure(
      model: model,
      state: GalleryViewState(
        contentPageIndex: 4, itemsCount: 5, animated: true, scrollRequest: ScrollRequest(duration: 2
        )
      ),
      layoutFactory: layoutFactory,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
    clock.advance(to: 1.2)

    view.configure(model: model, state: GalleryViewState(
      contentPageIndex: 0,
      itemsCount: 5,
      animated: true,
      scrollRequest: ScrollRequest(duration: 2, delta: .items(1, wraps: false))
    ), layoutFactory: layoutFactory, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)
    clock.advance(to: 3.2)

    XCTAssertEqual(view.state.contentPosition, .paging(index: 4))
    XCTAssertFalse(view.state.isScrolling)
  }

  func test_GalleryLayoutChange_RetargetsWithinOriginalDuration() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(
      direction: .horizontal,
      scrollMode: .autoPaging(inertionEnabled: false)
    )
    let view = makeGallery(model: model, clock: clock)
    let request = GalleryViewState(
      contentPageIndex: 4, itemsCount: 5, animated: true,
      scrollRequest: ScrollRequest(duration: 2)
    )
    configureGallery(view, model: model, state: request)
    clock.advance(to: 1)
    let changedModel = GalleryViewModel(
      items: model.items, metrics: GalleryViewMetrics(gaps: Array(repeating: 10, count: 6)),
      scrollMode: .autoPaging(inertionEnabled: false), path: UIElementPath("gallery")
    )

    configureGallery(view, model: changedModel, state: request)
    clock.advance(to: 2)

    XCTAssertEqual(view.state.contentPosition, .paging(index: 4))
    XCTAssertFalse(view.state.isScrolling)
    XCTAssertFalse(clock.isScheduled)
  }

  func test_GalleryNewAction_InterruptsAtCurrentPosition() {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal)
    let view = makeGallery(model: model, clock: clock)
    configureGallery(view, model: model, state: galleryTarget(offset: 400))
    clock.advance(to: 1)

    configureGallery(view, model: model, state: galleryTarget(offset: 0))
    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 100, accuracy: 0.01)
    clock.advance(to: 3)
    XCTAssertEqual(scrollView.contentOffset.x, 0, accuracy: 0.01)
  }

  func test_Pager_ZeroDurationIsForwardedToGallery() throws {
    let model = galleryModel(direction: .horizontal)
    let view = try makePager(
      model: model,
      state: PagerViewState(numberOfPages: 5, currentPage: 0, animated: true)
    )
    let block = try pagerBlock(
      model: model,
      state: PagerViewState(
        numberOfPages: 5,
        currentPage: 4,
        animated: true,
        scrollRequest: ScrollRequest(duration: 0)
      )
    )

    block.configureBlockView(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)

    let gallery = view.subviews[0] as! GalleryView
    let scrollView = gallery.scrollView
    XCTAssertEqual(scrollView.contentOffset.x, 400, accuracy: 0.01)
  }

  private func assertPagingGalleryResize(
    scrollMode: GalleryViewModel.ScrollMode,
    expectedOffset: CGFloat,
    file: StaticString = #filePath,
    line: UInt = #line
  ) {
    let clock = TestScrollAnimationClock()
    let model = galleryModel(direction: .horizontal, scrollMode: scrollMode)
    let view = makeGallery(model: model, clock: clock)
    let request = GalleryViewState(
      contentPageIndex: 4, itemsCount: 5, animated: true,
      scrollRequest: ScrollRequest(duration: 2)
    )
    configureGallery(view, model: model, state: request)
    clock.advance(to: 0.75)

    view.bounds.size.width = 200
    view.layoutIfNeeded()
    clock.advance(to: 2)

    let scrollView = view.scrollView
    XCTAssertEqual(
      scrollView.contentOffset.x,
      expectedOffset,
      accuracy: 0.01,
      file: file,
      line: line
    )
    XCTAssertEqual(view.state.contentPosition, .paging(index: 1), file: file, line: line)
    XCTAssertFalse(view.state.isScrolling, file: file, line: line)
  }

  @discardableResult
  private func layoutPager(_ block: PagerBlock, in host: UIView) async throws -> BlockView {
    let view = block.makeBlockView()
    host.addSubview(view)
    block.configureBlockView(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)
    view.frame = scrollBounds
    view.layoutIfNeeded()
    let synchronized = expectation(description: "Pending pager synchronization completes")
    DispatchQueue.main.async { synchronized.fulfill() }
    await fulfillment(of: [synchronized], timeout: 1)
    return view
  }
}

private final class ScrollPositionObserver: ElementStateObserver {
  private(set) var state: GalleryViewState?
  private(set) var path: UIElementPath?

  func elementStateChanged(_ state: ElementState, forPath path: UIElementPath) {
    self.state = state as? GalleryViewState
    self.path = path
  }
}

private final class PagerSelectionHost: UIView, PagerEventHandling, UIActionEventPerforming {
  private(set) var selectedPages: [Int] = []
  private(set) var actionPaths: [UIElementPath] = []

  func handle(event: PagerSelectedPageChangedEvent, from _: AnyObject) {
    selectedPages.append(event.selectedPageIndex)
  }

  func perform(uiActionEvent event: UIActionEvent, from _: AnyObject) {
    actionPaths.append(event.uiAction.path)
  }
}

private func galleryModel(
  direction: ScrollDirection,
  scrollMode: GalleryViewModel.ScrollMode = .default,
  text: String = "Page",
  layoutDirection: UserInterfaceLayoutDirection = .leftToRight
) -> GalleryViewModel {
  GalleryViewModel(
    items: (0..<5).map { _ in .init(crossAlignment: .leading, content: scrollPage(text: text)) },
    layoutDirection: layoutDirection,
    metrics: GalleryViewMetrics(gaps: Array(repeating: 0, count: 6)),
    scrollMode: scrollMode,
    path: UIElementPath("gallery"),
    direction: direction
  )
}

private func pagerBlock(model: GalleryViewModel, state: PagerViewState) throws -> PagerBlock {
  try PagerBlock(
    pagerPath: nil,
    layoutMode: .pageSize(RelativeValue(rawValue: 1)),
    gallery: model,
    selectedActions: Array(repeating: [UserInterfaceAction(path: "selected")], count: 5),
    state: state,
    widthTrait: .fixed(100),
    heightTrait: .fixed(100)
  )
}

private func galleryTarget(offset: CGFloat) -> GalleryViewState {
  GalleryViewState(
    contentPosition: .offset(offset),
    itemsCount: 5,
    isScrolling: false,
    scrollRange: 400,
    animated: true,
    scrollRequest: ScrollRequest(duration: 2)
  )
}

@MainActor
private func makePager(model: GalleryViewModel, state: PagerViewState) throws -> BlockView {
  let block = try pagerBlock(model: model, state: state)
  let view = block.makeBlockView()
  view.frame = scrollBounds
  block.configureBlockView(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)
  view.layoutIfNeeded()
  return view
}

@MainActor
private func makeGallery(model: GalleryViewModel, clock: TestScrollAnimationClock) -> GalleryView {
  let view = GalleryView(frame: scrollBounds, scrollClock: clock)
  configureGallery(view, model: model, state: GalleryViewState(contentOffset: 0, itemsCount: 5))
  view.layoutIfNeeded()
  return view
}

@MainActor
private func configureGallery(
  _ view: GalleryView,
  model: GalleryViewModel,
  state: GalleryViewState
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
