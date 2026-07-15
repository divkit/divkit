@testable import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import XCTest

final class DivPagerExtensionsTests: XCTestCase {
  func test_InfiniteScrollWithGoneItems_FiltersGoneBeforeBuffer() throws {
    let pagerBlock = try makePagerBlock()
    let items = pagerBlock.gallery.items

    XCTAssertTrue(items.allSatisfy { !$0.content.isEmpty })
    XCTAssertEqual(pagerBlock.gallery.bufferSize, 2)
    XCTAssertEqual(items.count, 10)
    XCTAssertEqual(pagerBlock.gallery.itemsCountWithoutInfinite, 6)
  }

  func test_InfiniteScrollWithGoneItems_WrapsAroundWhenScrolled() throws {
    let model = try makePagerBlock().gallery
    let bufferSize = model.bufferSize

    let realStartIndex = bufferSize
    let realEndIndex = model.items.count - bufferSize - 1

    XCTAssertTrue(
      model.items[realEndIndex + 1].content.equals(model.items[realStartIndex].content)
    )
    XCTAssertTrue(
      model.items[realStartIndex - 1].content.equals(model.items[realEndIndex].content)
    )
  }

  func test_InfiniteScrollWithGoneItems_KeepsGoneItemsForDisappearActions() throws {
    let model = try makePagerBlock().gallery

    XCTAssertFalse(
      model.removedItemsIndices.isEmpty,
      "Gone items must reach the view model so handleElementsDisappearing fires disappear_actions; got \(model.removedItemsIndices)"
    )
  }

  func test_SynchronizedInfiniteScroll_UpdatesPageCount_ButDoesNotClampCurrentPage() throws {
    let model = try makePagerBlock().gallery
    let overflowPage = model.itemsCountWithoutInfinite + 100
    let state = PagerViewState(
      numberOfPages: 0,
      currentPage: overflowPage,
      animated: true,
      isInfiniteScrollable: true
    )

    let synchronized = state.synchronized(with: model)

    XCTAssertEqual(synchronized.numberOfPages, model.itemsCountWithoutInfinite)
    XCTAssertEqual(synchronized.currentPage, CGFloat(overflowPage))
    XCTAssertTrue(synchronized.isInfiniteScrollable)
  }

  func test_SynchronizedInfiniteScroll_ReturnsSelfWhenUnchanged() throws {
    let model = try makePagerBlock().gallery
    let state = PagerViewState(
      numberOfPages: model.itemsCountWithoutInfinite,
      currentPage: 1,
      animated: true,
      isInfiniteScrollable: true
    )

    XCTAssertEqual(state.synchronized(with: model), state)
  }

  private func makePagerBlock() throws -> PagerBlock {
    let block = try DivPagerTemplate.make(
      fromFile: "pager_gone_with_infinite_scroll",
      subdirectory: "div-pager",
      context: DivBlockModelingContext()
    ) as! DecoratingBlock
    return block.child as! PagerBlock
  }
}
