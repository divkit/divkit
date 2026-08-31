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
