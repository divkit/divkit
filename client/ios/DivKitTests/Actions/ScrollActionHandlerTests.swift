@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import XCTest

final class ScrollActionHandlerTests: ScrollActionTestCase {
  func test_ScrollTo_Start_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollTo(start)

    assertScrollStateEqual(
      galleryState(offset: 0, itemCount: 5, range: 1000),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_End_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollTo(end)

    assertScrollStateEqual(
      galleryState(offset: 1000, itemCount: 5, range: 1000),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_Offset_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollTo(offsetDestination(200))

    assertScrollStateEqual(
      galleryState(offset: 200, itemCount: 5, range: 1000),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_Offset_Overflow_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollTo(offsetDestination(2000))

    assertScrollStateEqual(
      galleryState(offset: 1000, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollTo_Offset_NegativeOverflow_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollTo(offsetDestination(-2000))

    assertScrollStateEqual(
      galleryState(offset: 0, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollTo_Start_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(start)

    assertScrollStateEqual(
      galleryState(index: 0, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_End_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(end)

    assertScrollStateEqual(
      galleryState(index: 4, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_Index_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(3))

    assertScrollStateEqual(
      galleryState(index: 3, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_Index_Invalid_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(10))

    assertScrollStateEqual(
      galleryState(index: 2, itemCount: 5),
      getState()
    )
    XCTAssertFalse(isUpdateCardCalled)
  }

  func test_ScrollTo_Offset_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(offsetDestination(200))

    assertScrollStateEqual(
      galleryState(index: 2, itemCount: 5),
      getState()
    )
    XCTAssertFalse(isUpdateCardCalled)
  }

  func test_ScrollBy_Offset_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollBy(offset: 100)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .offset(100, wraps: false))
    assertScrollStateEqual(
      galleryState(offset: 100, itemCount: 5, range: 1000),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollBy_Offset_Clamp_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollBy(offset: 2000)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .offset(2000, wraps: false))
    assertScrollStateEqual(
      galleryState(offset: 100, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollBy_Offset_Ring_Gallery() {
    setState(galleryState(offset: 100, itemCount: 5, range: 1000))

    handleScrollBy(offset: 2200, overflow: .ring)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .offset(2200, wraps: true))
    assertScrollStateEqual(
      galleryState(offset: 100, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollBy_NegativeOffset_Gallery() {
    setState(galleryState(offset: 200, itemCount: 5, range: 1000))

    handleScrollBy(offset: -100)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .offset(-100, wraps: false))
    assertScrollStateEqual(
      galleryState(offset: 200, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollBy_NegativeOffset_Clamp_Gallery() {
    setState(galleryState(offset: 200, itemCount: 5, range: 1000))

    handleScrollBy(offset: -100)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .offset(-100, wraps: false))
    assertScrollStateEqual(
      galleryState(offset: 200, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollBy_NegativeOffset_Ring_Gallery() {
    setState(galleryState(offset: 200, itemCount: 5, range: 1000))

    handleScrollBy(offset: -2100, overflow: .ring)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .offset(-2100, wraps: true))
    assertScrollStateEqual(
      galleryState(offset: 200, itemCount: 5, range: 1000),
      getState()
    )
  }

  func test_ScrollBy_ItemCount_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: 1)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .items(1, wraps: false))
    assertScrollStateEqual(
      galleryState(index: 2, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollBy_NegativeItemCount_PagedGallery() {
    setState(galleryState(index: 3, itemCount: 5))

    handleScrollBy(itemCount: -2)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .items(-2, wraps: false))
    assertScrollStateEqual(
      galleryState(index: 3, itemCount: 5),
      getState()
    )
  }

  func test_ScrollBy_ItemCount_Clamp_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: 5)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .items(5, wraps: false))
    assertScrollStateEqual(
      galleryState(index: 2, itemCount: 5),
      getState()
    )
  }

  func test_ScrollBy_ItemCount_Ring_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: 11, overflow: .ring)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .items(11, wraps: true))
    assertScrollStateEqual(
      galleryState(index: 2, itemCount: 5),
      getState()
    )
  }

  func test_ScrollBy_NegativeItemCount_Clamp_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: -8)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .items(-8, wraps: false))
    assertScrollStateEqual(
      galleryState(index: 2, itemCount: 5),
      getState()
    )
  }

  func test_ScrollBy_NegativeItemCount_Ring_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: -3, overflow: .ring)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .items(-3, wraps: true))
    assertScrollStateEqual(
      galleryState(index: 2, itemCount: 5),
      getState()
    )
  }

  func test_ScrollBy_NegativeItemCount_Ring0_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: -7, overflow: .ring)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .items(-7, wraps: true))
    assertScrollStateEqual(
      galleryState(index: 2, itemCount: 5),
      getState()
    )
  }

  func test_ScrollBy_NegativeItemCount_RingOverflow_PagedGallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: -34, overflow: .ring)

    let request: GalleryViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .items(-34, wraps: true))
    assertScrollStateEqual(
      galleryState(index: 2, itemCount: 5),
      getState()
    )
  }

  func test_ScrollTo_Start_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(start)

    assertScrollStateEqual(
      pagerState(index: 0, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_End_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(end)

    assertScrollStateEqual(
      pagerState(index: 4, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_Offset_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(offsetDestination(200))

    assertScrollStateEqual(
      pagerState(index: 2, itemCount: 5),
      getState()
    )
    XCTAssertFalse(isUpdateCardCalled)
  }

  func test_ScrollTo_Index_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(3))

    assertScrollStateEqual(
      pagerState(index: 3, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollTo_Index_Invalid_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(6))

    assertScrollStateEqual(
      pagerState(index: 2, itemCount: 5),
      getState()
    )
    XCTAssertFalse(isUpdateCardCalled)
  }

  func test_ScrollBy_Offset_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollBy(offset: 100)

    assertScrollStateEqual(
      pagerState(index: 2, itemCount: 5),
      getState()
    )
    XCTAssertFalse(isUpdateCardCalled)
  }

  func test_ScrollBy_ItemCount_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollBy(itemCount: 1)

    let request: PagerViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .items(1, wraps: false))
    assertScrollStateEqual(
      pagerState(index: 2, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollBy_ItemCount_Pager_Last_Page() {
    setState(pagerState(index: 4, itemCount: 5))

    handleScrollBy(itemCount: 1)

    let request: PagerViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .items(1, wraps: false))
    assertScrollStateEqual(
      pagerState(index: 4, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollBy_ItemCount_Infinate_Pager_Last_Page() {
    setState(pagerWithInfiniteScrollState(index: 4, itemCount: 5))

    handleScrollBy(itemCount: 1)

    let request: PagerViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .items(1, wraps: false))
    assertScrollStateEqual(
      pagerWithInfiniteScrollState(index: 4, itemCount: 5),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_ScrollBy_NegativeItemCount_Pager() {
    setState(pagerState(index: 3, itemCount: 5))

    handleScrollBy(itemCount: -2)

    let request: PagerViewState = getState()
    XCTAssertEqual(request.scrollRequest?.delta, .items(-2, wraps: false))
    assertScrollStateEqual(
      pagerState(index: 3, itemCount: 5),
      getState()
    )
  }

  func test_Gallery_NonAnimated() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(3), animated: false)

    assertScrollStateEqual(
      galleryState(index: 3, itemCount: 5, animated: false),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_Pager_NonAnimated() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(3), animated: false)

    assertScrollStateEqual(
      pagerState(index: 3, itemCount: 5, animated: false),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }

  func test_Tab_NonAnimated() {
    setState(tabState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(3), animated: false)

    assertScrollStateEqual(
      TabViewState(selectedPageIndex: 3, countOfPages: 5, animated: false),
      getState()
    )
    XCTAssertTrue(isUpdateCardCalled)
  }
}
