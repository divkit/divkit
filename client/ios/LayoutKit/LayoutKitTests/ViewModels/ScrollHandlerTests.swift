#if os(iOS)
import CoreGraphics
@testable import LayoutKit
import Testing
import UIKit
import VGSL

@MainActor
@Suite
struct ScrollHandlerTests {
  private let layout = MockLayout.horizontal(pageCount: 5, pageSize: 100)
  private let delegate = MockScrollHandlerDelegate()

  // MARK: - State transitions

  @Test
  func initialState_finishScrolling_noStartOffset() {
    let handler = makeHandler()
    handler.onDidEndDecelerating(UIScrollView())
    #expect(delegate.finishScrollingCalls.count == 1)
    #expect(delegate.finishScrollingCalls.first! == nil)
  }

  @Test
  func dragging_thenEndWithoutDeceleration_finishes() {
    let handler = makeHandler()
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)

    handler.onWillBeginDragging(scrollView)
    handler.onDidEndDragging(scrollView, willDecelerate: false)

    #expect(delegate.finishScrollingCalls == [0])
  }

  @Test
  func dragging_thenDecelerate_thenEndDecelerating_finishes() {
    let handler = makeHandler()
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)

    handler.onWillBeginDragging(scrollView)
    handler.onDidEndDragging(scrollView, willDecelerate: true)

    #expect(delegate.finishScrollingCalls.isEmpty)

    handler.onDidEndDecelerating(scrollView)
    #expect(delegate.finishScrollingCalls.count == 1)
  }

  @Test
  func endScrollingAnimation_finishes() {
    let handler = makeHandler()
    let scrollView = UIScrollView()

    handler.onDidEndScrollingAnimation(scrollView)
    #expect(delegate.finishScrollingCalls.count == 1)
  }

  // MARK: - Free mode (no offset modification)

  @Test
  func freeMode_doesNotModifyTargetOffset() {
    let handler = makeHandler(mode: .free)
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)

    handler.onWillBeginDragging(scrollView)

    var target = CGPoint(x: 150, y: 0)
    handler.onWillEndDragging(
      scrollView,
      withVelocity: CGPoint(x: 1, y: 0),
      targetContentOffset: &target
    )
    #expect(target == CGPoint(x: 150, y: 0))
  }

  // MARK: - Fixed size paging

  @Test
  func fixedPaging_snapsToPageOrigin() {
    let handler = makeHandler(mode: .fixedSizePaging)
    handler.configurePager(
      pageOrigins: [0, 100, 200, 300, 400],
      isHorizontal: true
    )
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)

    handler.onWillBeginDragging(scrollView)

    var target = CGPoint(x: 130, y: 0)
    handler.onWillEndDragging(
      scrollView,
      withVelocity: CGPoint(x: 0.5, y: 0),
      targetContentOffset: &target
    )
    #expect(target.x == 100)
  }

  @Test
  func fixedPaging_velocityNudgeAfterOffsetJump_requiresSyncOnDragStart() {
    let handler = makeHandler(mode: .fixedSizePaging)
    handler.configurePager(
      pageOrigins: [0, 100, 200, 300, 400],
      isHorizontal: true
    )
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)
    scrollView.contentOffset = CGPoint(x: 300, y: 0)

    handler.onWillBeginDragging(scrollView)

    var target = CGPoint(x: 310, y: 0)
    handler.onWillEndDragging(
      scrollView,
      withVelocity: CGPoint(x: 800, y: 0),
      targetContentOffset: &target
    )
    #expect(target.x == 400)
  }

  // MARK: - Layout paging (single page)

  @Test
  func layoutPagingSinglePage_snapsToNearestPage() {
    let handler = makeHandler(mode: .layoutPaging(step: .singlePage))
    handler.configurePager(
      pageOrigins: [0, 100, 200, 300, 400],
      isHorizontal: true
    )
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)

    handler.onWillBeginDragging(scrollView)

    var target = CGPoint(x: 250, y: 0)
    handler.onWillEndDragging(
      scrollView,
      withVelocity: CGPoint(x: 0.5, y: 0),
      targetContentOffset: &target
    )
    #expect(target.x == 100)
  }

  @Test
  func layoutPagingSinglePage_respectsVelocityDirection() {
    let handler = makeHandler(mode: .layoutPaging(step: .singlePage))
    handler.configurePager(
      pageOrigins: [0, 100, 200, 300, 400],
      isHorizontal: true
    )
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)
    scrollView.contentOffset = CGPoint(x: 200, y: 0)

    handler.onWillBeginDragging(scrollView)

    var target = CGPoint(x: 150, y: 0)
    handler.onWillEndDragging(
      scrollView,
      withVelocity: CGPoint(x: -0.5, y: 0),
      targetContentOffset: &target
    )
    #expect(target.x == 100)
  }

  // MARK: - Layout paging (multiple pages)

  @Test
  func layoutPagingMultiplePages_snapsToPageOrigin() {
    let handler = makeHandler(mode: .layoutPaging(step: .multiplePages))
    handler.configurePager(
      pageOrigins: [0, 100, 200, 300, 400],
      isHorizontal: true
    )
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)

    handler.onWillBeginDragging(scrollView)

    var target = CGPoint(x: 250, y: 0)
    handler.onWillEndDragging(
      scrollView,
      withVelocity: CGPoint(x: 0.5, y: 0),
      targetContentOffset: &target
    )
    #expect(target.x == 300)
  }

  // MARK: - Vertical direction

  @Test
  func verticalDirection_usesYAxis() {
    let verticalLayout = MockLayout.vertical(pageCount: 5, pageSize: 100)
    let handler = ScrollHandler(layout: verticalLayout)
    handler.delegate = delegate
    handler.direction = .vertical
    handler.mode = .fixedSizePaging
    handler.configurePager(
      pageOrigins: [0, 100, 200, 300, 400],
      isHorizontal: false
    )

    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 100, height: 500)

    handler.onWillBeginDragging(scrollView)

    var target = CGPoint(x: 0, y: 130)
    handler.onWillEndDragging(
      scrollView,
      withVelocity: CGPoint(x: 0, y: 0.5),
      targetContentOffset: &target
    )
    #expect(target.y == 100)
  }

  // MARK: - handlePositionChange

  @Test
  func handlePositionChange_callsDelegateForNewPosition() {
    let handler = makeHandler()

    handler.handlePositionChange(
      oldPosition: .paging(index: 0),
      newPosition: .paging(index: 2),
      newLayout: false,
      animated: true
    )

    #expect(delegate.updateOffsetToPositionCalls.count == 1)
    let call = delegate.updateOffsetToPositionCalls.first
    #expect(call?.position == .paging(index: 2))
    #expect(call?.animated == true)
  }

  @Test
  func handlePositionChange_skipsWhenPositionUnchanged() {
    let handler = makeHandler()

    handler.handlePositionChange(
      oldPosition: .paging(index: 1),
      newPosition: .paging(index: 1),
      newLayout: false,
      animated: true
    )

    #expect(delegate.updateOffsetToPositionCalls.isEmpty)
  }

  @Test
  func handlePositionChange_skipsDuringUserDrag() {
    let handler = makeHandler()
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)

    handler.onWillBeginDragging(scrollView)

    handler.handlePositionChange(
      oldPosition: .paging(index: 0),
      newPosition: .paging(index: 2),
      newLayout: false,
      animated: true
    )

    #expect(delegate.updateOffsetToPositionCalls.isEmpty)
  }

  @Test
  func handlePositionChange_usesDetachedOffsetForNewLayout() {
    let handler = makeHandler()

    handler.handlePositionChange(
      oldPosition: .paging(index: 0),
      newPosition: .offset(200, firstVisibleItemIndex: 2),
      newLayout: true,
      animated: false
    )

    #expect(delegate.updateOffsetToPositionCalls == [
      MockScrollHandlerDelegate.PositionCall(
        position: .offset(200, firstVisibleItemIndex: 2), animated: false
      ),
    ])
  }

  @Test
  func handlePositionChange_newLayout_resolvesPagingOffsetThroughLayout() {
    let handler = makeHandler()

    handler.handlePositionChange(
      oldPosition: .paging(index: 0),
      newPosition: .paging(index: 2),
      newLayout: true,
      animated: false
    )

    #expect(delegate.updateOffsetToPositionCalls == [
      MockScrollHandlerDelegate.PositionCall(
        position: .paging(index: 2), animated: false
      ),
    ])
  }

  // MARK: - updateState (onDidScroll)

  @Test
  func onDidScroll_inPagingMode_reportsPagePosition() {
    let handler = makeHandler(mode: .fixedSizePaging)
    handler.configurePager(
      pageOrigins: [0, 100, 200, 300, 400],
      isHorizontal: true
    )
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)
    scrollView.contentOffset = CGPoint(x: 200, y: 0)

    handler.onDidScroll(scrollView)

    #expect(delegate.updateGalleryStateCalls.count == 1)
    let call = delegate.updateGalleryStateCalls.first
    #expect(call?.position == .paging(index: 2))
  }

  @Test
  func onDidScroll_inFreeMode_reportsOffsetPosition() {
    let handler = makeHandler(mode: .free)
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)
    scrollView.contentOffset = CGPoint(x: 150, y: 0)

    handler.onDidScroll(scrollView)

    #expect(delegate.updateGalleryStateCalls.count == 1)
    if case let .offset(value, _) = delegate.updateGalleryStateCalls.first?.position {
      #expect(value == 150)
    } else {
      Issue.record("Expected .offset position")
    }
  }

  // MARK: - configurePager / clearPager

  @Test
  func clearPager_disablesPagingBehavior() {
    let handler = makeHandler(mode: .fixedSizePaging)
    handler.configurePager(
      pageOrigins: [0, 100, 200, 300, 400],
      isHorizontal: true
    )
    handler.clearPager()

    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)

    handler.onWillBeginDragging(scrollView)

    var target = CGPoint(x: 130, y: 0)
    handler.onWillEndDragging(
      scrollView,
      withVelocity: CGPoint(x: 0.5, y: 0),
      targetContentOffset: &target
    )
    #expect(target == CGPoint(x: 130, y: 0))
  }

  // MARK: - configureInfiniteScroll

  @Test
  func configureInfiniteScroll_disabled_noLoop() {
    let handler = makeHandler()
    handler.configureInfiniteScroll(
      enabled: false,
      bufferSize: 1,
      boundsSize: 100,
      alignment: .center,
      insetMode: .fixed(values: .zero)
    )

    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)

    handler.onWillBeginDragging(scrollView)

    var target = CGPoint(x: 100, y: 0)
    handler.onWillEndDragging(
      scrollView,
      withVelocity: CGPoint(x: 1, y: 0),
      targetContentOffset: &target
    )

    handler.onDidScroll(scrollView)

    #expect(delegate.updateOffsetDetachedCalls.isEmpty)
  }

  // MARK: - finishScrolling passes startOffset

  @Test
  func finishScrolling_passesStartOffsetFromDragging() {
    let handler = makeHandler()
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)
    scrollView.contentOffset = CGPoint(x: 50, y: 0)

    handler.onWillBeginDragging(scrollView)
    handler.onDidEndDragging(scrollView, willDecelerate: false)

    #expect(delegate.finishScrollingCalls == [50])
  }

  @Test
  func finishScrolling_passesStartOffsetFromSettling() {
    let handler = makeHandler()
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)
    scrollView.contentOffset = CGPoint(x: 75, y: 0)

    handler.onWillBeginDragging(scrollView)

    var target = CGPoint(x: 200, y: 0)
    handler.onWillEndDragging(
      scrollView,
      withVelocity: CGPoint(x: 1, y: 0),
      targetContentOffset: &target
    )

    handler.onDidEndDecelerating(scrollView)

    #expect(delegate.finishScrollingCalls == [75])
  }

  // MARK: - evaluatePagingDuringDrag (interrupts scroll on page change)

  @Test
  func dragging_interruptsOnFullPageChange_inSinglePageMode() {
    let handler = makeHandler(mode: .layoutPaging(step: .singlePage))
    handler.configurePager(
      pageOrigins: [0, 100, 200, 300, 400],
      isHorizontal: true
    )
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)

    handler.onWillBeginDragging(scrollView)

    scrollView.contentOffset = CGPoint(x: 150, y: 0)
    handler.onDidScroll(scrollView)

    #expect(delegate.interruptScrollingCalls == 1)
  }

  @Test
  func dragging_doesNotInterruptInFreeModeWhenCrossingPageBoundary() {
    let handler = makeHandler(mode: .free)
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)

    handler.onWillBeginDragging(scrollView)

    scrollView.contentOffset = CGPoint(x: 150, y: 0)
    handler.onDidScroll(scrollView)

    #expect(delegate.interruptScrollingCalls == 0)
  }

  @Test
  func dragging_doesNotInterruptWithinSamePage() {
    let handler = makeHandler(mode: .fixedSizePaging)
    handler.configurePager(
      pageOrigins: [0, 100, 200, 300, 400],
      isHorizontal: true
    )
    let scrollView = UIScrollView()
    scrollView.contentSize = CGSize(width: 500, height: 100)

    handler.onWillBeginDragging(scrollView)

    scrollView.contentOffset = CGPoint(x: 40, y: 0)
    handler.onDidScroll(scrollView)

    #expect(delegate.interruptScrollingCalls == 0)
  }

  private func makeHandler(
    mode: ScrollHandler.Mode = .free,
    direction: ScrollDirection = .horizontal
  ) -> ScrollHandler {
    let handler = ScrollHandler(layout: layout)
    handler.delegate = delegate
    handler.direction = direction
    handler.mode = mode
    return handler
  }
}

// MARK: - Mock Layout

private struct MockLayout: GalleryViewLayouting {
  let pageOrigins: [CGFloat]
  let blockFrames: [CGRect]
  let contentSize: CGSize
  let transformation: ElementsTransformation? = nil
  let scrollDirection: ScrollDirection

  static func horizontal(pageCount: Int, pageSize: CGFloat) -> MockLayout {
    let origins = (0..<pageCount).map { CGFloat($0) * pageSize }
    let frames = origins.map { CGRect(x: $0, y: 0, width: pageSize, height: 100) }
    return MockLayout(
      pageOrigins: origins,
      blockFrames: frames,
      contentSize: CGSize(width: CGFloat(pageCount) * pageSize, height: 100),
      scrollDirection: .horizontal
    )
  }

  static func vertical(pageCount: Int, pageSize: CGFloat) -> MockLayout {
    let origins = (0..<pageCount).map { CGFloat($0) * pageSize }
    let frames = origins.map { CGRect(x: 0, y: $0, width: 100, height: pageSize) }
    return MockLayout(
      pageOrigins: origins,
      blockFrames: frames,
      contentSize: CGSize(width: 100, height: CGFloat(pageCount) * pageSize),
      scrollDirection: .vertical
    )
  }

  func contentOffset(pageIndex: CGFloat) -> CGFloat {
    let index = Int(pageIndex.rounded())
    guard pageOrigins.indices.contains(index) else { return 0 }
    return pageOrigins[index]
  }

  func pageIndex(forContentOffset contentOffset: CGFloat) -> CGFloat {
    guard !pageOrigins.isEmpty else { return 0 }
    let pageSize = pageOrigins.count > 1 ? pageOrigins[1] - pageOrigins[0] : 1
    return contentOffset / pageSize
  }

  func isEqual(to _: GalleryViewModel, boundsSize _: CGSize) -> Bool {
    false
  }
}

// MARK: - Mock Delegate

private final class MockScrollHandlerDelegate: ScrollHandlerDelegate {
  struct PositionCall: Equatable {
    let position: GalleryViewState.Position
    let animated: Bool
  }

  struct StateCall: Equatable {
    let position: GalleryViewState.Position
    let offset: CGFloat
  }

  var updateOffsetDetachedCalls: [CGFloat] = []
  var updateOffsetToPositionCalls: [PositionCall] = []
  var updateGalleryStateCalls: [StateCall] = []
  var interruptScrollingCalls = 0
  var finishScrollingCalls: [CGFloat?] = []

  func updateOffsetDetached(_ offset: CGFloat) {
    updateOffsetDetachedCalls.append(offset)
  }

  func updateOffsetDetached(_ pos: GalleryViewState.Position) {
    updateOffsetToPositionCalls.append(PositionCall(position: pos, animated: false))
  }

  func updateOffsetToPosition(_ pos: GalleryViewState.Position, animated: Bool) {
    updateOffsetToPositionCalls.append(PositionCall(position: pos, animated: animated))
  }

  func updateGalleryState(_ pos: GalleryViewState.Position, offset: CGFloat) {
    updateGalleryStateCalls.append(StateCall(position: pos, offset: offset))
  }

  func interruptScrolling() {
    interruptScrollingCalls += 1
  }

  func finishScrolling(scrollStartOffset: CGFloat?) {
    finishScrollingCalls.append(scrollStartOffset)
  }
}
#endif
