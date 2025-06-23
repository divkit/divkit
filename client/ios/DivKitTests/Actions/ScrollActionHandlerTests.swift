@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import XCTest

final class ScrollActionHandlerTests: XCTestCase {
  private let blockStateStorage = DivBlockStateStorage()
  private var handler: DivActionHandler!

  private var isUpdateCardCalled = false

  override func setUp() {
    handler = DivActionHandler(
      blockStateStorage: blockStateStorage,
      updateCard: { [unowned self] _ in
        self.isUpdateCardCalled = true
      }
    )
  }

  func test_ScrollTo_Start_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollTo(start)

    XCTAssertEqual(
      galleryState(offset: 0, itemCount: 5, range: 1000),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_End_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollTo(end)

    XCTAssertEqual(
      galleryState(offset: 1000, itemCount: 5, range: 1000),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_Offset_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollTo(offsetDestination(200))

    XCTAssertEqual(
      galleryState(offset: 200, itemCount: 5, range: 1000),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_Offset_Overflow_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollTo(offsetDestination(2000))

    XCTAssertEqual(
      galleryState(offset: 1000, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollTo_Offset_NegativeOverflow_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollTo(offsetDestination(-2000))

    XCTAssertEqual(
      galleryState(offset: 0, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollTo_Start_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(start)

    XCTAssertEqual(
      galleryState(index: 0, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_End_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(end)

    XCTAssertEqual(
      galleryState(index: 4, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_Index_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(3))

    XCTAssertEqual(
      galleryState(index: 3, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_Index_Invalid_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(10))

    XCTAssertEqual(
      galleryState(index: 2, itemCount: 5),
      getState()
    )
    XCTAssertFalse(isUpdateCardCalled)
  }

  func test_ScrollTo_Offset_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(offsetDestination(200))

    XCTAssertEqual(
      galleryState(index: 2, itemCount: 5),
      getState()
    )
    XCTAssertFalse(isUpdateCardCalled)
  }

  func test_ScrollBy_Offset_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollBy(offset: 100)

    XCTAssertEqual(
      galleryState(offset: 200, itemCount: 5, range: 1000),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollBy_Offset_Clamp_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollBy(offset: 2000)

    XCTAssertEqual(
      galleryState(offset: 1000, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollBy_Offset_Ring_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollBy(offset: 2200, overflow: .ring)

    XCTAssertEqual(
      galleryState(offset: 300, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollBy_NegativeOffset_Gallery() {
    setState(galleryState(offset: 200, itemCount: 5, range: 1000))

    handleScrollBy(offset: -100)

    XCTAssertEqual(
      galleryState(offset: 100, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollBy_NegativeOffset_Clamp_Gallery() {
    setState(galleryState(offset: 200, itemCount: 5, range: 1000))

    handleScrollBy(offset: -100)

    XCTAssertEqual(
      galleryState(offset: 100, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollBy_NegativeOffset_Ring_Gallery() {
    setState(galleryState(offset: 200, itemCount: 5, range: 1000))

    handleScrollBy(offset: -2100, overflow: .ring)

    XCTAssertEqual(
      galleryState(offset: 100, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollBy_ItemCount_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: 1)

    XCTAssertEqual(
      galleryState(index: 3, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollBy_NegativeItemCount_PagedGallery() {
    setState(galleryState(index: 3, itemCount: 5))

    handleScrollBy(itemCount: -2)

    XCTAssertEqual(
      galleryState(index: 1, itemCount: 5),
      getState()
    )
  }

  func test_ScrollBy_ItemCount_Clamp_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: 5)

    XCTAssertEqual(
      galleryState(index: 4, itemCount: 5),
      getState()
    )
  }

  func test_ScrollBy_ItemCount_Ring_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: 11, overflow: .ring)

    XCTAssertEqual(
      galleryState(index: 3, itemCount: 5),
      getState()
    )
  }

  func test_ScrollBy_NegativeItemCount_Clamp_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: -8)

    XCTAssertEqual(
      galleryState(index: 0, itemCount: 5),
      getState()
    )
  }

  func test_ScrollBy_NegativeItemCount_Ring_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: -3, overflow: .ring)

    XCTAssertEqual(
      galleryState(index: 4, itemCount: 5),
      getState()
    )
  }

  func test_ScrollBy_NegativeItemCount_Ring0_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: -7, overflow: .ring)

    XCTAssertEqual(
      galleryState(index: 0, itemCount: 5),
      getState()
    )
  }

  func test_ScrollBy_NegativeItemCount_RingOverflow_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: -34, overflow: .ring)

    XCTAssertEqual(
      galleryState(index: 3, itemCount: 5),
      getState()
    )
  }

  func test_ScrollTo_Start_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(start)

    XCTAssertEqual(
      pagerState(index: 0, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_End_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(end)

    XCTAssertEqual(
      pagerState(index: 4, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_Offset_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(offsetDestination(200))

    XCTAssertEqual(
      pagerState(index: 2, itemCount: 5),
      getState()
    )
    XCTAssertFalse(isUpdateCardCalled)
  }

  func test_ScrollTo_Index_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(3))

    XCTAssertEqual(
      pagerState(index: 3, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_Index_Invalid_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(6))

    XCTAssertEqual(
      pagerState(index: 2, itemCount: 5),
      getState()
    )
    XCTAssertFalse(isUpdateCardCalled)
  }

  func test_ScrollBy_Offset_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollBy(offset: 100)

    XCTAssertEqual(
      pagerState(index: 2, itemCount: 5),
      getState()
    )
    XCTAssertFalse(isUpdateCardCalled)
  }

  func test_ScrollBy_ItemCount_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: 1)

    XCTAssertEqual(
      pagerState(index: 3, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollBy_NegativeItemCount_Pager() {
    setState(pagerState(index: 3, itemCount: 5))

    handleScrollBy(itemCount: -2)

    XCTAssertEqual(
      pagerState(index: 1, itemCount: 5),
      getState()
    )
  }

  func test_Gallery_NonAnimated() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(3), animated: false)

    XCTAssertEqual(
      galleryState(index: 3, itemCount: 5, animated: false),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_Pager_NonAnimated() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(3), animated: false)

    XCTAssertEqual(
      pagerState(index: 3, itemCount: 5, animated: false),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_Tab_NonAnimated() {
    setState(tabState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(3), animated: false)

    XCTAssertEqual(
      tabState(index: 3, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  private func handleScrollTo(_ destination: DivActionScrollDestination, animated: Bool = true) {
    handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionScrollTo(
          DivActionScrollTo(
            animated: .value(animated),
            destination: destination,
            id: .value("element_id")
          )
        )
      ),
      path: cardId.path,
      source: .callback,
      sender: nil
    )
  }

  private func handleScrollBy(
    itemCount: Int = 0,
    offset: Int = 0,
    overflow: DivActionScrollBy.Overflow = .clamp,
    animated: Bool = true
  ) {
    handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionScrollBy(
          DivActionScrollBy(
            animated: .value(animated),
            id: .value("element_id"),
            itemCount: .value(itemCount),
            offset: .value(offset),
            overflow: .value(overflow)
          )
        )
      ),
      path: cardId.path,
      source: .callback,
      sender: nil
    )
  }

  private func getState<T: ElementState>() -> T {
    blockStateStorage.getState("element_id", cardId: cardId)!
  }

  private func setState(_ state: ElementState) {
    blockStateStorage.setState(
      id: "element_id",
      cardId: cardId,
      state: state
    )
  }
}

private let cardId = DivBlockModelingContext.testCardId

private let end = DivActionScrollDestination.endDestination(EndDestination())
private let start = DivActionScrollDestination.startDestination(StartDestination())

private func offsetDestination(_ value: Int) -> DivActionScrollDestination {
  DivActionScrollDestination.offsetDestination(OffsetDestination(value: .value(value)))
}

private func indexDestination(_ value: Int) -> DivActionScrollDestination {
  DivActionScrollDestination.indexDestination(IndexDestination(value: .value(value)))
}

private func galleryState(index: Int, itemCount: Int, animated: Bool = true) -> GalleryViewState {
  GalleryViewState(
    contentPageIndex: CGFloat(index),
    itemsCount: itemCount,
    animated: animated
  )
}

private func galleryState(
  offset: CGFloat,
  itemCount: Int,
  range: CGFloat,
  animated: Bool = true
) -> GalleryViewState {
  GalleryViewState(
    contentPosition: .offset(offset, firstVisibleItemIndex: 0),
    itemsCount: itemCount,
    isScrolling: false,
    scrollRange: range,
    animated: animated
  )
}

private func pagerState(index: Int, itemCount: Int, animated: Bool = true) -> PagerViewState {
  PagerViewState(
    numberOfPages: itemCount,
    currentPage: index,
    animated: animated
  )
}

private func tabState(index: Int, itemCount: Int) -> TabViewState {
  TabViewState(selectedPageIndex: CGFloat(index), countOfPages: itemCount)
}
