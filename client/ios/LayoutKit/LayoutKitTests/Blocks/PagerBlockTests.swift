import LayoutKit
import VGSL
import XCTest

final class PagerBlockTests: XCTestCase {
  func test_WhenFixedHeight_IntrinsicContentHeightReturnsFixedHeight() {
    let block = makePagerBlock(heightTrait: .fixed(111))

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: .infinity), 111)
  }

  func test_WhenFixedWidth_IntrinsicContentWidthReturnsFixedWidth() throws {
    let block = makePagerBlock(widthTrait: .fixed(111))

    XCTAssertEqual(block.intrinsicContentWidth, 111)
  }

  func test_WhenIntrinsictHeight_IntrinsicContentHeightReturnsMaxPageHeight() throws {
    let block = makePagerBlock(currentPage: 0)

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: .infinity), 1000)
  }

  func test_WhenNextPageLarger_IntrinsicContentHeightReturnsNextPageHeight() throws {
    let block = makePagerBlock(currentPage: 1)

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: .infinity), 1000)
  }

  func test_WhenPreviousPageLarger_IntrinsicContentHeightReturnsPreviousPageHeight() throws {
    let block = makePagerBlock(currentPage: 4)

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: .infinity), 1000)
  }
}

private func makePagerBlock(
  currentPage: Int = 0,
  widthTrait: LayoutTrait = .intrinsic,
  heightTrait: LayoutTrait = .intrinsic
) -> PagerBlock {
  try! PagerBlock(
    pagerPath: nil,
    alignment: .center,
    layoutMode: .neighbourPageSize(10),
    gallery: GalleryViewModel(
      items: [
        makeGalleryItem(size: 100),
        makeGalleryItem(size: 100),
        makeGalleryItem(size: 1000),
        makeGalleryItem(size: 1000),
        makeGalleryItem(size: 10),
      ],
      metrics: GalleryViewMetrics(gaps: [0, 10, 10, 10, 10, 0]),
      path: UIElementPath("gallery")
    ),
    selectedActions: [[]],
    state: PagerViewState(
      numberOfPages: 5,
      currentPage: currentPage,
      animated: false
    ),
    widthTrait: widthTrait,
    heightTrait: heightTrait
  )
}

private func makeGalleryItem(size: CGFloat) -> GalleryViewModel.Item {
  GalleryViewModel.Item(
    crossAlignment: .leading,
    content: TextBlock(
      widthTrait: .fixed(size),
      heightTrait: .fixed(size),
      text: NSAttributedString(string: "")
    )
  )
}
