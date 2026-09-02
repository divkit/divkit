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

  func test_WhenVerticalPagerWithMultiplePagesAndCenterAlignment_IntrinsicContentWidthEqualsWidestItem(
  ) {
    let block = makeVerticalPagerBlock(itemWidths: [100, 200], crossAlignment: .center)

    XCTAssertEqual(block.intrinsicContentWidth, 200)
  }

  func test_WhenVerticalPagerWithMultiplePagesAndTrailingAlignment_IntrinsicContentWidthEqualsWidestItem(
  ) {
    let block = makeVerticalPagerBlock(itemWidths: [100, 200], crossAlignment: .trailing)

    XCTAssertEqual(block.intrinsicContentWidth, 200)
  }

  func test_WhenHorizontalPagerWithMultiplePagesAndCenterAlignment_IntrinsicContentHeightEqualsTallestItem(
  ) {
    let block = makeHorizontalPagerBlock(itemHeights: [100, 200], crossAlignment: .center)

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: 390), 200)
  }

  func test_WhenHorizontalPagerWithMultiplePagesAndTrailingAlignment_IntrinsicContentHeightEqualsTallestItem(
  ) {
    let block = makeHorizontalPagerBlock(itemHeights: [100, 200], crossAlignment: .trailing)

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: 390), 200)
  }

  func test_WhenHorizontalPagerWithPageContentSizeAndIntrinsicWidth_IntrinsicContentWidthEqualsSumOfPageWidths(
  ) {
    let block = makeHorizontalPagerBlockWithPageContentSize(itemWidths: [70, 80, 60])

    XCTAssertEqual(block.intrinsicContentWidth, 250)
  }
}

private func makePagerBlock(
  currentPage: Int = 0,
  widthTrait: LayoutTrait = .intrinsic,
  heightTrait: LayoutTrait = .intrinsic
) -> PagerBlock {
  try! PagerBlock(
    pagerPath: nil,
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

private func makeVerticalPagerBlock(
  itemWidths: [CGFloat],
  crossAlignment: Alignment
) -> PagerBlock {
  try! PagerBlock(
    pagerPath: nil,
    layoutMode: .neighbourPageSize(10),
    gallery: PagerTestFixtures.verticalPagerModel(
      itemWidths: itemWidths,
      crossAlignment: crossAlignment
    ),
    selectedActions: Array(repeating: [], count: itemWidths.count),
    state: PagerViewState(
      numberOfPages: itemWidths.count,
      currentPage: 0,
      animated: false
    ),
    widthTrait: .intrinsic,
    heightTrait: .resizable
  )
}

private func makeHorizontalPagerBlock(
  itemHeights: [CGFloat],
  crossAlignment: Alignment
) -> PagerBlock {
  try! PagerBlock(
    pagerPath: nil,
    layoutMode: .neighbourPageSize(10),
    gallery: PagerTestFixtures.horizontalPagerModel(
      itemHeights: itemHeights,
      crossAlignment: crossAlignment
    ),
    selectedActions: Array(repeating: [], count: itemHeights.count),
    state: PagerViewState(
      numberOfPages: itemHeights.count,
      currentPage: 0,
      animated: false
    ),
    widthTrait: .resizable,
    heightTrait: .intrinsic
  )
}

private func makeHorizontalPagerBlockWithPageContentSize(
  itemWidths: [CGFloat]
) -> PagerBlock {
  let itemSpacing: CGFloat = 10
  let leadingPadding: CGFloat = 10
  let trailingPadding: CGFloat = 10
  let gaps = [leadingPadding]
    + [CGFloat](repeating: itemSpacing, count: max(0, itemWidths.count - 1))
    + [trailingPadding]

  return try! PagerBlock(
    pagerPath: nil,
    layoutMode: .pageContentSize,
    gallery: GalleryViewModel(
      items: itemWidths.map { width in
        GalleryViewModel.Item(
          crossAlignment: .leading,
          content: TextBlock(
            widthTrait: .fixed(width),
            heightTrait: .fixed(PagerTestFixtures.mainAxisPageSize),
            text: NSAttributedString(string: "")
          )
        )
      },
      metrics: GalleryViewMetrics(gaps: gaps),
      path: UIElementPath("horizontal-pager-wrap-content-width"),
      direction: .horizontal
    ),
    selectedActions: Array(repeating: [], count: itemWidths.count),
    state: PagerViewState(
      numberOfPages: itemWidths.count,
      currentPage: 0,
      animated: false
    ),
    widthTrait: .intrinsic,
    heightTrait: .fixed(PagerTestFixtures.mainAxisPageSize)
  )
}
