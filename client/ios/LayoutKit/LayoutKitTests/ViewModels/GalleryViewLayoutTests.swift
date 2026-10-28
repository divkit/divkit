import Foundation
@testable @preconcurrency import LayoutKit
import Testing
import VGSL

@Suite
struct GalleryViewLayoutTests {
  @Test(arguments: ScrollDirection.allCases)
  func producesEqualToModelBlocksCountNumberOfFrames(direction: ScrollDirection) {
    let model = Blocks.makeModel(direction: direction)
    let layout = GalleryViewLayout(model: model)
    #expect(layout.blockFrames.count == model.items.count)
  }

  @Test(arguments: ScrollDirection.allCases)
  func producesSummaryAxialSizeAndBlockMaximumCrossSizeContentSizePlusCrossInsets(
    direction: ScrollDirection
  ) {
    let axis = TestAxis(direction)
    let layout = GalleryViewLayout(model: Blocks.makeModel(direction: direction))
    #expect(axis.along(layout.contentSize) == Blocks.contentAxialSize)
    #expect(
      axis.across(layout.contentSize) == Blocks.contentCrossMaxSize + Blocks.fixedInsets.sum
    )
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenDoesNotHaveBounds_appliesOtherBlocksMaximumCrossSizeToCrossResizableBlock(
    direction: ScrollDirection
  ) throws {
    let axis = TestAxis(direction)
    let model = Blocks.makeModel(direction: direction)
    let layout = GalleryViewLayout(model: model)
    let index = try #require(model.items.firstIndex(where: { axis.isAcrossResizable($0.content) }))
    #expect(axis.acrossSize(layout.blockFrames[index]) == Blocks.contentCrossMaxSize)
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasBounds_appliesInsetedBoundsCrossSizeToCrossResizableBlock(
    direction: ScrollDirection
  ) throws {
    let axis = TestAxis(direction)
    let model = Blocks.makeModel(direction: direction)
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let index = try #require(model.items.firstIndex(where: { axis.isAcrossResizable($0.content) }))
    #expect(
      axis.acrossSize(layout.blockFrames[index])
        == axis.across(boundsSize) - Blocks.fixedInsets.sum
    )
  }

  @Test(arguments: ScrollDirection.allCases)
  func clampsCrossResizableItemToItsConstraints(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    func item(minSize: CGFloat, maxSize: CGFloat) -> GalleryViewModel.Item {
      GalleryViewModel.Item(
        crossAlignment: .leading,
        content: axis.textBlock(
          along: .fixed(90),
          across: .weighted(.default, minSize: minSize, maxSize: maxSize),
          text: "x"
        )
      )
    }
    let metrics = GalleryViewMetrics(
      axialInsetMode: .fixed(values: SideInsets(leading: 0, trailing: 0)),
      crossInsetMode: .fixed(values: SideInsets(leading: 0, trailing: 0)),
      spacings: [0, 0],
      crossSpacing: 0
    )
    let model = GalleryViewModel(
      items: [
        item(minSize: 0, maxSize: .infinity),
        item(minSize: 110, maxSize: .infinity),
        item(minSize: 0, maxSize: 60),
      ],
      layoutDirection: .leftToRight,
      metrics: metrics,
      scrollMode: .default,
      path: UIElementPath("model"),
      direction: direction,
      scrollAlignment: nil
    )
    let layout = GalleryViewLayout(model: model, boundsSize: axis.size(along: 300, across: 100))

    // match_parent items fill the 100pt cross size, each clamped to its own min/max.
    #expect(layout.blockFrames.map(axis.acrossSize) == [100, 110, 60])
  }

  @Test(arguments: ScrollDirection.allCases)
  func crossAxisOverflow_alignsItemsRelativeToAvailableSpace(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let layout = GalleryViewLayout(
      model: CrossAxisOverflowFixtures.model(direction: direction),
      boundsSize: CrossAxisOverflowFixtures.boundsSize(axis)
    )

    // Items overflow the 100pt cross size by 60pt: center splits overflow evenly,
    // trailing pushes it before the leading edge.
    #expect(layout.blockFrames.map(axis.minAcross) == [0, -30, -60])
  }

  @Test(arguments: ScrollDirection.allCases)
  func constrainedCrossAxisSize_respectsCrossInsets(direction: ScrollDirection) throws {
    let axis = TestAxis(direction)
    // Original inputs of the former horizontal and vertical tests.
    let (galleryAlong, galleryAcross, itemAlong, itemAcross): (CGFloat, CGFloat, CGFloat, CGFloat) =
      direction.isHorizontal
        ? (300, 100, 120, ConstrainedCrossAxisGalleryFixtures.tallIntrinsicCrossAxisSize)
        : (100, 300, 60, ConstrainedCrossAxisGalleryFixtures.wideIntrinsicCrossAxisSize)
    let crossPadding: CGFloat = 10
    let availableCrossAxisSize = galleryAcross - crossPadding * 2

    let model = GalleryViewModel(
      items: [
        GalleryViewModel.Item(
          crossAlignment: .leading,
          content: axis.fixedWrapContentBlock(
            along: itemAlong,
            across: itemAcross,
            constrainedAcross: true
          )
        ),
      ],
      metrics: ConstrainedCrossAxisGalleryFixtures.galleryMetrics(crossPadding: crossPadding),
      path: UIElementPath("gallery-constrained-cross-axis"),
      direction: direction
    )

    let layout = GalleryViewLayout(
      model: model,
      boundsSize: axis.size(along: galleryAlong, across: galleryAcross)
    )

    let firstFrame = try #require(layout.blockFrames.first)

    #expect(axis.minAcross(firstFrame) == crossPadding)
    #expect(axis.acrossSize(firstFrame) == availableCrossAxisSize)
    #expect(axis.maxAcross(firstFrame) == galleryAcross - crossPadding)
  }

  @Test(arguments: ScrollDirection.allCases)
  func degenerateBoundedCrossAxis_clampsCrossAxisViewportToZero(
    direction: ScrollDirection
  ) throws {
    let axis = TestAxis(direction)
    let galleryAcross: CGFloat = 8
    let crossPadding: CGFloat = 10
    let itemAcross: CGFloat = 20

    let model = GalleryViewModel(
      items: [
        GalleryViewModel.Item(
          crossAlignment: .center,
          content: axis.fixedWrapContentBlock(along: 120, across: itemAcross)
        ),
      ],
      metrics: ConstrainedCrossAxisGalleryFixtures.galleryMetrics(crossPadding: crossPadding),
      path: UIElementPath("gallery-degenerate-cross-axis-viewport"),
      direction: direction
    )

    let layout = GalleryViewLayout(
      model: model,
      boundsSize: axis.size(along: 300, across: galleryAcross)
    )

    let firstFrame = try #require(layout.blockFrames.first)

    #expect(galleryAcross < crossPadding * 2)
    #expect(axis.acrossSize(firstFrame) == itemAcross)
    #expect(axis.minAcross(firstFrame) == 0)
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasSeveralColumns_producesContentAxialSizeOfLongestColumn(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let model = MultiColumnGalleryFixtures.makeModel(direction: direction)
    let layout = GalleryViewLayout(model: model)
    #expect(axis.along(layout.contentSize) == 100)
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasSeveralColumns_lastPageEndsAtLongestColumnEnd(direction: ScrollDirection) throws {
    let layout = GalleryViewLayout(
      model: MultiColumnGalleryFixtures.makeModel(direction: direction),
      boundsSize: CGSize(width: 50, height: 50)
    )
    let lastPage = try #require(layout.blockPages.last)
    #expect(lastPage.origin + lastPage.size == 100)
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasResizableAxialInsets_calculatesContentSizeFittingProvidedSize(
    direction: ScrollDirection
  ) {
    let axis = TestAxis(direction)
    let model = Blocks.makeModel(direction: direction, metrics: .resizableAxial)
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let expectedSize = Blocks.contentAxialSizeWithoutSideGaps + axis.along(boundsSize) - Blocks
      .resizableInsets.maxViewportSize
    #expect(axis.along(layout.contentSize) == expectedSize)
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasResizableAxialInsets_producesSideGapsFittingProvidedSize(
    direction: ScrollDirection
  ) {
    let axis = TestAxis(direction)
    let model = Blocks.makeModel(direction: direction, metrics: .resizableAxial)
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let expectedInsetValue = (axis.along(boundsSize) - Blocks.resizableInsets.maxViewportSize) / 2
    #expect(layout.blockFrames.first.map(axis.minAlong) == expectedInsetValue)
    #expect(
      layout.blockFrames.last.map(axis.maxAlong)
        == axis.along(layout.contentSize) - expectedInsetValue
    )
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasResizableCrossInsets_producesSideGapsFittingProvidedSize(
    direction: ScrollDirection
  ) {
    let axis = TestAxis(direction)
    let model = Blocks.makeModel(
      direction: direction,
      metrics: .resizableCross,
      crossAlignment: .leading
    )
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let expectedInsetValue = (axis.across(boundsSize) - Blocks.resizableInsets.maxViewportSize) / 2
    #expect(layout.blockFrames.first.map(axis.minAcross) == expectedInsetValue)
    #expect(
      layout.blockFrames.first.map(axis.maxAcross)
        == axis.across(layout.contentSize) - expectedInsetValue
    )
  }

  @Test(arguments: ScrollDirection.allCases)
  func startsAtFirstGapAndFinishesBeforeLastGap(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let layout = GalleryViewLayout(model: Blocks.makeModel(direction: direction))
    #expect(layout.blockFrames.first.map(axis.minAlong) == Blocks.gapSize)
    #expect(
      layout.blockFrames.last.map(axis.maxAlong) == Blocks.gapSize + Blocks.framesAxialSize
    )
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasCenterCrossAlignment_layoutFramesAroundInsetedCrossCenter(
    direction: ScrollDirection
  ) {
    let axis = TestAxis(direction)
    let layout = GalleryViewLayout(
      model: Blocks.makeModel(direction: direction, crossAlignment: .center),
      boundsSize: boundsSize
    )
    let center = floor(
      Blocks.fixedInsets.leading + (axis.across(boundsSize) - Blocks.fixedInsets.sum) / 2
    )
    #expect(layout.blockFrames.allSatisfy { axis.midAcross($0).isApproximatelyEqualTo(center) })
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasLeadingCrossAlignment_layoutFramesAtLeadingCrossInset(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let layout = GalleryViewLayout(
      model: Blocks.makeModel(direction: direction, crossAlignment: .leading),
      boundsSize: boundsSize
    )
    #expect(
      layout.blockFrames.allSatisfy {
        axis.minAcross($0).isApproximatelyEqualTo(Blocks.fixedInsets.leading)
      }
    )
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasTrailingCrossAlignment_layoutFramesAtTrailingCrossInset(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let layout = GalleryViewLayout(
      model: Blocks.makeModel(direction: direction, crossAlignment: .trailing),
      boundsSize: boundsSize
    )
    let trailing = axis.across(boundsSize) - Blocks.fixedInsets.trailing
    #expect(layout.blockFrames.allSatisfy { axis.maxAcross($0).isApproximatelyEqualTo(trailing) })
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasPagingModel_returnsLastIndexForLastPageOrigin(direction: ScrollDirection) {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel(direction: direction))
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

  @Test(arguments: ScrollDirection.allCases)
  func whenHasPagingModel_returnsZeroIndexForOffsetOutOfRange(direction: ScrollDirection) {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel(direction: direction))
    #expect(layout.pageIndex(forContentOffset: -1).isApproximatelyEqualTo(
      0,
      withAccuracy: accuracy
    ))
    #expect(layout.pageIndex(forContentOffset: .infinity).isApproximatelyEqualTo(
      0,
      withAccuracy: accuracy
    ))
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasFixedPagingModel_returnsInitialOffsetForReverseConversion(
    direction: ScrollDirection
  ) {
    let layout = GalleryViewLayout(
      model: Blocks.fixedPagingModel(direction: direction),
      boundsSize: boundsSize
    )
    let page = layout.blockPages[2]
    let offset = page.origin + page.size / 2
    let pageIndex = layout.pageIndex(forContentOffset: offset)
    let foundOffset = layout.contentOffset(pageIndex: pageIndex)
    #expect(offset.isApproximatelyEqualTo(foundOffset, withAccuracy: accuracy))
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasAutoPagingModel_returnsInitialOffsetForReverseConversion(
    direction: ScrollDirection
  ) {
    let layout = GalleryViewLayout(
      model: Blocks.autoPagingModel(direction: direction),
      boundsSize: boundsSize
    )
    let page = layout.blockPages[2]
    let offset = page.origin + page.size / 2
    let pageIndex = layout.pageIndex(forContentOffset: offset)
    let foundOffset = layout.contentOffset(pageIndex: pageIndex)
    #expect(offset.isApproximatelyEqualTo(foundOffset, withAccuracy: accuracy))
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasAutoPagingModel_returnsMaxOffsetForLastPage(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let layout = GalleryViewLayout(
      model: Blocks.autoPagingModel(direction: direction),
      boundsSize: boundsSize
    )
    let offset = layout.contentOffset(pageIndex: CGFloat(layout.blockPages.count - 1))
    let maxOffset = axis.along(layout.contentSize) - axis.along(boundsSize)
    #expect(maxOffset.isApproximatelyEqualTo(offset, withAccuracy: accuracy))
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasPagingModel_detectsContainingGreaterOrEqualMinimumAndLowerMaximumWithAccuracy(
    direction: ScrollDirection
  ) throws {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel(direction: direction))
    let page = try #require(layout.blockPages.first)
    let below = page.origin - .leastNonzeroMagnitude
    let middle = page.origin + page.size / 2
    let upper = page.origin + page.size - .leastNonzeroMagnitude
    #expect(page.contains(below))
    #expect(page.contains(middle))
    #expect(!page.contains(upper))
  }

  @Test(arguments: ScrollDirection.allCases)
  func whenHasPagingModel_containsContinuousPages(direction: ScrollDirection) {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel(direction: direction))
    let totalSpace = (1..<layout.blockPages.count).reduce(CGFloat(0)) { result, index in
      let page = layout.blockPages[index - 1]
      let nextPage = layout.blockPages[index]
      return result + nextPage.origin - (page.origin + page.size)
    }
    #expect(totalSpace.isApproximatelyEqualTo(0, withAccuracy: accuracy))
  }

  @Test(arguments: ScrollDirection.allCases)
  func fillingItems_withoutBounds_takePartInSlotAndFillIt(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let layout = GalleryViewLayout(model: FillingItemsFixtures.model(direction: direction))

    // The slot fits the largest content (80), filling items stretch to it, the others keep theirs.
    #expect(layout.blockFrames.map(axis.acrossSize) == [80, 80, 50, 40])
    #expect(axis.across(layout.contentSize) == 80)
  }

  @Test(arguments: ScrollDirection.allCases)
  func fillingItems_withBounds_fillBoundedSlot(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let layout = GalleryViewLayout(
      model: FillingItemsFixtures.model(direction: direction),
      boundsSize: axis.size(along: 300, across: 100)
    )

    #expect(layout.blockFrames.map(axis.acrossSize) == [100, 100, 50, 40])
  }

  @Test(arguments: ScrollDirection.allCases)
  func crossResizableContent_fillsBoundsCrossSize(direction: ScrollDirection) {
    let axis = TestAxis(direction)
    let layout = GalleryViewLayout(
      model: GalleryViewModel(
        blocks: [
          axis.textBlock(along: .intrinsic, across: .resizable, text: "Item 1"),
        ],
        metrics: GalleryViewMetrics(gaps: [10, 10]),
        path: UIElementPath("model"),
        direction: direction
      ),
      // Original bounds of the former horizontal and vertical tests.
      boundsSize: CGSize(width: 100, height: 200)
    )
    #expect(axis.acrossSize(layout.blockFrames[0]) == axis.across(CGSize(width: 100, height: 200)))
  }

  // MARK: - Parametrized scrollAlignment tests

  @Test(
    "Leading scroll alignment: item's leading edge aligns with the viewport's leading edge",
    arguments: pagingScrollModes, ScrollDirection.allCases
  )
  func leadingScrollAlignment_itemLeadingEdgeMatchesViewportLeadingEdge(
    scrollMode: GalleryViewModel.ScrollMode,
    direction: ScrollDirection
  ) {
    let axis = TestAxis(direction)
    let model = Blocks.makeModel(
      direction: direction,
      scrollMode: scrollMode,
      scrollAlignment: .leading
    )
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    for (frame, page) in zip(layout.blockFrames, layout.blockPages) {
      #expect(axis.minAlong(frame).isApproximatelyEqualTo(page.origin, withAccuracy: accuracy))
    }
  }

  @Test(
    "Center scroll alignment: item is centered in the viewport",
    arguments: pagingScrollModes, ScrollDirection.allCases
  )
  func centerScrollAlignment_itemCenteredInViewport(
    scrollMode: GalleryViewModel.ScrollMode,
    direction: ScrollDirection
  ) {
    let axis = TestAxis(direction)
    let model = Blocks.makeModel(
      direction: direction,
      scrollMode: scrollMode,
      scrollAlignment: .center
    )
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let pageSize = pageSizeFor(scrollMode: scrollMode, boundsAxialSize: axis.along(boundsSize))
    for (frame, page) in zip(layout.blockFrames, layout.blockPages) {
      let viewportCenter = page.origin + pageSize / 2
      #expect(axis.midAlong(frame).isApproximatelyEqualTo(viewportCenter, withAccuracy: accuracy))
    }
  }

  @Test(
    "Trailing scroll alignment: item's trailing edge aligns with the viewport's trailing edge",
    arguments: pagingScrollModes, ScrollDirection.allCases
  )
  func trailingScrollAlignment_itemTrailingEdgeMatchesViewportTrailingEdge(
    scrollMode: GalleryViewModel.ScrollMode,
    direction: ScrollDirection
  ) {
    let axis = TestAxis(direction)
    let model = Blocks.makeModel(
      direction: direction,
      scrollMode: scrollMode,
      scrollAlignment: .trailing
    )
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let pageSize = pageSizeFor(scrollMode: scrollMode, boundsAxialSize: axis.along(boundsSize))
    for (frame, page) in zip(layout.blockFrames, layout.blockPages) {
      // Skip pages clamped to origin 0: the geometric invariant only holds
      // when the frame is far enough from the content start.
      guard axis.minAlong(frame) >= pageSize - axis.alongSize(frame) else { continue }
      let viewportTrailingEdge = page.origin + pageSize
      #expect(
        axis.maxAlong(frame).isApproximatelyEqualTo(viewportTrailingEdge, withAccuracy: accuracy)
      )
    }
  }

  @Test(
    "Page count equals frame count for each paging mode and scrollAlignment",
    arguments: pagingCases, ScrollDirection.allCases
  )
  func pageCount_equalsFrameCount(pagingCase: PagingCase, direction: ScrollDirection) {
    let model = Blocks.makeModel(
      direction: direction,
      scrollMode: pagingCase.scrollMode,
      scrollAlignment: pagingCase.alignment
    )
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    #expect(layout.blockPages.count == layout.blockFrames.count)
  }

  @Test(
    "Roundtrip pageIndex→contentOffset→pageIndex is stable for each paging mode and scrollAlignment",
    arguments: pagingCases, ScrollDirection.allCases
  )
  func roundtrip_pageIndexToContentOffsetToPageIndex_isStable(
    pagingCase: PagingCase,
    direction: ScrollDirection
  ) throws {
    let model = Blocks.makeModel(
      direction: direction,
      scrollMode: pagingCase.scrollMode,
      scrollAlignment: pagingCase.alignment
    )
    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)
    let page = try #require(layout.blockPages.dropFirst().first)
    let offset = page.origin + page.size / 2
    let pageIndex = layout.pageIndex(forContentOffset: offset)
    let recoveredOffset = layout.contentOffset(pageIndex: pageIndex)
    #expect(offset.isApproximatelyEqualTo(recoveredOffset, withAccuracy: accuracy))
  }

  @Test(
    "For items narrower than the viewport, trailing alignment has the smallest page origin and leading the largest",
    arguments: pagingScrollModes, ScrollDirection.allCases
  )
  func scrollAlignment_pageOriginsOrdered_forNarrowItems(
    scrollMode: GalleryViewModel.ScrollMode,
    direction: ScrollDirection
  ) throws {
    func layout(_ scrollAlignment: Alignment) -> GalleryViewLayout {
      GalleryViewLayout(
        model: Blocks.makeModel(
          direction: direction,
          scrollMode: scrollMode,
          scrollAlignment: scrollAlignment
        ),
        boundsSize: boundsSize
      )
    }
    // Use the last page: it maps to a 1pt-wide resizable block (narrower than the 64pt viewport),
    // so the ordering trailing < center < leading holds regardless of scroll mode.
    let leadingOrigin = try #require(layout(.leading).blockPages.last).origin
    let centerOrigin = try #require(layout(.center).blockPages.last).origin
    let trailingOrigin = try #require(layout(.trailing).blockPages.last).origin
    #expect(trailingOrigin < centerOrigin)
    #expect(centerOrigin < leadingOrigin)
  }
}

// MARK: - Test data

struct PagingCase: Sendable, CustomTestStringConvertible {
  let scrollMode: GalleryViewModel.ScrollMode
  let alignment: Alignment

  var testDescription: String {
    "\(scrollMode), \(alignment)"
  }
}

private let pagingScrollModes: [GalleryViewModel.ScrollMode] = [
  .autoPaging(inertionEnabled: true),
  .fixedPaging(pageSize: boundsSize.width),
]

private let scrollAlignments: [Alignment] = [.leading, .center, .trailing]

private let pagingCases: [PagingCase] = pagingScrollModes.flatMap { scrollMode in
  scrollAlignments.map { PagingCase(scrollMode: scrollMode, alignment: $0) }
}

private func pageSizeFor(
  scrollMode: GalleryViewModel.ScrollMode,
  boundsAxialSize: CGFloat
) -> CGFloat {
  switch scrollMode {
  case .autoPaging, .default:
    boundsAxialSize
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
  static let fixedInsets = SideInsets(leading: 4, trailing: 10)
  static let resizableInsets = InsetMode.Resizable(minValue: 10, maxViewportSize: 14)

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

  static func fixedPagingModel(direction: ScrollDirection) -> GalleryViewModel {
    makeModel(direction: direction, scrollMode: .fixedPaging(pageSize: boundsSize.width))
  }

  static func autoPagingModel(direction: ScrollDirection) -> GalleryViewModel {
    makeModel(direction: direction, scrollMode: .autoPaging(inertionEnabled: true))
  }

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

private enum MultiColumnGalleryFixtures {
  static func makeModel(direction: ScrollDirection) -> GalleryViewModel {
    let blocks: [Block] = [100, 10, 10].map {
      TextBlock(
        widthTrait: .fixed(direction.isHorizontal ? $0 : 10),
        heightTrait: .fixed(direction.isHorizontal ? 10 : $0),
        text: NSAttributedString(string: "x"),
        accessibilityElement: nil
      )
    }
    return GalleryViewModel(
      blocks: blocks,
      metrics: GalleryViewMetrics(
        axialInsetMode: .fixed(values: .zero),
        crossInsetMode: .fixed(values: .zero),
        spacings: [0, 0],
        crossSpacing: 0
      ),
      path: UIElementPath("model"),
      direction: direction,
      columnCount: 2
    )
  }
}

private let boundsSize = CGSize(width: 64, height: 64)
private let accuracy: CGFloat = 1e-4

private enum ConstrainedCrossAxisGalleryFixtures {
  static let wideIntrinsicCrossAxisSize: CGFloat = 400
  static let tallIntrinsicCrossAxisSize: CGFloat = 150

  static func galleryMetrics(crossPadding: CGFloat) -> GalleryViewMetrics {
    GalleryViewMetrics(
      axialInsetMode: .fixed(values: .zero),
      crossInsetMode: .fixed(
        values: SideInsets(leading: crossPadding, trailing: crossPadding)
      ),
      spacings: [],
      crossSpacing: 0
    )
  }
}

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
