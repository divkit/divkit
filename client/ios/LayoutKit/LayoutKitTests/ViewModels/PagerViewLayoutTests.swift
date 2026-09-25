import CoreGraphics
import Foundation
@testable import LayoutKit
import Testing
import VGSL

@Suite
struct PagerViewLayoutTests {
  @Test
  func neighbouredLayout_centerAlignment() {
    let viewportWidth: CGFloat = 390
    let neighbourPageWidth: CGFloat = 10
    let itemSpacing: CGFloat = 4
    let leadingPadding: CGFloat = 40
    let trailingPadding: CGFloat = 0

    let pageWidth = viewportWidth - (neighbourPageWidth + itemSpacing) * 2
    let expected = expectedCenterAlignedLayout(
      viewportSize: viewportWidth,
      pageSize: pageWidth,
      itemSpacing: itemSpacing,
      leadingPadding: leadingPadding,
      trailingPadding: trailingPadding
    )

    let layout = PagerViewLayout(
      model: galleryModel(
        pageCount: 3,
        itemSpacing: itemSpacing,
        leadingPadding: leadingPadding,
        trailingPadding: trailingPadding
      ),
      layoutMode: .neighbourPageSize(neighbourPageWidth),
      boundsSize: CGSize(width: viewportWidth, height: 23)
    )

    verifyHorizontalLayout(layout, matches: expected)
  }

  @Test
  func neighbouredLayout_leadingAlignment() {
    let viewportWidth: CGFloat = 390
    let neighbourPageWidth: CGFloat = 10
    let itemSpacing: CGFloat = 4
    let leadingPadding: CGFloat = 40
    let trailingPadding: CGFloat = 0

    let pageWidth = viewportWidth - leadingPadding - (neighbourPageWidth + itemSpacing)
    let expected = expectedLeadingAlignedLayout(
      viewportSize: viewportWidth,
      pageSize: pageWidth,
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
        alignment: .leading
      ),
      layoutMode: .neighbourPageSize(neighbourPageWidth),
      boundsSize: CGSize(width: viewportWidth, height: 23)
    )

    verifyHorizontalLayout(layout, matches: expected)
  }

  @Test
  func neighbouredLayout_trailingAlignment_scrollable() {
    let viewportWidth: CGFloat = 390
    let neighbourPageWidth: CGFloat = 10
    let itemSpacing: CGFloat = 4
    let leadingPadding: CGFloat = 0
    let trailingPadding: CGFloat = 40

    let pageWidth = viewportWidth - trailingPadding - (neighbourPageWidth + itemSpacing)
    let expected = expectedTrailingAlignedLayout(
      viewportSize: viewportWidth,
      pageSize: pageWidth,
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
        alignment: .trailing
      ),
      layoutMode: .neighbourPageSize(neighbourPageWidth),
      boundsSize: CGSize(width: viewportWidth, height: 23)
    )

    verifyHorizontalLayout(layout, matches: expected)
  }

  @Test
  func verticalNeighbouredLayout_leadingAlignment() {
    let viewportHeight: CGFloat = 390
    let neighbourPageHeight: CGFloat = 10
    let itemSpacing: CGFloat = 4
    let leadingPadding: CGFloat = 40
    let trailingPadding: CGFloat = 0

    let pageHeight = viewportHeight - leadingPadding - (neighbourPageHeight + itemSpacing)
    let expected = expectedLeadingAlignedLayout(
      viewportSize: viewportHeight,
      pageSize: pageHeight,
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
        direction: .vertical
      ),
      layoutMode: .neighbourPageSize(neighbourPageHeight),
      boundsSize: CGSize(width: 120, height: viewportHeight)
    )

    verifyVerticalLayout(layout, matches: expected)
  }

  @Test(arguments: [
    PagerTestFixtures.Vertical.leadingCase,
    PagerTestFixtures.Vertical.centerCase,
    PagerTestFixtures.Vertical.trailingCase,
  ])
  func intrinsicPagerFrames_withNilWidth_positionsNarrowPageByCrossAxisAlignment(
    crossAxisCase: PagerCrossAxisAlignmentCase
  ) {
    let layoutMode = PagerBlock.LayoutMode.neighbourPageSize(10)
    #expect(
      crossAxisCase.model.frames(fitting: nil, layoutMode: layoutMode).map(\.minX)
        == crossAxisCase.expectedCrossAxisOffsets
    )
    #expect(
      crossAxisCase.model.intrinsicPagerSize(forWidth: nil, layoutMode: layoutMode).width
        == PagerTestFixtures.maxCrossAxisItemSize
    )
  }

  @Test(arguments: [
    PagerTestFixtures.Horizontal.leadingCase,
    PagerTestFixtures.Horizontal.centerCase,
    PagerTestFixtures.Horizontal.trailingCase,
  ])
  func intrinsicPagerFrames_withNilWidthAndPageContentSize_positionsShortPageByCrossAxisAlignment(
    crossAxisCase: PagerCrossAxisAlignmentCase
  ) {
    let layoutMode = PagerBlock.LayoutMode.pageContentSize
    #expect(
      crossAxisCase.model.frames(fitting: nil, layoutMode: layoutMode).map(\.minY)
        == crossAxisCase.expectedCrossAxisOffsets
    )
    #expect(
      crossAxisCase.model.intrinsicPagerSize(forWidth: nil, layoutMode: layoutMode).height
        == PagerTestFixtures.maxCrossAxisItemSize
    )
  }

  @Test(arguments: [
    PagerTestFixtures.Horizontal.leadingCase,
    PagerTestFixtures.Horizontal.centerCase,
    PagerTestFixtures.Horizontal.trailingCase,
  ])
  func boundedPagerFrames_withBoundedHeight_positionsShortPageByCrossAxisAlignment(
    crossAxisCase: PagerCrossAxisAlignmentCase
  ) {
    let layout = PagerViewLayout(
      model: crossAxisCase.model,
      layoutMode: .neighbourPageSize(10),
      boundsSize: CGSize(width: 390, height: 200)
    )
    #expect(layout.blockFrames.map(\.minY) == crossAxisCase.expectedCrossAxisOffsets)
  }

  @Test
  func verticalPager_constrainedCrossAxisWidth_respectsCrossInsets() throws {
    let pagerWidth: CGFloat = 300
    let pagerHeight: CGFloat = 70
    let crossPadding: CGFloat = 10
    let pageHeight = pagerHeight * 0.9
    let availableCrossAxisWidth = pagerWidth - crossPadding * 2

    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.verticalPageItem(
          intrinsicCrossAxisWidth: ConstrainedCrossAxisPagerFixtures.wideIntrinsicCrossAxisSize
        ),
        ConstrainedCrossAxisPagerFixtures.verticalPageItem(intrinsicCrossAxisWidth: 100),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.verticalPagerMetrics(
        crossPadding: crossPadding,
        itemCount: 2
      ),
      path: UIElementPath("vertical-pager-constrained-width"),
      direction: .vertical
    )

    let layout = PagerViewLayout(
      model: model,
      layoutMode: .pageSize(RelativeValue(rawValue: 0.9)),
      boundsSize: CGSize(width: pagerWidth, height: pagerHeight)
    )

    #expect(layout.blockFrames.count == 2)
    let firstFrame = try #require(layout.blockFrames.first)

    #expect(firstFrame.minX == crossPadding)
    #expect(firstFrame.width == availableCrossAxisWidth)
    #expect(firstFrame.height == pageHeight)

    let secondFrame = layout.blockFrames[1]
    #expect(secondFrame.minX == crossPadding)
    #expect(secondFrame.width == 100)
  }

  @Test
  func horizontalPager_constrainedCrossAxisHeight_respectsCrossInsets() throws {
    let pagerWidth: CGFloat = 300
    let pagerHeight: CGFloat = 100
    let crossPadding: CGFloat = 10
    let pageWidth = pagerWidth * 0.9
    let availableCrossAxisHeight = pagerHeight - crossPadding * 2

    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.horizontalPageItem(
          intrinsicCrossAxisHeight: ConstrainedCrossAxisPagerFixtures.tallIntrinsicCrossAxisSize
        ),
        ConstrainedCrossAxisPagerFixtures.horizontalPageItem(intrinsicCrossAxisHeight: 40),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.horizontalPagerMetrics(
        crossPadding: crossPadding,
        itemCount: 2
      ),
      path: UIElementPath("horizontal-pager-constrained-height"),
      direction: .horizontal
    )

    let layout = PagerViewLayout(
      model: model,
      layoutMode: .pageSize(RelativeValue(rawValue: 0.9)),
      boundsSize: CGSize(width: pagerWidth, height: pagerHeight)
    )

    #expect(layout.blockFrames.count == 2)
    let firstFrame = try #require(layout.blockFrames.first)

    #expect(firstFrame.minY == crossPadding)
    #expect(firstFrame.height == availableCrossAxisHeight)
    #expect(firstFrame.width == pageWidth)

    let secondFrame = layout.blockFrames[1]
    #expect(secondFrame.minY == crossPadding)
    #expect(secondFrame.height == 40)
  }

  @Test
  func verticalPager_constrainedCrossAxisWidth_withZeroBounds_doesNotCrash() {
    let crossPadding: CGFloat = 10
    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.verticalPageItem(
          intrinsicCrossAxisWidth: ConstrainedCrossAxisPagerFixtures.wideIntrinsicCrossAxisSize
        ),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.verticalPagerMetrics(
        crossPadding: crossPadding,
        itemCount: 1
      ),
      path: UIElementPath("vertical-pager-zero-bounds"),
      direction: .vertical
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
      fitting: PagerFittingSize(scrollAxis: scrollAxisSize, crossAxis: crossAxisSize),
      layoutMode: layoutMode
    )

    #expect(crossAxisSize < crossInsetSum)
    #expect(frames.first?.height == expectedHeight)
  }

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
      fitting: PagerFittingSize(scrollAxis: scrollAxisSize, crossAxis: pagerWidth),
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

  @Test
  func verticalPager_resizableCrossInsets_useCrossAxisViewportSize() throws {
    let pagerWidth: CGFloat = 300
    let pagerHeight: CGFloat = 70
    let resizableInsets = InsetMode.Resizable(minValue: 10, maxViewportSize: 14)
    let expectedCrossInset = (pagerWidth - resizableInsets.maxViewportSize) / 2
    let availableCrossAxisWidth = pagerWidth - expectedCrossInset * 2

    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.verticalPageItem(
          intrinsicCrossAxisWidth: ConstrainedCrossAxisPagerFixtures.wideIntrinsicCrossAxisSize
        ),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.resizableCrossAxisPagerMetrics(
        resizableInsets: resizableInsets,
        itemCount: 1
      ),
      path: UIElementPath("vertical-pager-resizable-cross-insets"),
      direction: .vertical
    )

    let layout = PagerViewLayout(
      model: model,
      layoutMode: .pageSize(RelativeValue(rawValue: 0.9)),
      boundsSize: CGSize(width: pagerWidth, height: pagerHeight)
    )

    let firstFrame = try #require(layout.blockFrames.first)

    #expect(firstFrame.minX == expectedCrossInset)
    #expect(firstFrame.width == availableCrossAxisWidth)
    #expect(firstFrame.maxX == pagerWidth - expectedCrossInset)
  }

  @Test
  func horizontalPager_resizableCrossInsets_useCrossAxisViewportSize() throws {
    let pagerWidth: CGFloat = 300
    let pagerHeight: CGFloat = 100
    let resizableInsets = InsetMode.Resizable(minValue: 10, maxViewportSize: 14)
    let expectedCrossInset = (pagerHeight - resizableInsets.maxViewportSize) / 2
    let availableCrossAxisHeight = pagerHeight - expectedCrossInset * 2

    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.horizontalPageItem(
          intrinsicCrossAxisHeight: ConstrainedCrossAxisPagerFixtures.tallIntrinsicCrossAxisSize
        ),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.resizableCrossAxisPagerMetrics(
        resizableInsets: resizableInsets,
        itemCount: 1
      ),
      path: UIElementPath("horizontal-pager-resizable-cross-insets"),
      direction: .horizontal
    )

    let layout = PagerViewLayout(
      model: model,
      layoutMode: .pageSize(RelativeValue(rawValue: 0.9)),
      boundsSize: CGSize(width: pagerWidth, height: pagerHeight)
    )

    let firstFrame = try #require(layout.blockFrames.first)

    #expect(firstFrame.minY == expectedCrossInset)
    #expect(firstFrame.height == availableCrossAxisHeight)
    #expect(firstFrame.maxY == pagerHeight - expectedCrossInset)
  }

  @Test
  func horizontalPager_constrainedCrossAxisHeight_withIndefiniteCrossAxis_usesIntrinsicHeight(
  ) throws {
    let pagerWidth: CGFloat = 300
    let crossPadding: CGFloat = 10
    let childHeight: CGFloat = 60
    let layoutMode = PagerBlock.LayoutMode.pageSize(RelativeValue(rawValue: 0.9))

    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.horizontalPageItem(intrinsicCrossAxisHeight: childHeight),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.horizontalPagerMetrics(
        crossPadding: crossPadding,
        itemCount: 1
      ),
      path: UIElementPath("horizontal-pager-indefinite-cross-axis-height"),
      direction: .horizontal
    )

    let fittingSize = PagerFittingSize(scrollAxis: pagerWidth, crossAxis: nil)
    let frames = model.frames(fitting: fittingSize, layoutMode: layoutMode)

    let firstFrame = try #require(frames.first)

    #expect(firstFrame.height == childHeight)
    #expect(model.intrinsicPagerSize(forWidth: pagerWidth, layoutMode: layoutMode).height
      == crossPadding * 2 + childHeight
    )
  }

  @Test
  func horizontalPager_constrainedCrossAxisHeight_withZeroCrossAxis_clampsToZero() {
    let pagerWidth: CGFloat = 300
    let crossPadding: CGFloat = 10
    let childHeight: CGFloat = 60
    let layoutMode = PagerBlock.LayoutMode.pageSize(RelativeValue(rawValue: 0.9))

    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.horizontalPageItem(intrinsicCrossAxisHeight: childHeight),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.horizontalPagerMetrics(
        crossPadding: crossPadding,
        itemCount: 1
      ),
      path: UIElementPath("horizontal-pager-zero-cross-axis-height"),
      direction: .horizontal
    )

    let frames = model.frames(
      fitting: PagerFittingSize(scrollAxis: pagerWidth, crossAxis: 0),
      layoutMode: layoutMode
    )

    #expect(frames.first?.height == 0)
  }

  @Test
  func verticalPager_constrainedCrossAxisWidth_withIndefiniteCrossAxis_usesIntrinsicWidth() throws {
    let pagerHeight: CGFloat = 70
    let crossPadding: CGFloat = 10
    let childWidth: CGFloat = 250
    let layoutMode = PagerBlock.LayoutMode.pageSize(RelativeValue(rawValue: 0.9))

    let model = GalleryViewModel(
      items: [
        ConstrainedCrossAxisPagerFixtures.verticalPageItem(intrinsicCrossAxisWidth: childWidth),
      ],
      metrics: ConstrainedCrossAxisPagerFixtures.verticalPagerMetrics(
        crossPadding: crossPadding,
        itemCount: 1
      ),
      path: UIElementPath("vertical-pager-indefinite-cross-axis-width"),
      direction: .vertical
    )

    let fittingSize = PagerFittingSize(scrollAxis: pagerHeight, crossAxis: nil)
    let frames = model.frames(fitting: fittingSize, layoutMode: layoutMode)

    let firstFrame = try #require(frames.first)

    #expect(firstFrame.width == childWidth)
    #expect(model.intrinsicPagerSize(forWidth: nil, layoutMode: layoutMode).width
      == crossPadding * 2 + childWidth
    )
  }

  @Test
  func verticalNeighbouredLayout_centerAlignment() {
    let viewportHeight: CGFloat = 390
    let neighbourPageHeight: CGFloat = 10
    let itemSpacing: CGFloat = 4
    let leadingPadding: CGFloat = 40
    let trailingPadding: CGFloat = 0

    let pageHeight = viewportHeight - (neighbourPageHeight + itemSpacing) * 2
    let expected = expectedCenterAlignedLayout(
      viewportSize: viewportHeight,
      pageSize: pageHeight,
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
        direction: .vertical
      ),
      layoutMode: .neighbourPageSize(neighbourPageHeight),
      boundsSize: CGSize(width: 120, height: viewportHeight)
    )

    verifyVerticalLayout(layout, matches: expected)
  }

  @Test
  func percentageLayout_centerAlignment() {
    let viewportWidth: CGFloat = 390
    let itemSpacing: CGFloat = 10
    let leadingPadding: CGFloat = 40
    let trailingPadding: CGFloat = 40
    let pageWidth = viewportWidth

    let expected = expectedCenterAlignedLayout(
      viewportSize: viewportWidth,
      pageSize: pageWidth,
      itemSpacing: itemSpacing,
      leadingPadding: leadingPadding,
      trailingPadding: trailingPadding
    )

    let layout = PagerViewLayout(
      model: galleryModel(
        pageCount: 3,
        itemSpacing: itemSpacing,
        leadingPadding: leadingPadding,
        trailingPadding: trailingPadding
      ),
      layoutMode: .pageSize(RelativeValue(integerLiteral: 1)),
      boundsSize: CGSize(width: viewportWidth, height: 23)
    )

    verifyHorizontalLayout(layout, matches: expected)
  }

  @Test
  func fewSmallPages_endAlignment_pinsScrollToStart() {
    let viewportWidth: CGFloat = 300
    let neighbourPageWidth: CGFloat = 210
    let itemSpacing: CGFloat = 10
    let leadingPadding: CGFloat = 10
    let trailingPadding: CGFloat = 10

    let pageWidth = viewportWidth - trailingPadding - (neighbourPageWidth + itemSpacing)
    let expected = expectedLayoutWhenContentFits(
      pageWidth: pageWidth,
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
        alignment: .trailing
      ),
      layoutMode: .neighbourPageSize(neighbourPageWidth),
      boundsSize: CGSize(width: viewportWidth, height: 60)
    )

    // contentWidth (250) <= viewportWidth (300) → no scrolling
    verifyHorizontalLayout(layout, matches: expected)
  }

  @Test
  func contentOffset_clampsToMaxContentOffset() {
    let viewportWidth: CGFloat = 390
    let neighbourPageWidth: CGFloat = 10
    let itemSpacing: CGFloat = 4
    let leadingPadding: CGFloat = 40
    let trailingPadding: CGFloat = 0
    let pageWidth = viewportWidth - (neighbourPageWidth + itemSpacing) * 2

    let expected = expectedCenterAlignedLayout(
      viewportSize: viewportWidth,
      pageSize: pageWidth,
      itemSpacing: itemSpacing,
      leadingPadding: leadingPadding,
      trailingPadding: trailingPadding
    )
    let layout = PagerViewLayout(
      model: galleryModel(
        pageCount: 3,
        itemSpacing: itemSpacing,
        leadingPadding: leadingPadding,
        trailingPadding: trailingPadding
      ),
      layoutMode: .neighbourPageSize(neighbourPageWidth),
      boundsSize: CGSize(width: viewportWidth, height: 23)
    )

    let lastPageIndex = 2
    let maxContentOffset = expected.contentWidth - viewportWidth
    let lastPageOrigin = expected.pageOrigins[lastPageIndex]
    let lastPageSize = expected.blockPageSizes[lastPageIndex]
    let fractionalPageIndex = CGFloat(lastPageIndex) + 0.5
    let unclampedOffset = lastPageOrigin + lastPageSize * 0.5

    #expect(lastPageOrigin == maxContentOffset)
    #expect(unclampedOffset > maxContentOffset)
    #expect(layout.contentOffset(pageIndex: fractionalPageIndex) == maxContentOffset)
    #expect(layout.contentOffset(pageIndex: fractionalPageIndex) != unclampedOffset)
  }

  @Test
  func horizontalPager_pageContentSize_withMatchParentPageWidth_measuresHeightAtPagerWidth() {
    let pagerWidth: CGFloat = 360
    let layoutMode = PagerBlock.LayoutMode.pageContentSize
    let model = pageContentSizeMatchParentWidthGalleryModel()

    let frames = model.frames(
      fitting: PagerFittingSize(scrollAxis: pagerWidth, crossAxis: nil),
      layoutMode: layoutMode
    )

    #expect(frames.map(\.width) == [pagerWidth, 400, 300])

    let tallestPageHeight = frames.map(\.height).max() ?? 0
    let intrinsicSize = model.intrinsicPagerSize(forWidth: pagerWidth, layoutMode: layoutMode)

    #expect(intrinsicSize.height == tallestPageHeight)
    #expect(tallestPageHeight < 200)
  }

  @Test
  func horizontalPager_pageContentSize_withMatchParentPageWidth_subtractsAxialInsets() {
    let pagerWidth: CGFloat = 360
    let leadingInset: CGFloat = 16
    let trailingInset: CGFloat = 24
    let contentWidth = pagerWidth - leadingInset - trailingInset
    let layoutMode = PagerBlock.LayoutMode.pageContentSize
    let model = pageContentSizeMatchParentWidthGalleryModel(
      axialInsets: SideInsets(leading: leadingInset, trailing: trailingInset)
    )

    let frames = model.frames(
      fitting: PagerFittingSize(scrollAxis: pagerWidth, crossAxis: nil),
      layoutMode: layoutMode
    )

    #expect(frames.map(\.width) == [contentWidth, 400, 300])
    #expect(frames.first?.minX == leadingInset)
  }

  @Test
  func horizontalPager_degenerateBoundedCrossAxis_clampsResizablePagesToZero() {
    let scrollAxisSize: CGFloat = 360
    let crossAxisSize: CGFloat = 20
    let crossInsetSum: CGFloat = 32
    let fixedPageHeight: CGFloat = 100

    let fixedPageItem = GalleryViewModel.Item(
      crossAlignment: .leading,
      content: TextBlock(
        widthTrait: .fixed(120),
        heightTrait: .fixed(fixedPageHeight),
        text: NSAttributedString(string: "fixed")
      )
    )
    let resizablePageItem = GalleryViewModel.Item(
      crossAlignment: .leading,
      content: TextBlock(
        widthTrait: .fixed(120),
        heightTrait: .weighted(.default),
        text: NSAttributedString(string: "resizable")
      )
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
      direction: .horizontal
    )
    let layoutMode = PagerBlock.LayoutMode.pageContentSize

    let boundedFrames = model.frames(
      fitting: PagerFittingSize(scrollAxis: scrollAxisSize, crossAxis: crossAxisSize),
      layoutMode: layoutMode
    )
    #expect(crossAxisSize < crossInsetSum)
    #expect(boundedFrames[0].height == fixedPageHeight)
    #expect(boundedFrames[1].height == 0)

    let intrinsicFrames = model.frames(
      fitting: PagerFittingSize(scrollAxis: scrollAxisSize, crossAxis: nil),
      layoutMode: layoutMode
    )
    #expect(intrinsicFrames[0].height == fixedPageHeight)
    #expect(intrinsicFrames[1].height == fixedPageHeight)
  }

  @Test
  func horizontalPager_degenerateBoundedCrossAxis_clampsCrossAxisViewportToZero() {
    let scrollAxisSize: CGFloat = 300
    let crossAxisSize: CGFloat = 8
    let crossPadding: CGFloat = 10

    let model = GalleryViewModel(
      items: [
        GalleryViewModel.Item(
          crossAlignment: .center,
          content: TextBlock(
            widthTrait: .fixed(120),
            heightTrait: .weighted(.default),
            text: NSAttributedString(string: "page"),
            accessibilityElement: nil
          )
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
      path: UIElementPath("horizontal-pager-degenerate-cross-axis-viewport"),
      direction: .horizontal
    )

    let frames = model.frames(
      fitting: PagerFittingSize(scrollAxis: scrollAxisSize, crossAxis: crossAxisSize),
      layoutMode: .pageSize(RelativeValue(rawValue: 0.9))
    )

    #expect(crossAxisSize < crossPadding * 2)
    #expect(frames.first?.height == 0)
    #expect(frames.first?.minY == crossPadding)
  }

  @Test
  func horizontalPager_clampsResizableItemHeightToConstraints() {
    func item(minHeight: CGFloat, maxHeight: CGFloat) -> GalleryViewModel.Item {
      GalleryViewModel.Item(
        crossAlignment: .leading,
        content: TextBlock(
          widthTrait: .fixed(120),
          heightTrait: .weighted(
            .default,
            minSize: minHeight,
            maxSize: maxHeight
          ),
          text: NSAttributedString(string: "x"),
          accessibilityElement: nil
        )
      )
    }

    let minHeight: CGFloat = 120
    let maxHeight: CGFloat = 80

    let metrics = GalleryViewMetrics(
      axialInsetMode: .fixed(values: SideInsets(leading: 0.0, trailing: 0.0)),
      spacings: [4.0, 4.0],
      crossSpacing: 0.0
    )
    let model = GalleryViewModel(
      items: [
        item(minHeight: 0, maxHeight: .infinity),
        item(minHeight: minHeight, maxHeight: .infinity),
        item(minHeight: 0, maxHeight: maxHeight),
      ],
      metrics: metrics,
      path: UIElementPath("1")
    )
    let crossAxisSize: CGFloat = 100
    let layout = PagerViewLayout(
      model: model,
      layoutMode: .neighbourPageSize(10.0),
      boundsSize: CGSize(width: 360.0, height: crossAxisSize)
    )

    #expect(
      layout.blockFrames.map(\.height) == [
        crossAxisSize,
        minHeight,
        maxHeight,
      ]
    )
  }
}

private struct ExpectedPagerLayout {
  let pageWidths: [CGFloat]
  let pageMinXs: [CGFloat]
  let contentWidth: CGFloat
  let pageOrigins: [CGFloat]
  let blockPageSizes: [CGFloat]
  let contentOffsets: [CGFloat]?
  var clampedContentOffsets: [(pageIndex: CGFloat, offset: CGFloat)]?
}

private func pageLayoutMetrics(
  pageSize: CGFloat,
  itemSpacing: CGFloat,
  leadingPadding: CGFloat,
  trailingPadding: CGFloat,
  pageCount: Int
) -> (pageMinXs: [CGFloat], contentWidth: CGFloat) {
  let pageTotalSize = pageSize + itemSpacing
  var pageMinXs = [CGFloat]()

  for counter in 0..<pageCount {
    pageMinXs.append(leadingPadding + CGFloat(counter) * pageTotalSize)
  }

  let contentWidth = (pageMinXs.last ?? 0) + pageSize + trailingPadding
  return (pageMinXs, contentWidth)
}

private func expectedCenterAlignedLayout(
  viewportSize: CGFloat,
  pageSize: CGFloat,
  itemSpacing: CGFloat,
  leadingPadding: CGFloat,
  trailingPadding: CGFloat,
  pageCount: Int = 3
) -> ExpectedPagerLayout {
  let (pageMinXs, contentWidth) = pageLayoutMetrics(
    pageSize: pageSize,
    itemSpacing: itemSpacing,
    leadingPadding: leadingPadding,
    trailingPadding: trailingPadding,
    pageCount: pageCount
  )
  let centerAlignmentOffset = (viewportSize - pageSize) / 2
  let secondPageScrollOrigin = pageMinXs[1] - centerAlignmentOffset
  let lastPageScrollOrigin = contentWidth - viewportSize
  let pageOrigins = [0, secondPageScrollOrigin, lastPageScrollOrigin]

  return ExpectedPagerLayout(
    pageWidths: [CGFloat](repeating: pageSize, count: pageCount),
    pageMinXs: pageMinXs,
    contentWidth: contentWidth,
    pageOrigins: pageOrigins,
    blockPageSizes: [
      secondPageScrollOrigin,
      lastPageScrollOrigin - secondPageScrollOrigin,
      contentWidth - lastPageScrollOrigin,
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
  let (pageMinXs, contentWidth) = pageLayoutMetrics(
    pageSize: pageSize,
    itemSpacing: itemSpacing,
    leadingPadding: leadingPadding,
    trailingPadding: trailingPadding,
    pageCount: pageCount
  )
  let secondPageScrollOrigin = pageMinXs[1] - leadingPadding
  let lastPageScrollOrigin = contentWidth - viewportSize
  let pageOrigins = [0, secondPageScrollOrigin, lastPageScrollOrigin]

  return ExpectedPagerLayout(
    pageWidths: [CGFloat](repeating: pageSize, count: pageCount),
    pageMinXs: pageMinXs,
    contentWidth: contentWidth,
    pageOrigins: pageOrigins,
    blockPageSizes: [
      secondPageScrollOrigin,
      lastPageScrollOrigin - secondPageScrollOrigin,
      contentWidth - lastPageScrollOrigin,
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
  let (pageMinXs, contentWidth) = pageLayoutMetrics(
    pageSize: pageSize,
    itemSpacing: itemSpacing,
    leadingPadding: leadingPadding,
    trailingPadding: trailingPadding,
    pageCount: pageCount
  )
  let trailingAlignmentOffset = viewportSize - pageSize - trailingPadding
  let secondPageScrollOrigin = pageMinXs[1] - trailingAlignmentOffset
  let lastPageScrollOrigin = contentWidth - viewportSize
  let pageOrigins = [0, secondPageScrollOrigin, lastPageScrollOrigin]

  return ExpectedPagerLayout(
    pageWidths: [CGFloat](repeating: pageSize, count: pageCount),
    pageMinXs: pageMinXs,
    contentWidth: contentWidth,
    pageOrigins: pageOrigins,
    blockPageSizes: [
      secondPageScrollOrigin,
      lastPageScrollOrigin - secondPageScrollOrigin,
      contentWidth - lastPageScrollOrigin,
    ],
    contentOffsets: pageOrigins,
    clampedContentOffsets: [(CGFloat(pageCount - 1) + 0.5, lastPageScrollOrigin)]
  )
}

private func expectedLayoutWhenContentFits(
  pageWidth: CGFloat,
  itemSpacing: CGFloat,
  leadingPadding: CGFloat,
  trailingPadding: CGFloat,
  pageCount: Int = 3
) -> ExpectedPagerLayout {
  let (pageMinXs, contentWidth) = pageLayoutMetrics(
    pageSize: pageWidth,
    itemSpacing: itemSpacing,
    leadingPadding: leadingPadding,
    trailingPadding: trailingPadding,
    pageCount: pageCount
  )
  let pageStep = pageWidth + itemSpacing
  let blockPageSizes = (0..<pageCount).map { index in
    index < pageCount - 1 ? pageStep : contentWidth - (pageMinXs.last ?? 0)
  }

  return ExpectedPagerLayout(
    pageWidths: [CGFloat](repeating: pageWidth, count: pageCount),
    pageMinXs: pageMinXs,
    contentWidth: contentWidth,
    pageOrigins: [CGFloat](repeating: 0, count: pageCount),
    blockPageSizes: blockPageSizes,
    contentOffsets: [CGFloat](repeating: 0, count: pageCount)
  )
}

private func verifyHorizontalLayout(
  _ layout: PagerViewLayout,
  matches expected: ExpectedPagerLayout,
  sourceLocation: SourceLocation = #_sourceLocation
) {
  #expect(
    layout.blockFrames.map(\.width) == expected.pageWidths,
    sourceLocation: sourceLocation
  )
  #expect(
    layout.blockFrames.map(\.minX) == expected.pageMinXs,
    sourceLocation: sourceLocation
  )
  #expect(
    layout.contentSize.width == expected.contentWidth,
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

private func verifyVerticalLayout(
  _ layout: PagerViewLayout,
  matches expected: ExpectedPagerLayout,
  sourceLocation: SourceLocation = #_sourceLocation
) {
  #expect(
    layout.blockFrames.map(\.height) == expected.pageWidths,
    sourceLocation: sourceLocation
  )
  #expect(
    layout.blockFrames.map(\.minY) == expected.pageMinXs,
    sourceLocation: sourceLocation
  )
  #expect(
    layout.contentSize.height == expected.contentWidth,
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

private func pageContentSizeMatchParentWidthGalleryModel(
  axialInsets: SideInsets = .zero
) -> GalleryViewModel {
  func page(
    text: String,
    minWidth: CGFloat = 0,
    maxWidth: CGFloat = .infinity
  ) -> GalleryViewModel.Item {
    GalleryViewModel.Item(
      crossAlignment: .leading,
      content: TextBlock(
        widthTrait: .weighted(.default, minSize: minWidth, maxSize: maxWidth),
        heightTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
        text: NSAttributedString(string: text)
      )
    )
  }

  return GalleryViewModel(
    items: [
      page(text: "First page\nWidth = match_parent\nNo constraints"),
      page(
        text: "Middle page\nPage width = match_parent\nMin width > pager width",
        minWidth: 400
      ),
      page(
        text: "Last page\nPage width = match_parent\nMax width < pager width",
        maxWidth: 300
      ),
    ],
    metrics: GalleryViewMetrics(
      axialInsetMode: .fixed(values: axialInsets),
      spacings: [10, 10],
      crossSpacing: 0
    ),
    path: UIElementPath("horizontal-pager-wrap-content-size-mode-match-parent"),
    direction: .horizontal
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

  static func verticalPagerMetrics(
    crossPadding: CGFloat,
    itemCount: Int
  ) -> GalleryViewMetrics {
    pagerMetrics(crossPadding: crossPadding, itemCount: itemCount)
  }

  static func horizontalPagerMetrics(
    crossPadding: CGFloat,
    itemCount: Int
  ) -> GalleryViewMetrics {
    pagerMetrics(crossPadding: crossPadding, itemCount: itemCount)
  }

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

  static func verticalPageItem(intrinsicCrossAxisWidth: CGFloat) -> GalleryViewModel.Item {
    GalleryViewModel.Item(
      crossAlignment: .leading,
      content: BlockWithFixedWrapContent(
        width: intrinsicCrossAxisWidth,
        constrainedHorizontally: true
      )
    )
  }

  static func horizontalPageItem(intrinsicCrossAxisHeight: CGFloat) -> GalleryViewModel.Item {
    GalleryViewModel.Item(
      crossAlignment: .leading,
      content: BlockWithFixedWrapContent(
        height: intrinsicCrossAxisHeight,
        constrainedVertically: true
      )
    )
  }

  private static func pagerMetrics(
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
