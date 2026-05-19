import Foundation
@testable @preconcurrency import LayoutKit
import Testing
import VGSL

@Suite
struct GalleryViewLayoutTests {
  @Test
  func producesEqualToModelBlocksCountNumberOfFrames() {
    let model = Blocks.horizontalCenterModel
    let layout = GalleryViewLayout(model: model)
    #expect(layout.blockFrames.count == model.items.count)
  }

  @Test
  func whenHasHorizontalDirection_producesSummaryWidthAndBlockMaximumHeightContentSizePlusCrossInsets(
  ) {
    let layout = GalleryViewLayout(model: Blocks.horizontalCenterModel)
    #expect(layout.contentSize.width == Blocks.contentAxialSize)
    #expect(layout.contentSize.height == Blocks.contentCrossMaxSize + Blocks.fixedInsets.sum)
  }

  @Test
  func whenDoesNotHaveBounds_andHorizontalDirection_appliesResizableBlockHeightToOtherBlocksMaximumHeight(
  ) throws {
    let model = Blocks.horizontalCenterModel
    let layout = GalleryViewLayout(model: model)
    let index = try #require(model.items.firstIndex(where: { $0.content.isVerticallyResizable }))
    #expect(layout.blockFrames[index].height == Blocks.contentCrossMaxSize)
  }

  @Test
  func whenHasBounds_andHorizontalDirection_appliesResizableBlockHeightToInsetedBoundsHeight(
  ) throws {
    let model = Blocks.horizontalCenterModel
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let height = boundsSize.height - Blocks.fixedInsets.sum
    let index = try #require(model.items.firstIndex(where: { $0.content.isVerticallyResizable }))
    #expect(layout.blockFrames[index].height == height)
  }

  @Test
  func whenHasVerticalDirection_producesSummaryHeightAndBlockMaximumWidthContentSizePlusCrossInsets(
  ) {
    let layout = GalleryViewLayout(model: Blocks.verticalCenterModel)
    #expect(layout.contentSize.width == Blocks.contentCrossMaxSize + Blocks.fixedInsets.sum)
    #expect(layout.contentSize.height == Blocks.contentAxialSize)
  }

  @Test
  func whenDoesNotHaveBounds_andVerticalDirection_appliesResizableBlockWidthToOtherBlocksMaximumWidth(
  ) throws {
    let model = Blocks.verticalCenterModel
    let layout = GalleryViewLayout(model: model)
    let index = try #require(model.items.firstIndex(where: { $0.content.isHorizontallyResizable }))
    #expect(layout.blockFrames[index].width == Blocks.contentCrossMaxSize)
  }

  @Test
  func whenHasBounds_andVerticalDirection_appliesResizableBlockWidthToInsetedBoundsWidth() throws {
    let model = Blocks.verticalCenterModel
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let index = try #require(model.items.firstIndex(where: { $0.content.isHorizontallyResizable }))
    #expect(layout.blockFrames[index].width == boundsSize.width - Blocks.fixedInsets.sum)
  }

  @Test
  func whenHasResizableAxialInsets_andHorizontalDirection_calculatesContentSizeFittingProvidedSize(
  ) {
    let model = Blocks.horizontalResizableModel
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let expectedSize = Blocks.contentAxialSizeWithoutSideGaps + boundsSize.width - Blocks
      .resizableInsets.maxViewportSize
    #expect(layout.contentSize.width == expectedSize)
  }

  @Test
  func whenHasResizableAxialInsets_andVerticalDirection_calculatesContentSizeFittingProvidedSize() {
    let model = Blocks.verticalResizableModel
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let expectedSize = Blocks.contentAxialSizeWithoutSideGaps + boundsSize.height - Blocks
      .resizableInsets.maxViewportSize
    #expect(layout.contentSize.height == expectedSize)
  }

  @Test
  func whenHasResizableAxialInsets_andHorizontalDirection_producesSideGapsFittingProvidedSize() {
    let model = Blocks.horizontalResizableModel
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let expectedInsetValue = (boundsSize.width - Blocks.resizableInsets.maxViewportSize) / 2
    #expect(layout.blockFrames.first?.minX == expectedInsetValue)
    #expect(layout.blockFrames.last?.maxX == layout.contentSize.width - expectedInsetValue)
  }

  @Test
  func whenHasResizableAxialInsets_andVerticalDirection_producesSideGapsFittingProvidedSize() {
    let model = Blocks.verticalResizableModel
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let expectedInsetValue = (boundsSize.height - Blocks.resizableInsets.maxViewportSize) / 2
    #expect(layout.blockFrames.first?.minY == expectedInsetValue)
    #expect(layout.blockFrames.last?.maxY == layout.contentSize.height - expectedInsetValue)
  }

  @Test
  func whenHasResizableCrossInsets_andVerticalDirection_producesSideGapsFittingProvidedSize() {
    let model = Blocks.makeModel(
      direction: .vertical,
      metrics: .resizableCross,
      crossAlignment: .leading
    )
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let expectedInsetValue = (boundsSize.width - Blocks.resizableInsets.maxViewportSize) / 2
    #expect(layout.blockFrames.first?.minX == expectedInsetValue)
    #expect(layout.blockFrames.first?.maxX == layout.contentSize.width - expectedInsetValue)
  }

  @Test
  func whenHasResizableCrossInsets_andHorizontalDirection_producesSideGapsFittingProvidedSize() {
    let model = Blocks.makeModel(
      direction: .horizontal,
      metrics: .resizableCross,
      crossAlignment: .leading
    )
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let expectedInsetValue = (boundsSize.height - Blocks.resizableInsets.maxViewportSize) / 2
    #expect(layout.blockFrames.first?.minY == expectedInsetValue)
    #expect(layout.blockFrames.first?.maxY == layout.contentSize.height - expectedInsetValue)
  }

  @Test
  func whenHasHorizontalDirection_startsLeftEdgeAtFirstGapAndFinishesRightEdgeBeforeLastGap() {
    let layout = GalleryViewLayout(model: Blocks.horizontalCenterModel)
    #expect(layout.blockFrames.first?.minX == Blocks.gapSize)
    #expect(layout.blockFrames.last?.maxX == Blocks.gapSize + Blocks.framesAxialSize)
  }

  @Test
  func whenHasHorizontalDirection_andCenterVerticalAlignment_layoutFramesAroundInsetedVerticalCenter(
  ) {
    let layout = GalleryViewLayout(model: Blocks.horizontalCenterModel, boundsSize: boundsSize)
    let center = floor(
      Blocks.fixedInsets.leading + (boundsSize.height - Blocks.fixedInsets.sum) / 2
    )
    #expect(layout.blockFrames.allSatisfy { $0.midY.isApproximatelyEqualTo(center) })
  }

  @Test
  func whenHasHorizontalDirection_andLeadingVerticalAlignment_layoutFramesAboveCrossInsets() {
    let layout = GalleryViewLayout(model: Blocks.horizontalTrailingModel, boundsSize: boundsSize)
    let trailing = boundsSize.height - Blocks.fixedInsets.trailing
    #expect(layout.blockFrames.allSatisfy { $0.maxY.isApproximatelyEqualTo(trailing) })
  }

  @Test
  func whenHasHorizontalDirection_andTrailingVerticalAlignment_layoutFramesBelowCrossInsets() {
    let layout = GalleryViewLayout(model: Blocks.horizontalLeadingModel, boundsSize: boundsSize)
    #expect(
      layout.blockFrames.allSatisfy { $0.minY.isApproximatelyEqualTo(Blocks.fixedInsets.leading) }
    )
  }

  @Test
  func whenHasVerticalDirection_andCenterHorizontalAlignment_layoutFramesAroundInsetedHorizontalCenter(
  ) {
    let layout = GalleryViewLayout(model: Blocks.verticalCenterModel, boundsSize: boundsSize)
    let center = floor(Blocks.fixedInsets.leading + (boundsSize.width - Blocks.fixedInsets.sum) / 2)
    #expect(layout.blockFrames.allSatisfy { $0.midX.isApproximatelyEqualTo(center) })
  }

  @Test
  func whenHasVerticalDirection_andLeadingHorizontalAlignment_layoutFramesAtLeadingOfCrossInset() {
    let layout = GalleryViewLayout(model: Blocks.verticalLeadingModel, boundsSize: boundsSize)
    #expect(
      layout.blockFrames.allSatisfy { $0.minX.isApproximatelyEqualTo(Blocks.fixedInsets.leading) }
    )
  }

  @Test
  func whenHasVerticalDirection_andTrailingHorizontalAlignment_layoutFramesAtTrailingOfCrossInset() {
    let layout = GalleryViewLayout(model: Blocks.verticalTrailingModel, boundsSize: boundsSize)
    let trailing = boundsSize.width - Blocks.fixedInsets.trailing
    #expect(layout.blockFrames.allSatisfy { $0.maxX.isApproximatelyEqualTo(trailing) })
  }

  @Test
  func whenHasVerticalDirection_startsHeightAtFirstGapAndFinishesBeforeLastGap() {
    let layout = GalleryViewLayout(model: Blocks.verticalCenterModel)
    #expect(layout.blockFrames.first?.minY == Blocks.gapSize)
    #expect(layout.blockFrames.last?.maxY == Blocks.gapSize + Blocks.framesAxialSize)
  }

  @Test
  func whenHasPagingModel_returnsLastIndexForLastPageOrigin() {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel)
    let index = layout.blockFrames.count - 1
    let offset = layout.blockPages[index].origin
    let pageIndex = layout.pageIndex(forContentOffset: offset)
    #expect(
      pageIndex.isApproximatelyEqualTo(
        CGFloat(index),
        withAccuracy: accuracy
      )
    )
  }

  @Test
  func whenHasPagingModel_returnsZeroIndexForOffsetOutOfRange() {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel)
    #expect(layout.pageIndex(forContentOffset: -1).isApproximatelyEqualTo(
      0,
      withAccuracy: accuracy
    ))
    #expect(layout.pageIndex(forContentOffset: .infinity).isApproximatelyEqualTo(
      0,
      withAccuracy: accuracy
    ))
  }

  @Test
  func whenHasFixedPagingModel_returnsInitialOffsetForReverseConversion() {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel, boundsSize: boundsSize)
    let page = layout.blockPages[2]
    let offset = page.origin + page.size / 2
    let pageIndex = layout.pageIndex(forContentOffset: offset)
    let foundOffset = layout.contentOffset(pageIndex: pageIndex)
    #expect(offset.isApproximatelyEqualTo(foundOffset, withAccuracy: accuracy))
  }

  @Test
  func whenHasAutoPagingModel_returnsInitialOffsetForReverseConversion() {
    let layout = GalleryViewLayout(model: Blocks.autoPagingModel, boundsSize: boundsSize)
    let page = layout.blockPages[2]
    let offset = page.origin + page.size / 2
    let pageIndex = layout.pageIndex(forContentOffset: offset)
    let foundOffset = layout.contentOffset(pageIndex: pageIndex)
    #expect(offset.isApproximatelyEqualTo(foundOffset, withAccuracy: accuracy))
  }

  @Test
  func whenHasAutoPagingModel_returnsMaxOffsetForLastPage() {
    let layout = GalleryViewLayout(model: Blocks.autoPagingModel, boundsSize: boundsSize)
    let offset = layout.contentOffset(pageIndex: CGFloat(layout.blockPages.count - 1))
    let maxOffset = layout.contentSize.width - boundsSize.width
    #expect(maxOffset.isApproximatelyEqualTo(offset, withAccuracy: accuracy))
  }

  @Test
  func whenHasPagingModel_detectsContainingGreaterOrEqualMinimumAndLowerMaximumWithAccuracy(
  ) throws {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel)
    let page = try #require(layout.blockPages.first)
    let below = page.origin - .leastNonzeroMagnitude
    let middle = page.origin + page.size / 2
    let upper = page.origin + page.size - .leastNonzeroMagnitude
    #expect(page.contains(below))
    #expect(page.contains(middle))
    #expect(!page.contains(upper))
  }

  @Test
  func whenHasPagingModel_containsContinuousPages() {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel)
    let totalSpace = (1..<layout.blockPages.count).reduce(CGFloat(0)) { result, index in
      let page = layout.blockPages[index - 1]
      let nextPage = layout.blockPages[index]
      return result + nextPage.origin - (page.origin + page.size)
    }
    #expect(totalSpace.isApproximatelyEqualTo(0, withAccuracy: accuracy))
  }

  @Test
  func horizontal_resizableHeightContent() {
    let layout = GalleryViewLayout(
      model: GalleryViewModel(
        blocks: [
          TextBlock(
            widthTrait: .intrinsic,
            heightTrait: .resizable,
            text: NSAttributedString(string: "Item 1")
          ),
        ],
        metrics: GalleryViewMetrics(gaps: [10, 10]),
        path: UIElementPath("model"),
        direction: .horizontal
      ),
      boundsSize: CGSize(width: 100, height: 200)
    )
    #expect(layout.blockFrames[0].height == 200)
  }

  @Test
  func vertical_resizableWidthContent() {
    let layout = GalleryViewLayout(
      model: GalleryViewModel(
        blocks: [
          TextBlock(
            widthTrait: .resizable,
            text: NSAttributedString(string: "Item 1")
          ),
        ],
        metrics: GalleryViewMetrics(gaps: [10, 10]),
        path: UIElementPath("model"),
        direction: .vertical
      ),
      boundsSize: CGSize(width: 100, height: 200)
    )
    #expect(layout.blockFrames[0].width == 100)
  }

  // MARK: - Parametrized scrollAlignment tests

  @Test(
    "Leading scroll alignment: item's leading edge aligns with the viewport's leading edge",
    arguments: pagingScrollModes
  )
  func leadingScrollAlignment_itemLeadingEdgeMatchesViewportLeadingEdge(
    scrollMode: GalleryViewModel.ScrollMode
  ) {
    let model = Blocks.makeModel(scrollMode: scrollMode, scrollAlignment: .leading)
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    for (frame, page) in zip(layout.blockFrames, layout.blockPages) {
      #expect(frame.minX.isApproximatelyEqualTo(page.origin, withAccuracy: accuracy))
    }
  }

  @Test(
    "Center scroll alignment: item is centered in the viewport",
    arguments: pagingScrollModes
  )
  func centerScrollAlignment_itemCenteredInViewport(
    scrollMode: GalleryViewModel.ScrollMode
  ) {
    let model = Blocks.makeModel(scrollMode: scrollMode, scrollAlignment: .center)
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let pageSize = pageSizeFor(scrollMode: scrollMode, boundsWidth: boundsSize.width)
    for (frame, page) in zip(layout.blockFrames, layout.blockPages) {
      let viewportCenter = page.origin + pageSize / 2
      #expect(frame.midX.isApproximatelyEqualTo(viewportCenter, withAccuracy: accuracy))
    }
  }

  @Test(
    "Trailing scroll alignment: item's trailing edge aligns with the viewport's trailing edge",
    arguments: pagingScrollModes
  )
  func trailingScrollAlignment_itemTrailingEdgeMatchesViewportTrailingEdge(
    scrollMode: GalleryViewModel.ScrollMode
  ) {
    let model = Blocks.makeModel(scrollMode: scrollMode, scrollAlignment: .trailing)
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let pageSize = pageSizeFor(scrollMode: scrollMode, boundsWidth: boundsSize.width)
    for (frame, page) in zip(layout.blockFrames, layout.blockPages) {
      // Skip pages clamped to origin 0: the geometric invariant only holds
      // when the frame is far enough from the content start.
      guard frame.minX >= pageSize - frame.width else { continue }
      let viewportTrailingEdge = page.origin + pageSize
      #expect(frame.maxX.isApproximatelyEqualTo(viewportTrailingEdge, withAccuracy: accuracy))
    }
  }

  @Test(
    "Page count equals frame count for each paging mode and scrollAlignment",
    arguments: pagingScrollModes, scrollAlignments
  )
  func pageCount_equalsFrameCount(
    scrollMode: GalleryViewModel.ScrollMode,
    alignment: Alignment
  ) {
    let model = Blocks.makeModel(scrollMode: scrollMode, scrollAlignment: alignment)
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    #expect(layout.blockPages.count == layout.blockFrames.count)
  }

  @Test(
    "Roundtrip pageIndex→contentOffset→pageIndex is stable for each paging mode and scrollAlignment",
    arguments: pagingScrollModes, scrollAlignments
  )
  func roundtrip_pageIndexToContentOffsetToPageIndex_isStable(
    scrollMode: GalleryViewModel.ScrollMode,
    alignment: Alignment
  ) throws {
    let model = Blocks.makeModel(scrollMode: scrollMode, scrollAlignment: alignment)
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let page = try #require(layout.blockPages.dropFirst().first)
    let offset = page.origin + page.size / 2
    let pageIndex = layout.pageIndex(forContentOffset: offset)
    let recoveredOffset = layout.contentOffset(pageIndex: pageIndex)
    #expect(offset.isApproximatelyEqualTo(recoveredOffset, withAccuracy: accuracy))
  }

  @Test(
    "For items narrower than the viewport, trailing alignment has the smallest page origin and leading the largest",
    arguments: pagingScrollModes
  )
  func scrollAlignment_pageOriginsOrdered_forNarrowItems(
    scrollMode: GalleryViewModel.ScrollMode
  ) throws {
    let leadingLayout = GalleryViewLayout(
      model: Blocks.makeModel(scrollMode: scrollMode, scrollAlignment: .leading),
      boundsSize: boundsSize
    )
    let centerLayout = GalleryViewLayout(
      model: Blocks.makeModel(scrollMode: scrollMode, scrollAlignment: .center),
      boundsSize: boundsSize
    )
    let trailingLayout = GalleryViewLayout(
      model: Blocks.makeModel(scrollMode: scrollMode, scrollAlignment: .trailing),
      boundsSize: boundsSize
    )
    // Use the last page: it maps to a 1pt-wide resizable block (narrower than the 64pt viewport),
    // so the ordering trailing < center < leading holds regardless of scroll mode.
    let leadingOrigin = try #require(leadingLayout.blockPages.last).origin
    let centerOrigin = try #require(centerLayout.blockPages.last).origin
    let trailingOrigin = try #require(trailingLayout.blockPages.last).origin
    #expect(trailingOrigin < centerOrigin)
    #expect(centerOrigin < leadingOrigin)
  }
}

// MARK: - Test data

private let pagingScrollModes: [GalleryViewModel.ScrollMode] = [
  .autoPaging(inertionEnabled: true),
  .fixedPaging(pageSize: boundsSize.width),
]

private let scrollAlignments: [Alignment] = [.leading, .center, .trailing]

private func pageSizeFor(
  scrollMode: GalleryViewModel.ScrollMode,
  boundsWidth: CGFloat
) -> CGFloat {
  switch scrollMode {
  case .autoPaging, .default:
    boundsWidth
  case let .fixedPaging(pageSize):
    pageSize
  }
}

private enum Blocks {
  static let smallCount = 2
  static let bigCount = 2
  static let resizableCount = 2
  static let totalCount = bigCount + smallCount + resizableCount
  static let blocksCount = smallCount + bigCount + resizableCount
  static let smallSize = CGFloat(64)
  static let bigSize = CGFloat(128)
  static let resizableSize = CGFloat(1)
  static let gapSize = CGFloat(1)
  static let bigAxialSize = bigSize * CGFloat(bigCount)
  static let smallAxialSize = smallSize * CGFloat(smallCount)
  static let resizableAxialSize = resizableSize * CGFloat(resizableCount)
  static let gapAxialSize = gapSize * CGFloat(blocksCount + 1)
  static let framesAxialSize = bigAxialSize + smallAxialSize + resizableAxialSize + gapSize *
    CGFloat(max(blocksCount - 1, 0))
  static let contentAxialSize = bigAxialSize + smallAxialSize + resizableAxialSize + gapAxialSize
  static let contentAxialSizeWithoutSideGaps = contentAxialSize - 2 * gapSize
  static let contentCrossMaxSize = max(bigSize, smallSize)
  static let horizontalCenterModel = makeModel()
  static let horizontalLeadingModel = makeModel(crossAlignment: .leading)
  static let horizontalTrailingModel = makeModel(crossAlignment: .trailing)
  static let verticalCenterModel = makeModel(direction: .vertical)
  static let verticalLeadingModel = makeModel(
    direction: .vertical,
    crossAlignment: .leading
  )
  static let verticalTrailingModel = makeModel(
    direction: .vertical,
    crossAlignment: .trailing
  )
  static let fixedPagingModel = makeModel(scrollMode: .fixedPaging(pageSize: boundsSize.width))
  static let autoPagingModel = makeModel(scrollMode: .autoPaging(inertionEnabled: true))
  static let fixedInsets = SideInsets(leading: 4, trailing: 10)
  static let resizableInsets = InsetMode.Resizable(minValue: 10, maxViewportSize: 14)
  static let horizontalResizableModel = makeModel(metrics: .resizableAxial)
  static let verticalResizableModel = makeModel(
    direction: .vertical,
    metrics: .resizableAxial
  )

  private static let horizontallyResizable: Block = SeparatorBlock(direction: .horizontal)

  private static let verticallyResizable: Block = SeparatorBlock(direction: .vertical)

  private static let small: Block = try! ContainerBlock(
    layoutDirection: .horizontal,
    widthTrait: .fixed(smallSize),
    heightTrait: .fixed(smallSize),
    children: [verticallyResizable]
  )

  private static let big: Block = try! ContainerBlock(
    layoutDirection: .horizontal,
    widthTrait: .fixed(bigSize),
    heightTrait: .fixed(bigSize),
    children: [verticallyResizable]
  )

  static func makeModel(
    direction: ScrollDirection = .horizontal,
    metrics: GalleryViewMetrics = .fixed,
    crossAlignment: Alignment = .center,
    scrollMode: GalleryViewModel.ScrollMode = .default,
    scrollAlignment: Alignment? = nil,
    layoutDirection: UserInterfaceLayoutDirection = .leftToRight
  ) -> GalleryViewModel {
    let resizableBlock = direction.isHorizontal ? verticallyResizable : horizontallyResizable

    let blocks = [Block](repeating: big, times: try! UInt(value: bigCount)) +
      [Block](repeating: small, times: try! UInt(value: smallCount)) +
      [Block](repeating: resizableBlock, times: try! UInt(value: resizableCount))

    let items = blocks.map { GalleryViewModel.Item(crossAlignment: crossAlignment, content: $0) }

    return GalleryViewModel(
      items: items,
      layoutDirection: layoutDirection,
      metrics: metrics,
      scrollMode: scrollMode,
      path: UIElementPath("model"),
      direction: direction,
      scrollAlignment: scrollAlignment
    )
  }
}

private let boundsSize = CGSize(width: 64, height: 64)
private let accuracy: CGFloat = 1e-4

extension GalleryViewMetrics {
  fileprivate static let spacings = [CGFloat](
    repeating: Blocks.gapSize,
    times: try! UInt(value: Blocks.totalCount - 1)
  )

  fileprivate static let resizableAxial = GalleryViewMetrics(
    axialInsetMode: .resizable(params: Blocks.resizableInsets),
    crossInsetMode: .fixed(values: Blocks.fixedInsets),
    spacings: spacings,
    crossSpacing: 0
  )

  fileprivate static let fixed = GalleryViewMetrics(
    axialInsetMode: .fixed(values: SideInsets(leading: Blocks.gapSize, trailing: Blocks.gapSize)),
    crossInsetMode: .fixed(values: Blocks.fixedInsets),
    spacings: spacings,
    crossSpacing: 0
  )

  fileprivate static let resizableCross = GalleryViewMetrics(
    axialInsetMode: .fixed(values: Blocks.fixedInsets),
    crossInsetMode: .resizable(params: Blocks.resizableInsets),
    spacings: spacings,
    crossSpacing: 0
  )
}
