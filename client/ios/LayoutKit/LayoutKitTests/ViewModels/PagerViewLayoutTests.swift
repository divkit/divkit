import CoreGraphics
import Foundation
@testable import LayoutKit
import Testing
import VGSL

@Suite
struct PagerViewLayoutTests {
  @Test(arguments: ScrollDirection.allCases)
  func neighbouredLayout_centerAlignment(direction: ScrollDirection) {
    let viewportSize: CGFloat = 390
    let neighbourPageSize: CGFloat = 10
    let itemSpacing: CGFloat = 4
    let leadingPadding: CGFloat = 40
    let trailingPadding: CGFloat = 0

    let pageSize = viewportSize - (neighbourPageSize + itemSpacing) * 2
    let expected = expectedCenterAlignedLayout(
      viewportSize: viewportSize,
      pageSize: pageSize,
      itemSpacing: itemSpacing,
      leadingPadding: leadingPadding,
      trailingPadding: trailingPadding
    )

    let layout = PagerViewLayout(
      model: galleryModel(
        pageCount: 3,
        itemSpacing: itemSpacing,
        leadingPadding: leadingPadding,
        trailingPadding: trailingPadding,
        direction: direction
      ),
      layoutMode: .neighbourPageSize(neighbourPageSize),
      boundsSize: pagerBoundsSize(along: viewportSize, direction: direction)
    )

    verifyLayout(layout, matches: expected, direction: direction)
  }

  @Test(arguments: ScrollDirection.allCases)
  func neighbouredLayout_leadingAlignment(direction: ScrollDirection) {
    let viewportSize: CGFloat = 390
    let neighbourPageSize: CGFloat = 10
    let itemSpacing: CGFloat = 4
    let leadingPadding: CGFloat = 40
    let trailingPadding: CGFloat = 0

    let pageSize = viewportSize - leadingPadding - (neighbourPageSize + itemSpacing)
    let expected = expectedLeadingAlignedLayout(
      viewportSize: viewportSize,
      pageSize: pageSize,
      itemSpacing: itemSpacing,
      leadingPadding: leadingPadding,
      trailingPadding: trailingPadding
    )

    let layout = PagerViewLayout(
      model: galleryModel(
        pageCount: 3,
        itemSpacing: itemSpacing,
        leadingPadding: leadingPadding,
        trailingPadding: trailingPadding,
        alignment: .leading,
        direction: direction
      ),
      layoutMode: .neighbourPageSize(neighbourPageSize),
      boundsSize: pagerBoundsSize(along: viewportSize, direction: direction)
    )

    verifyLayout(layout, matches: expected, direction: direction)
  }

  @Test(arguments: ScrollDirection.allCases)
  func neighbouredLayout_trailingAlignment_scrollable(direction: ScrollDirection) {
    let viewportSize: CGFloat = 390
    let neighbourPageSize: CGFloat = 10
    let itemSpacing: CGFloat = 4
    let leadingPadding: CGFloat = 0
    let trailingPadding: CGFloat = 40

    let pageSize = viewportSize - trailingPadding - (neighbourPageSize + itemSpacing)
    let expected = expectedTrailingAlignedLayout(
      viewportSize: viewportSize,
      pageSize: pageSize,
      itemSpacing: itemSpacing,
      leadingPadding: leadingPadding,
      trailingPadding: trailingPadding
    )

    let layout = PagerViewLayout(
      model: galleryModel(
        pageCount: 3,
        itemSpacing: itemSpacing,
        leadingPadding: leadingPadding,
        trailingPadding: trailingPadding,
        alignment: .trailing,
        direction: direction
      ),
      layoutMode: .neighbourPageSize(neighbourPageSize),
      boundsSize: pagerBoundsSize(along: viewportSize, direction: direction)
    )

    verifyLayout(layout, matches: expected, direction: direction)
  }

  @Test(arguments: PagerTestFixtures.crossAxisAlignmentCases, ScrollDirection.allCases)
  func intrinsicPagerFrames_withNilBounds_positionsNarrowPageByCrossAxisAlignment(
    crossAxisCase: PagerCrossAxisAlignmentCase,
    direction: ScrollDirection
  ) {
    let axis = TestAxis(direction)
    let model = crossAxisCase.model(direction: direction)
    let layoutMode = PagerBlock.LayoutMode.neighbourPageSize(10)
    #expect(
      model.frames(fitting: nil, layoutMode: layoutMode).map(axis.minAcross)
        == crossAxisCase.expectedCrossAxisOffsets
    )
    #expect(
      axis.across(model.intrinsicPagerSize(fitting: .unbounded, layoutMode: layoutMode))
        == PagerTestFixtures.maxCrossAxisItemSize
    )
  }

  @Test(arguments: PagerTestFixtures.crossAxisAlignmentCases, ScrollDirection.allCases)
  func intrinsicPagerFrames_withNilBoundsAndPageContentSize_positionsShortPageByCrossAxisAlignment(
    crossAxisCase: PagerCrossAxisAlignmentCase,
    direction: ScrollDirection
  ) {
    let axis = TestAxis(direction)
    let model = crossAxisCase.model(direction: direction)
    let layoutMode = PagerBlock.LayoutMode.pageContentSize
    #expect(
      model.frames(fitting: nil, layoutMode: layoutMode).map(axis.minAcross)
        == crossAxisCase.expectedCrossAxisOffsets
    )
    #expect(
      axis.across(model.intrinsicPagerSize(fitting: .unbounded, layoutMode: layoutMode))
        == PagerTestFixtures.maxCrossAxisItemSize
    )
  }

  @Test(arguments: PagerTestFixtures.crossAxisAlignmentCases, ScrollDirection.allCases)
  func boundedPagerFrames_withBoundedCrossAxis_positionsShortPageByCrossAxisAlignment(
    crossAxisCase: PagerCrossAxisAlignmentCase,
    direction: ScrollDirection
  ) {
    let axis = TestAxis(direction)
    let layout = PagerViewLayout(
      model: crossAxisCase.model(direction: direction),
      layoutMode: .neighbourPageSize(10),
      boundsSize: axis.size(along: 390, across: 200)
    )
    #expect(layout.blockFrames.map(axis.minAcross) == crossAxisCase.expectedCrossAxisOffsets)
  }

  @Test(arguments: ScrollDirection.allCases)
  func constrainedCrossAxisSize_respectsCrossInsets(direction: ScrollDirection) throws {
    let axis = TestAxis(direction)
    // Original inputs of the former horizontal and vertical tests.
    let (pagerAlong, pagerAcross, largeCrossSize, smallCrossSize): (
      CGFloat,
      CGFloat,
      CGFloat,
      CGFloat
    ) = direction.isHorizontal
      ? (300, 100, ConstrainedCrossAxisPagerFixtures.tallIntrinsicCrossAxisSize, 40)
      : (70, 300, ConstrainedCrossAxisPagerFixtures.wideIntrinsicCrossAxisSize, 100)
    let crossPadding: CGFloat = 10
    let pageSize = pagerAlong * 0.9
    let availableCrossAxisSize = pagerAcross - crossPadding * 2

    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.pageItem(
          direction: direction,
          intrinsicCrossAxisSize: largeCrossSize
        ),
        ConstrainedCrossAxisPagerFixtures.pageItem(
          direction: direction,
          intrinsicCrossAxisSize: smallCrossSize
        ),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.pagerMetrics(
        crossPadding: crossPadding,
        itemCount: 2
      ),
      path: UIElementPath("pager-constrained-cross-axis"),
      direction: direction
    )

    let layout = PagerViewLayout(
      model: model,
      layoutMode: .pageSize(RelativeValue(rawValue: 0.9)),
      boundsSize: axis.size(along: pagerAlong, across: pagerAcross)
    )

    #expect(layout.blockFrames.count == 2)
    let firstFrame = try #require(layout.blockFrames.first)

    #expect(axis.minAcross(firstFrame) == crossPadding)
    #expect(axis.acrossSize(firstFrame) == availableCrossAxisSize)
    #expect(axis.alongSize(firstFrame) == pageSize)

    let secondFrame = layout.blockFrames[1]
    #expect(axis.minAcross(secondFrame) == crossPadding)
    #expect(axis.acrossSize(secondFrame) == smallCrossSize)
  }

  @Test(arguments: ScrollDirection.allCases)
  func crossAxisOverflow_alignsItemsRelativeToAvailableSpace(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let layout = PagerViewLayout(
      model: CrossAxisOverflowFixtures.model(direction: direction),
      layoutMode: .pageContentSize,
      boundsSize: CrossAxisOverflowFixtures.boundsSize(axis)
    )

    // Items overflow the 100pt cross size by 60pt: center splits overflow evenly,
    // trailing pushes it before the leading edge.
    #expect(layout.blockFrames.map(axis.minAcross) == [0, -30, -60])
  }

  @Test(arguments: ScrollDirection.allCases)
  func constrainedCrossAxisSize_withZeroBounds_doesNotCrash(direction: ScrollDirection) {
    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.pageItem(
          direction: direction,
          intrinsicCrossAxisSize: ConstrainedCrossAxisPagerFixtures.wideIntrinsicCrossAxisSize
        ),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.pagerMetrics(crossPadding: 10, itemCount: 1),
      path: UIElementPath("pager-zero-bounds"),
      direction: direction
    )

    let layout = PagerViewLayout(
      model: model,
      layoutMode: .pageSize(RelativeValue(rawValue: 0.9)),
      boundsSize: .zero
    )

    #expect(layout.blockFrames.isEmpty)
    #expect(layout.blockPages.isEmpty)
    #expect(layout.contentSize == .zero)
  }

  // Vertical only by nature: the scroll-axis size of a vertical page is measured
  // for a cross-axis width, while the width of a horizontal page never depends on its height.
  @Test
  func verticalPager_pageContentSize_withZeroCrossAxis_measuresHeightAtClampedCrossAxisWidth() {
    let scrollAxisSize: CGFloat = 200
    let crossAxisSize: CGFloat = 0
    let crossPadding: CGFloat = 10
    let crossInsetSum = crossPadding * 2
    let expectedHeight: CGFloat = 42

    let pageBlock = BlockWithFixedWrapContent(
      width: 120,
      height: expectedHeight,
      constrainedHorizontally: true,
      intrinsicHeightForWidth: { width in
        width == 0 ? expectedHeight : 999
      }
    )

    let model = GalleryViewModel(
      items: [
        GalleryViewModel.Item(crossAlignment: .leading, content: pageBlock),
      ],
      metrics: GalleryViewMetrics(
        axialInsetMode: .fixed(values: .zero),
        crossInsetMode: .fixed(
          values: SideInsets(leading: crossPadding, trailing: crossPadding)
        ),
        spacings: [],
        crossSpacing: 0
      ),
      path: UIElementPath("vertical-pager-zero-cross-axis-page-content-size"),
      direction: .vertical
    )
    let layoutMode = PagerBlock.LayoutMode.pageContentSize

    let frames = model.frames(
      fitting: AxialBounds(along: scrollAxisSize, across: crossAxisSize),
      layoutMode: layoutMode
    )

    #expect(crossAxisSize < crossInsetSum)
    #expect(frames.first?.height == expectedHeight)
  }

  // Vertical only by nature, see above.
  @Test
  func verticalPager_pageContentSize_measuresHeightAtLayoutWidth() throws {
    let pagerWidth: CGFloat = 200
    let crossPadding: CGFloat = 10
    let scrollAxisSize: CGFloat = 500
    let availableCrossAxisWidth = pagerWidth - crossPadding * 2
    let pageWidth: CGFloat = 300

    let pageText = TextBlock(
      widthTrait: .fixed(pageWidth),
      heightTrait: .intrinsic(constrained: false, minSize: 0, maxSize: .infinity),
      text: NSAttributedString(
        string: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor"
      ),
      accessibilityElement: nil
    )

    let model = GalleryViewModel(
      items: [
        GalleryViewModel.Item(crossAlignment: .leading, content: pageText),
      ],
      metrics: GalleryViewMetrics(
        axialInsetMode: .fixed(values: .zero),
        crossInsetMode: .fixed(
          values: SideInsets(leading: crossPadding, trailing: crossPadding)
        ),
        spacings: [],
        crossSpacing: 0
      ),
      path: UIElementPath("vertical-pager-page-content-size-layout-width"),
      direction: .vertical
    )

    let frames = model.frames(
      fitting: AxialBounds(along: scrollAxisSize, across: pagerWidth),
      layoutMode: .pageContentSize
    )

    let firstFrame = try #require(frames.first)

    let expectedHeight = pageText.intrinsicContentHeight(forWidth: pageWidth)
    let heightAtViewportWidth = pageText.intrinsicContentHeight(forWidth: availableCrossAxisWidth)

    #expect(firstFrame.width == pageWidth)
    #expect(firstFrame.width > availableCrossAxisWidth)
    #expect(heightAtViewportWidth > expectedHeight)
    #expect(firstFrame.height == expectedHeight)
  }

  @Test(arguments: ScrollDirection.allCases)
  func resizableCrossInsets_useCrossAxisViewportSize(direction: ScrollDirection) throws {
    let axis = TestAxis(direction)
    // Original inputs of the former horizontal and vertical tests.
    let (pagerAlong, pagerAcross, intrinsicCrossAxisSize): (CGFloat, CGFloat, CGFloat) =
      direction.isHorizontal
        ? (300, 100, ConstrainedCrossAxisPagerFixtures.tallIntrinsicCrossAxisSize)
        : (70, 300, ConstrainedCrossAxisPagerFixtures.wideIntrinsicCrossAxisSize)
    let resizableInsets = InsetMode.Resizable(minValue: 10, maxViewportSize: 14)
    let expectedCrossInset = (pagerAcross - resizableInsets.maxViewportSize) / 2
    let availableCrossAxisSize = pagerAcross - expectedCrossInset * 2

    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.pageItem(
          direction: direction,
          intrinsicCrossAxisSize: intrinsicCrossAxisSize
        ),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.resizableCrossAxisPagerMetrics(
        resizableInsets: resizableInsets,
        itemCount: 1
      ),
      path: UIElementPath("pager-resizable-cross-insets"),
      direction: direction
    )

    let layout = PagerViewLayout(
      model: model,
      layoutMode: .pageSize(RelativeValue(rawValue: 0.9)),
      boundsSize: axis.size(along: pagerAlong, across: pagerAcross)
    )

    let firstFrame = try #require(layout.blockFrames.first)

    #expect(axis.minAcross(firstFrame) == expectedCrossInset)
    #expect(axis.acrossSize(firstFrame) == availableCrossAxisSize)
    #expect(axis.maxAcross(firstFrame) == pagerAcross - expectedCrossInset)
  }

  @Test(arguments: ScrollDirection.allCases)
  func constrainedCrossAxisSize_withIndefiniteCrossAxis_usesIntrinsicSize(
    direction: ScrollDirection
  ) throws {
    let axis = TestAxis(direction)
    // Original inputs of the former horizontal and vertical tests.
    let (pagerAlong, childCrossSize): (CGFloat, CGFloat) =
      direction.isHorizontal ? (300, 60) : (70, 250)
    let crossPadding: CGFloat = 10
    // The cross axis stays indefinite only while the pager measures its own cross size.
    // A horizontal pager already knows its width by then, a vertical one knows neither axis.
    let fittingSize = direction.isHorizontal
      ? AxialBounds(along: pagerAlong, across: nil)
      : .unbounded
    let layoutMode = PagerBlock.LayoutMode.pageSize(RelativeValue(rawValue: 0.9))

    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.pageItem(
          direction: direction,
          intrinsicCrossAxisSize: childCrossSize
        ),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.pagerMetrics(
        crossPadding: crossPadding,
        itemCount: 1
      ),
      path: UIElementPath("pager-indefinite-cross-axis"),
      direction: direction
    )

    let frames = model.frames(fitting: fittingSize, layoutMode: layoutMode)

    let firstFrame = try #require(frames.first)

    #expect(axis.acrossSize(firstFrame) == childCrossSize)
    #expect(
      axis.across(model.intrinsicPagerSize(fitting: fittingSize, layoutMode: layoutMode))
        == crossPadding * 2 + childCrossSize
    )
  }

  @Test(arguments: ScrollDirection.allCases)
  func constrainedCrossAxisSize_withZeroCrossAxis_clampsToZero(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let pagerAlong: CGFloat = 300
    let layoutMode = PagerBlock.LayoutMode.pageSize(RelativeValue(rawValue: 0.9))

    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.pageItem(
          direction: direction,
          intrinsicCrossAxisSize: 60
        ),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.pagerMetrics(crossPadding: 10, itemCount: 1),
      path: UIElementPath("pager-zero-cross-axis"),
      direction: direction
    )

    let frames = model.frames(
      fitting: AxialBounds(along: pagerAlong, across: 0),
      layoutMode: layoutMode
    )

    #expect(frames.first.map(axis.acrossSize) == 0)
  }

  @Test(arguments: ScrollDirection.allCases)
  func percentageLayout_centerAlignment(direction: ScrollDirection) {
    let viewportSize: CGFloat = 390
    let itemSpacing: CGFloat = 10
    let leadingPadding: CGFloat = 40
    let trailingPadding: CGFloat = 40
    let pageSize = viewportSize

    let expected = expectedCenterAlignedLayout(
      viewportSize: viewportSize,
      pageSize: pageSize,
      itemSpacing: itemSpacing,
      leadingPadding: leadingPadding,
      trailingPadding: trailingPadding
    )

    let layout = PagerViewLayout(
      model: galleryModel(
        pageCount: 3,
        itemSpacing: itemSpacing,
        leadingPadding: leadingPadding,
        trailingPadding: trailingPadding,
        direction: direction
      ),
      layoutMode: .pageSize(RelativeValue(integerLiteral: 1)),
      boundsSize: pagerBoundsSize(along: viewportSize, direction: direction)
    )

    verifyLayout(layout, matches: expected, direction: direction)
  }

  @Test(arguments: ScrollDirection.allCases)
  func fewSmallPages_endAlignment_pinsScrollToStart(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let viewportSize: CGFloat = 300
    let neighbourPageSize: CGFloat = 210
    let itemSpacing: CGFloat = 10
    let leadingPadding: CGFloat = 10
    let trailingPadding: CGFloat = 10

    let pageSize = viewportSize - trailingPadding - (neighbourPageSize + itemSpacing)
    let expected = expectedLayoutWhenContentFits(
      pageSize: pageSize,
      itemSpacing: itemSpacing,
      leadingPadding: leadingPadding,
      trailingPadding: trailingPadding
    )

    let layout = PagerViewLayout(
      model: galleryModel(
        pageCount: 3,
        itemSpacing: itemSpacing,
        leadingPadding: leadingPadding,
        trailingPadding: trailingPadding,
        alignment: .trailing,
        direction: direction
      ),
      layoutMode: .neighbourPageSize(neighbourPageSize),
      boundsSize: axis.size(along: viewportSize, across: 60)
    )

    // contentSize (250) <= viewportSize (300) → no scrolling
    verifyLayout(layout, matches: expected, direction: direction)
  }

  @Test(arguments: ScrollDirection.allCases)
  func contentOffset_clampsToMaxContentOffset(direction: ScrollDirection) {
    let viewportSize: CGFloat = 390
    let neighbourPageSize: CGFloat = 10
    let itemSpacing: CGFloat = 4
    let leadingPadding: CGFloat = 40
    let trailingPadding: CGFloat = 0
    let pageSize = viewportSize - (neighbourPageSize + itemSpacing) * 2

    let expected = expectedCenterAlignedLayout(
      viewportSize: viewportSize,
      pageSize: pageSize,
      itemSpacing: itemSpacing,
      leadingPadding: leadingPadding,
      trailingPadding: trailingPadding
    )
    let layout = PagerViewLayout(
      model: galleryModel(
        pageCount: 3,
        itemSpacing: itemSpacing,
        leadingPadding: leadingPadding,
        trailingPadding: trailingPadding,
        direction: direction
      ),
      layoutMode: .neighbourPageSize(neighbourPageSize),
      boundsSize: pagerBoundsSize(along: viewportSize, direction: direction)
    )

    let lastPageIndex = 2
    let maxContentOffset = expected.contentAxialSize - viewportSize
    let lastPageOrigin = expected.pageOrigins[lastPageIndex]
    let lastPageSize = expected.blockPageSizes[lastPageIndex]
    let fractionalPageIndex = CGFloat(lastPageIndex) + 0.5
    let unclampedOffset = lastPageOrigin + lastPageSize * 0.5

    #expect(lastPageOrigin == maxContentOffset)
    #expect(unclampedOffset > maxContentOffset)
    #expect(layout.contentOffset(pageIndex: fractionalPageIndex) == maxContentOffset)
    #expect(layout.contentOffset(pageIndex: fractionalPageIndex) != unclampedOffset)
  }

  @Test(arguments: ScrollDirection.allCases)
  func pageContentSize_withMatchParentPageSize_clampsPagesAndMeasuresCrossAxis(
    direction: ScrollDirection
  ) {
    let axis = TestAxis(direction)
    let pagerAlong: CGFloat = 360
    let layoutMode = PagerBlock.LayoutMode.pageContentSize
    let model = pageContentSizeMatchParentGalleryModel(direction: direction)
    let fittingSize = AxialBounds(along: pagerAlong, across: nil)

    let frames = model.frames(fitting: fittingSize, layoutMode: layoutMode)

    #expect(frames.map(axis.alongSize) == [pagerAlong, 400, 300])

    let largestPageCrossSize = frames.map(axis.acrossSize).max() ?? 0
    let intrinsicSize = model.intrinsicPagerSize(fitting: fittingSize, layoutMode: layoutMode)

    #expect(axis.across(intrinsicSize) == largestPageCrossSize)
    if direction.isHorizontal {
      // Text height is measured at the page width, so the text wraps into a few lines only.
      #expect(largestPageCrossSize < 200)
    }
  }

  @Test(arguments: ScrollDirection.allCases)
  func pageContentSize_withMatchParentPageSize_subtractsAxialInsets(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let pagerAlong: CGFloat = 360
    let leadingInset: CGFloat = 16
    let trailingInset: CGFloat = 24
    let contentSize = pagerAlong - leadingInset - trailingInset
    let layoutMode = PagerBlock.LayoutMode.pageContentSize
    let model = pageContentSizeMatchParentGalleryModel(
      direction: direction,
      axialInsets: SideInsets(leading: leadingInset, trailing: trailingInset)
    )

    let frames = model.frames(
      fitting: AxialBounds(along: pagerAlong, across: nil),
      layoutMode: layoutMode
    )

    #expect(frames.map(axis.alongSize) == [contentSize, 400, 300])
    #expect(frames.first.map(axis.minAlong) == leadingInset)
  }

  @Test(arguments: ScrollDirection.allCases)
  func degenerateBoundedCrossAxis_clampsResizablePagesToZero(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let scrollAxisSize: CGFloat = 360
    let crossAxisSize: CGFloat = 20
    let crossInsetSum: CGFloat = 32
    let fixedPageCrossSize: CGFloat = 100

    let fixedPageItem = GalleryViewModel.Item(
      crossAlignment: .leading,
      content: axis.textBlock(along: .fixed(120), across: .fixed(fixedPageCrossSize), text: "fixed")
    )
    let resizablePageItem = GalleryViewModel.Item(
      crossAlignment: .leading,
      content: axis.textBlock(along: .fixed(120), across: .weighted(.default), text: "resizable")
    )

    let model = GalleryViewModel(
      items: [fixedPageItem, resizablePageItem],
      metrics: GalleryViewMetrics(
        axialInsetMode: .fixed(values: .zero),
        crossInsetMode: .fixed(
          values: SideInsets(leading: crossInsetSum / 2, trailing: crossInsetSum / 2)
        ),
        spacings: [0],
        crossSpacing: 0
      ),
      path: UIElementPath("degenerate-bounded-cross-axis"),
      direction: direction
    )
    let layoutMode = PagerBlock.LayoutMode.pageContentSize

    let boundedFrames = model.frames(
      fitting: AxialBounds(along: scrollAxisSize, across: crossAxisSize),
      layoutMode: layoutMode
    )
    #expect(crossAxisSize < crossInsetSum)
    #expect(boundedFrames.map(axis.acrossSize) == [fixedPageCrossSize, 0])

    let intrinsicFrames = model.frames(
      fitting: AxialBounds(along: scrollAxisSize, across: nil),
      layoutMode: layoutMode
    )
    #expect(intrinsicFrames.map(axis.acrossSize) == [fixedPageCrossSize, fixedPageCrossSize])
  }

  @Test(arguments: ScrollDirection.allCases)
  func degenerateBoundedCrossAxis_clampsCrossAxisViewportToZero(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let scrollAxisSize: CGFloat = 300
    let crossAxisSize: CGFloat = 8
    let crossPadding: CGFloat = 10

    let model = GalleryViewModel(
      items: [
        GalleryViewModel.Item(
          crossAlignment: .center,
          content: axis.textBlock(along: .fixed(120), across: .weighted(.default), text: "page")
        ),
      ],
      metrics: GalleryViewMetrics(
        axialInsetMode: .fixed(values: .zero),
        crossInsetMode: .fixed(
          values: SideInsets(leading: crossPadding, trailing: crossPadding)
        ),
        spacings: [],
        crossSpacing: 0
      ),
      path: UIElementPath("pager-degenerate-cross-axis-viewport"),
      direction: direction
    )

    let frames = model.frames(
      fitting: AxialBounds(along: scrollAxisSize, across: crossAxisSize),
      layoutMode: .pageSize(RelativeValue(rawValue: 0.9))
    )

    #expect(crossAxisSize < crossPadding * 2)
    #expect(frames.first.map(axis.acrossSize) == 0)
    #expect(frames.first.map(axis.minAcross) == crossPadding)
  }

  @Test(arguments: ScrollDirection.allCases)
  func fillingPages_withoutCrossBounds_takePartInSlotAndFillIt(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let frames = FillingItemsFixtures.model(direction: direction).frames(
      fitting: AxialBounds(along: 300, across: nil),
      layoutMode: .pageSize(RelativeValue(rawValue: 0.5))
    )

    #expect(frames.map(axis.acrossSize) == [80, 80, 50, 40])
  }

  @Test(arguments: ScrollDirection.allCases)
  func clampsCrossResizableItemToConstraints(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    func item(minSize: CGFloat, maxSize: CGFloat) -> GalleryViewModel.Item {
      GalleryViewModel.Item(
        crossAlignment: .leading,
        content: axis.textBlock(
          along: .fixed(120),
          across: .weighted(.default, minSize: minSize, maxSize: maxSize),
          text: "x"
        )
      )
    }

    let minSize: CGFloat = 120
    let maxSize: CGFloat = 80

    let metrics = GalleryViewMetrics(
      axialInsetMode: .fixed(values: SideInsets(leading: 0.0, trailing: 0.0)),
      spacings: [4.0, 4.0],
      crossSpacing: 0.0
    )
    let model = GalleryViewModel(
      items: [
        item(minSize: 0, maxSize: .infinity),
        item(minSize: minSize, maxSize: .infinity),
        item(minSize: 0, maxSize: maxSize),
      ],
      metrics: metrics,
      path: UIElementPath("1"),
      direction: direction
    )
    let crossAxisSize: CGFloat = 100
    let layout = PagerViewLayout(
      model: model,
      layoutMode: .neighbourPageSize(10.0),
      boundsSize: axis.size(along: 360.0, across: crossAxisSize)
    )

    #expect(layout.blockFrames.map(axis.acrossSize) == [crossAxisSize, minSize, maxSize])
  }
}

private struct ExpectedPagerLayout {
  let pageSizes: [CGFloat]
  let pageMinAlongs: [CGFloat]
  let contentAxialSize: CGFloat
  let pageOrigins: [CGFloat]
  let blockPageSizes: [CGFloat]
  let contentOffsets: [CGFloat]?
  var clampedContentOffsets: [(pageIndex: CGFloat, offset: CGFloat)]?
}

/// Cross-axis size of the pager used by the former horizontal (height = 23)
/// and vertical (width = 120) tests.
private func pagerBoundsSize(along: CGFloat, direction: ScrollDirection) -> CGSize {
  TestAxis(direction).size(along: along, across: direction.isHorizontal ? 23 : 120)
}

private func pageLayoutMetrics(
  pageSize: CGFloat,
  itemSpacing: CGFloat,
  leadingPadding: CGFloat,
  trailingPadding: CGFloat,
  pageCount: Int
) -> (pageMinAlongs: [CGFloat], contentAxialSize: CGFloat) {
  let pageTotalSize = pageSize + itemSpacing
  var pageMinAlongs = [CGFloat]()

  for counter in 0..<pageCount {
    pageMinAlongs.append(leadingPadding + CGFloat(counter) * pageTotalSize)
  }

  let contentAxialSize = (pageMinAlongs.last ?? 0) + pageSize + trailingPadding
  return (pageMinAlongs, contentAxialSize)
}

private func expectedCenterAlignedLayout(
  viewportSize: CGFloat,
  pageSize: CGFloat,
  itemSpacing: CGFloat,
  leadingPadding: CGFloat,
  trailingPadding: CGFloat,
  pageCount: Int = 3
) -> ExpectedPagerLayout {
  let (pageMinAlongs, contentAxialSize) = pageLayoutMetrics(
    pageSize: pageSize,
    itemSpacing: itemSpacing,
    leadingPadding: leadingPadding,
    trailingPadding: trailingPadding,
    pageCount: pageCount
  )
  let centerAlignmentOffset = (viewportSize - pageSize) / 2
  let secondPageScrollOrigin = pageMinAlongs[1] - centerAlignmentOffset
  let lastPageScrollOrigin = contentAxialSize - viewportSize
  let pageOrigins = [0, secondPageScrollOrigin, lastPageScrollOrigin]

  return ExpectedPagerLayout(
    pageSizes: [CGFloat](repeating: pageSize, times: UInt(pageCount)),
    pageMinAlongs: pageMinAlongs,
    contentAxialSize: contentAxialSize,
    pageOrigins: pageOrigins,
    blockPageSizes: [
      secondPageScrollOrigin,
      lastPageScrollOrigin - secondPageScrollOrigin,
      contentAxialSize - lastPageScrollOrigin,
    ],
    contentOffsets: pageOrigins,
    clampedContentOffsets: [(CGFloat(pageCount - 1) + 0.5, lastPageScrollOrigin)]
  )
}

private func expectedLeadingAlignedLayout(
  viewportSize: CGFloat,
  pageSize: CGFloat,
  itemSpacing: CGFloat,
  leadingPadding: CGFloat,
  trailingPadding: CGFloat,
  pageCount: Int = 3
) -> ExpectedPagerLayout {
  let (pageMinAlongs, contentAxialSize) = pageLayoutMetrics(
    pageSize: pageSize,
    itemSpacing: itemSpacing,
    leadingPadding: leadingPadding,
    trailingPadding: trailingPadding,
    pageCount: pageCount
  )
  let secondPageScrollOrigin = pageMinAlongs[1] - leadingPadding
  let lastPageScrollOrigin = contentAxialSize - viewportSize
  let pageOrigins = [0, secondPageScrollOrigin, lastPageScrollOrigin]

  return ExpectedPagerLayout(
    pageSizes: [CGFloat](repeating: pageSize, times: UInt(pageCount)),
    pageMinAlongs: pageMinAlongs,
    contentAxialSize: contentAxialSize,
    pageOrigins: pageOrigins,
    blockPageSizes: [
      secondPageScrollOrigin,
      lastPageScrollOrigin - secondPageScrollOrigin,
      contentAxialSize - lastPageScrollOrigin,
    ],
    contentOffsets: pageOrigins,
    clampedContentOffsets: [(CGFloat(pageCount - 1) + 0.5, lastPageScrollOrigin)]
  )
}

private func expectedTrailingAlignedLayout(
  viewportSize: CGFloat,
  pageSize: CGFloat,
  itemSpacing: CGFloat,
  leadingPadding: CGFloat,
  trailingPadding: CGFloat,
  pageCount: Int = 3
) -> ExpectedPagerLayout {
  let (pageMinAlongs, contentAxialSize) = pageLayoutMetrics(
    pageSize: pageSize,
    itemSpacing: itemSpacing,
    leadingPadding: leadingPadding,
    trailingPadding: trailingPadding,
    pageCount: pageCount
  )
  let trailingAlignmentOffset = viewportSize - pageSize - trailingPadding
  let secondPageScrollOrigin = pageMinAlongs[1] - trailingAlignmentOffset
  let lastPageScrollOrigin = contentAxialSize - viewportSize
  let pageOrigins = [0, secondPageScrollOrigin, lastPageScrollOrigin]

  return ExpectedPagerLayout(
    pageSizes: [CGFloat](repeating: pageSize, times: UInt(pageCount)),
    pageMinAlongs: pageMinAlongs,
    contentAxialSize: contentAxialSize,
    pageOrigins: pageOrigins,
    blockPageSizes: [
      secondPageScrollOrigin,
      lastPageScrollOrigin - secondPageScrollOrigin,
      contentAxialSize - lastPageScrollOrigin,
    ],
    contentOffsets: pageOrigins,
    clampedContentOffsets: [(CGFloat(pageCount - 1) + 0.5, lastPageScrollOrigin)]
  )
}

private func expectedLayoutWhenContentFits(
  pageSize: CGFloat,
  itemSpacing: CGFloat,
  leadingPadding: CGFloat,
  trailingPadding: CGFloat,
  pageCount: Int = 3
) -> ExpectedPagerLayout {
  let (pageMinAlongs, contentAxialSize) = pageLayoutMetrics(
    pageSize: pageSize,
    itemSpacing: itemSpacing,
    leadingPadding: leadingPadding,
    trailingPadding: trailingPadding,
    pageCount: pageCount
  )
  let pageStep = pageSize + itemSpacing
  let blockPageSizes = (0..<pageCount).map { index in
    index < pageCount - 1 ? pageStep : contentAxialSize - (pageMinAlongs.last ?? 0)
  }

  return ExpectedPagerLayout(
    pageSizes: [CGFloat](repeating: pageSize, times: UInt(pageCount)),
    pageMinAlongs: pageMinAlongs,
    contentAxialSize: contentAxialSize,
    pageOrigins: [CGFloat](repeating: 0, times: UInt(pageCount)),
    blockPageSizes: blockPageSizes,
    contentOffsets: [CGFloat](repeating: 0, times: UInt(pageCount))
  )
}

private func verifyLayout(
  _ layout: PagerViewLayout,
  matches expected: ExpectedPagerLayout,
  direction: ScrollDirection,
  sourceLocation: SourceLocation = #_sourceLocation
) {
  let axis = TestAxis(direction)
  #expect(
    layout.blockFrames.map(axis.alongSize) == expected.pageSizes,
    sourceLocation: sourceLocation
  )
  #expect(
    layout.blockFrames.map(axis.minAlong) == expected.pageMinAlongs,
    sourceLocation: sourceLocation
  )
  #expect(
    axis.along(layout.contentSize) == expected.contentAxialSize,
    sourceLocation: sourceLocation
  )
  #expect(
    layout.pageOrigins == expected.pageOrigins,
    sourceLocation: sourceLocation
  )
  #expect(
    layout.blockPages.map(\.size) == expected.blockPageSizes,
    sourceLocation: sourceLocation
  )
  if let contentOffsets = expected.contentOffsets {
    for (index, offset) in contentOffsets.enumerated() {
      #expect(
        layout.contentOffset(pageIndex: CGFloat(index)) == offset,
        sourceLocation: sourceLocation
      )
    }
    #expect(
      layout.pageIndex(forContentOffset: 0) == 0,
      sourceLocation: sourceLocation
    )
  }
  if let clampedContentOffsets = expected.clampedContentOffsets {
    for check in clampedContentOffsets {
      #expect(
        layout.contentOffset(pageIndex: check.pageIndex) == check.offset,
        sourceLocation: sourceLocation
      )
    }
  }
}

private func pageContentSizeMatchParentGalleryModel(
  direction: ScrollDirection,
  axialInsets: SideInsets = .zero
) -> GalleryViewModel {
  let axis = TestAxis(direction)
  func page(
    text: String,
    minSize: CGFloat = 0,
    maxSize: CGFloat = .infinity
  ) -> GalleryViewModel.Item {
    GalleryViewModel.Item(
      crossAlignment: .leading,
      content: axis.textBlock(
        along: .weighted(.default, minSize: minSize, maxSize: maxSize),
        across: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
        text: text
      )
    )
  }

  return GalleryViewModel(
    items: [
      page(text: "First page\nPage size = match_parent\nNo constraints"),
      page(
        text: "Middle page\nPage size = match_parent\nMin size > pager size",
        minSize: 400
      ),
      page(
        text: "Last page\nPage size = match_parent\nMax size < pager size",
        maxSize: 300
      ),
    ],
    metrics: GalleryViewMetrics(
      axialInsetMode: .fixed(values: axialInsets),
      spacings: [10, 10],
      crossSpacing: 0
    ),
    path: UIElementPath("pager-wrap-content-size-mode-match-parent"),
    direction: direction
  )
}

private func galleryModel(
  pageCount: Int,
  itemSpacing: CGFloat,
  leadingPadding: CGFloat,
  trailingPadding: CGFloat,
  alignment: Alignment = .center,
  direction: ScrollDirection = .horizontal
) -> GalleryViewModel {
  GalleryViewModel(
    items: Array(
      repeating: GalleryViewModel.Item(
        crossAlignment: .leading,
        content: TextBlock(
          widthTrait: direction.isHorizontal ? .weighted(.default) : .fixed(100),
          heightTrait: direction.isHorizontal ? .intrinsic(
            constrained: false,
            minSize: 0.0,
            maxSize: CGFloat.infinity
          ) : .weighted(.default),
          text: NSAttributedString(string: "Sample")
        )
      ),
      times: UInt(pageCount)
    ),
    metrics: GalleryViewMetrics(
      axialInsetMode: .fixed(
        values: SideInsets(leading: leadingPadding, trailing: trailingPadding)
      ),
      spacings: [CGFloat](repeating: itemSpacing, times: UInt(max(0, pageCount - 1))),
      crossSpacing: 0.0
    ),
    path: UIElementPath("pager"),
    alignment: alignment,
    direction: direction
  )
}

private enum ConstrainedCrossAxisPagerFixtures {
  static let wideIntrinsicCrossAxisSize: CGFloat = 400
  static let tallIntrinsicCrossAxisSize: CGFloat = 150

  static func resizableCrossAxisPagerMetrics(
    resizableInsets: InsetMode.Resizable,
    itemCount: Int
  ) -> GalleryViewMetrics {
    GalleryViewMetrics(
      axialInsetMode: .fixed(values: .zero),
      crossInsetMode: .resizable(params: resizableInsets),
      spacings: [CGFloat](repeating: 0, times: UInt(max(0, itemCount - 1))),
      crossSpacing: 0
    )
  }

  static func pageItem(
    direction: ScrollDirection,
    intrinsicCrossAxisSize: CGFloat
  ) -> GalleryViewModel.Item {
    GalleryViewModel.Item(
      crossAlignment: .leading,
      content: TestAxis(direction).fixedWrapContentBlock(
        along: 0,
        across: intrinsicCrossAxisSize,
        constrainedAcross: true
      )
    )
  }

  static func pagerMetrics(
    crossPadding: CGFloat,
    itemCount: Int
  ) -> GalleryViewMetrics {
    GalleryViewMetrics(
      axialInsetMode: .fixed(values: .zero),
      crossInsetMode: .fixed(
        values: SideInsets(leading: crossPadding, trailing: crossPadding)
      ),
      spacings: [CGFloat](repeating: 0, times: UInt(max(0, itemCount - 1))),
      crossSpacing: 0
    )
  }
}
