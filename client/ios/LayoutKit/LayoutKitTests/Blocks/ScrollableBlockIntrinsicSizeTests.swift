import CoreGraphics
import Foundation
import LayoutKit
import Testing
import VGSL

/// Intrinsic size paths that take a non-trivial width:
/// `GalleryBlock` for a horizontal gallery and `PagerBlock` for a vertical pager,
/// each accompanied by the mirrored case for the other scroll direction.
@Suite
struct ScrollableBlockIntrinsicSizeTests {
  // MARK: - Gallery

  @Test
  func horizontalGallery_intrinsicHeight_measuresFixedWidthItemAtItsWidth() throws {
    let item = textItem(widthTrait: .fixed(itemWidth))
    let block = try galleryBlock(item: item, direction: .horizontal)

    #expect(
      block.intrinsicContentHeight(forWidth: blockWidth)
        == item.intrinsicContentHeight(forWidth: itemWidth)
    )
  }

  @Test
  func horizontalGallery_intrinsicHeight_measuresMatchParentItemAtGalleryWidth() throws {
    let item = textItem(widthTrait: .resizable)
    let block = try galleryBlock(item: item, direction: .horizontal)
    // Axial paddings are horizontal here.
    let expectedHeight = item.intrinsicContentHeight(forWidth: blockWidth - padding * 2)

    #expect(expectedHeight != item.intrinsicContentHeight(forWidth: 0))
    #expect(block.intrinsicContentHeight(forWidth: blockWidth) == expectedHeight)
  }

  @Test
  func verticalGallery_intrinsicHeight_measuresMatchParentItemAtGalleryWidth() throws {
    let item = textItem(widthTrait: .resizable)
    let block = try galleryBlock(item: item, direction: .vertical)
    // Axial paddings are vertical here, there are no cross insets.
    let expectedHeight = item.intrinsicContentHeight(forWidth: blockWidth) + padding * 2

    #expect(block.intrinsicContentHeight(forWidth: blockWidth) == expectedHeight)
  }

  @Test(arguments: ScrollDirection.allCases)
  func gallery_intrinsicAxialSize_withMatchParentItem_ignoresItem(
    direction: ScrollDirection
  ) throws {
    let axis = TestAxis(direction)
    let item = axis.textBlock(along: .resizable, across: .fixed(itemWidth), text: "x")
    let block = try GalleryBlock(
      model: GalleryViewModel(
        blocks: [item],
        metrics: GalleryViewMetrics(gaps: [padding, padding]),
        path: UIElementPath("gallery"),
        direction: direction
      ),
      state: GalleryViewState(contentOffset: 0, itemsCount: 1),
      widthTrait: direction.isHorizontal ? .intrinsic : .fixed(itemWidth),
      heightTrait: direction.isHorizontal ? .fixed(itemWidth) : .intrinsic
    )
    let intrinsicAxialSize = direction.isHorizontal
      ? block.intrinsicContentWidth
      : block.intrinsicContentHeight(forWidth: itemWidth)

    // A match_parent item along the scroll axis of a wrap_content gallery has nothing to fill,
    // so it is laid out with zero size.
    #expect(intrinsicAxialSize == padding * 2)
  }

  // MARK: - Pager

  @Test
  func verticalPager_intrinsicHeight_measuresFixedWidthPageAtItsWidth() throws {
    let page = textItem(widthTrait: .fixed(itemWidth))
    let block = try pagerBlock(page: page, direction: .vertical, layoutMode: .pageContentSize)

    #expect(
      block.intrinsicContentHeight(forWidth: blockWidth)
        == page.intrinsicContentHeight(forWidth: itemWidth)
    )
  }

  @Test
  func verticalPager_intrinsicHeight_measuresMatchParentPageAtPagerWidth() throws {
    let page = textItem(widthTrait: .resizable)
    let block = try pagerBlock(page: page, direction: .vertical, layoutMode: .pageContentSize)
    // Cross insets are horizontal here.
    let expectedHeight = page.intrinsicContentHeight(forWidth: blockWidth - padding * 2)

    #expect(expectedHeight != page.intrinsicContentHeight(forWidth: 0))
    #expect(block.intrinsicContentHeight(forWidth: blockWidth) == expectedHeight)
  }

  @Test
  func horizontalPager_intrinsicHeight_measuresMatchParentPageAtPagerWidth() throws {
    let page = textItem(widthTrait: .resizable)
    let block = try pagerBlock(page: page, direction: .horizontal, layoutMode: .pageContentSize)
    // Cross insets are vertical here.
    let expectedHeight = page.intrinsicContentHeight(forWidth: blockWidth) + padding * 2

    #expect(block.intrinsicContentHeight(forWidth: blockWidth) == expectedHeight)
  }

  /// A vertical pager measures its height before its width is applied to the pages, so a page
  /// constrained across the scroll axis used to be measured at its unbounded intrinsic width and
  /// laid out at the bounded one. The measured height must match the laid out one.
  @Test
  func verticalPager_intrinsicHeight_matchesLayoutForConstrainedPage() throws {
    let page = BlockWithFixedWrapContent(
      width: 800,
      height: 20,
      constrainedHorizontally: true,
      intrinsicHeightForWidth: { $0 >= 800 ? 20 : 60 }
    )
    let block = try pagerBlock(page: page, direction: .vertical, layoutMode: .pageContentSize)

    let height = block.intrinsicContentHeight(forWidth: blockWidth)
    let layout = PagerViewLayout(
      model: block.gallery,
      layoutMode: .pageContentSize,
      boundsSize: CGSize(width: blockWidth, height: height)
    )

    // The page is constrained to blockWidth minus the cross insets, so it takes 60, not 20.
    #expect(height == 60)
    #expect(layout.blockFrames.first?.height == height)
  }

  @Test
  func horizontalPager_intrinsicWidth_withPercentagePageSize_hasNoPageSize() throws {
    let page = textItem(widthTrait: .fixed(itemWidth))
    let block = try pagerBlock(
      page: page,
      direction: .horizontal,
      layoutMode: .pageSize(RelativeValue(rawValue: 0.5)),
      widthTrait: .intrinsic
    )

    // A page is a percentage of the pager width, which is not known here.
    #expect(block.intrinsicContentWidth == 0)
  }

  @Test
  func verticalPager_intrinsicHeight_withPercentagePageSize_resolvesPageAgainstWidth() throws {
    let block = try pagerBlock(
      page: textItem(widthTrait: .resizable),
      direction: .vertical,
      layoutMode: .pageSize(RelativeValue(rawValue: 0.5))
    )

    // The pager height is not known yet, so the page is a percentage of the pager width.
    #expect(block.intrinsicContentHeight(forWidth: blockWidth) == blockWidth * 0.5)
  }

  @Test
  func verticalPager_intrinsicHeight_withNeighbourPageSize_resolvesPageAgainstWidth() throws {
    let neighbourPageSize: CGFloat = 20
    let block = try pagerBlock(
      page: textItem(widthTrait: .resizable),
      direction: .vertical,
      layoutMode: .neighbourPageSize(neighbourPageSize)
    )

    // A centered page with a neighbour on each side, no axial insets and no spacing.
    #expect(
      block.intrinsicContentHeight(forWidth: blockWidth) == blockWidth - neighbourPageSize * 2
    )
  }
}

private let blockWidth: CGFloat = 200
private let itemWidth: CGFloat = 100
private let padding: CGFloat = 10

private func textItem(widthTrait: LayoutTrait) -> TextBlock {
  TextBlock(
    widthTrait: widthTrait,
    heightTrait: .intrinsic,
    text: NSAttributedString(
      string: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor"
    ),
    accessibilityElement: nil
  )
}

/// Gallery with axial paddings and no cross insets.
private func galleryBlock(item: Block, direction: ScrollDirection) throws -> GalleryBlock {
  try GalleryBlock(
    model: GalleryViewModel(
      blocks: [item],
      metrics: GalleryViewMetrics(gaps: [padding, padding]),
      path: UIElementPath("gallery"),
      direction: direction
    ),
    state: GalleryViewState(contentOffset: 0, itemsCount: 1),
    widthTrait: .resizable,
    heightTrait: .intrinsic
  )
}

/// Pager with cross insets and no axial paddings.
private func pagerBlock(
  page: Block,
  direction: ScrollDirection,
  layoutMode: PagerBlock.LayoutMode,
  widthTrait: LayoutTrait = .resizable
) throws -> PagerBlock {
  try PagerBlock(
    pagerPath: nil,
    layoutMode: layoutMode,
    gallery: GalleryViewModel(
      blocks: [page],
      metrics: GalleryViewMetrics(
        axialInsetMode: .fixed(values: .zero),
        crossInsetMode: .fixed(values: SideInsets(leading: padding, trailing: padding)),
        spacings: [],
        crossSpacing: 0
      ),
      path: UIElementPath("pager"),
      direction: direction
    ),
    selectedActions: [[]],
    state: PagerViewState(numberOfPages: 1, currentPage: 0, animated: false),
    widthTrait: widthTrait,
    heightTrait: .intrinsic
  )
}
