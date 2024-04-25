@testable import LayoutKit

import XCTest

import BaseUIPublic
import CommonCorePublic

final class GalleryViewLayoutTests: XCTestCase {
  func test_ProducesEqualToModelBlocksCountNumberOfFrames() {
    let model = Blocks.horizontalCenterModel

    let layout = GalleryViewLayout(model: model)

    XCTAssertEqual(layout.blockFrames.count, model.items.count)
  }

  func test_WhenHasHorizontalDirection_ProducesSummaryWidthAndBlockMaximumHeightContentSizePlusCrossInsets(
  ) {
    let layout = GalleryViewLayout(model: Blocks.horizontalCenterModel)

    XCTAssertEqual(layout.contentSize.width, Blocks.contentAxialSize)
    XCTAssertEqual(layout.contentSize.height, Blocks.contentCrossMaxSize + Blocks.fixedInsets.sum)
  }

  func test_WhenDoesNotHaveBounds_AndHorizontalDirection_AppliesResizableBlockHeightToOtherBlocksMaximumHeight(
  ) {
    let model = Blocks.horizontalCenterModel

    let layout = GalleryViewLayout(model: model)

    if let index = model.items.firstIndex(where: { $0.content.isVerticallyResizable }) {
      XCTAssertEqual(layout.blockFrames[index].height, Blocks.contentCrossMaxSize)
    } else {
      XCTFail()
    }
  }

  func test_WhenHasBounds_AndHorizontalDirection_AppliesResizableBlockHeightToInsetedBoundsHeight() {
    let model = Blocks.horizontalCenterModel

    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)

    let height = boundsSize.height - Blocks.fixedInsets.sum
    if let index = model.items.firstIndex(where: { $0.content.isVerticallyResizable }) {
      XCTAssertEqual(layout.blockFrames[index].height, height)
    } else {
      XCTFail()
    }
  }

  func test_WhenHasVerticalDirection_ProducesSummaryHeightAndBlockMaximumWidthContentSizePlusCrossInsets(
  ) {
    let layout = GalleryViewLayout(model: Blocks.verticalCenterModel)

    XCTAssertEqual(layout.contentSize.width, Blocks.contentCrossMaxSize + Blocks.fixedInsets.sum)
    XCTAssertEqual(layout.contentSize.height, Blocks.contentAxialSize)
  }

  func test_WhenDoesNotHaveBounds_AndVerticalDirection_AppliesResizableBlockWidthToOtherBlocksMaximumWidth(
  ) {
    let model = Blocks.verticalCenterModel

    let layout = GalleryViewLayout(model: model)

    if let index = model.items.firstIndex(where: { $0.content.isHorizontallyResizable }) {
      XCTAssertEqual(layout.blockFrames[index].width, Blocks.contentCrossMaxSize)
    } else {
      XCTFail()
    }
  }

  func test_WhenHasBounds_AndVerticalDirection_AppliesResizableBlockWidthToInsetedBoundsWidth() {
    let model = Blocks.verticalCenterModel

    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)

    if let index = model.items.firstIndex(where: { $0.content.isHorizontallyResizable }) {
      XCTAssertEqual(layout.blockFrames[index].width, boundsSize.width - Blocks.fixedInsets.sum)
    } else {
      XCTFail()
    }
  }

  func test_WhenHasResizableAxialInsets_AndHorizontalDirection_CalculatesContentSizeFittingProvidedSize(
  ) {
    let model = Blocks.horizontalResizableModel

    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)

    let expectedSize = Blocks.contentAxialSizeWithoutSideGaps + boundsSize.width - Blocks
      .resizableInsets.maxViewportSize
    XCTAssertEqual(layout.contentSize.width, expectedSize)
  }

  func test_WhenHasResizableAxialInsets_AndVerticalDirection_CalculatesContentSizeFittingProvidedSize(
  ) {
    let model = Blocks.verticalResizableModel

    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)

    let expectedSize = Blocks.contentAxialSizeWithoutSideGaps + boundsSize.height - Blocks
      .resizableInsets.maxViewportSize
    XCTAssertEqual(layout.contentSize.height, expectedSize)
  }

  func test_WhenHasResizableAxialInsets_AndHorizontalDirection_ProducesSideGapsFittingProvidedSize(
  ) {
    let model = Blocks.horizontalResizableModel

    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)

    let expectedInsetValue = (boundsSize.width - Blocks.resizableInsets.maxViewportSize) / 2
    XCTAssertEqual(layout.blockFrames.first?.minX, expectedInsetValue)
    XCTAssertEqual(layout.blockFrames.last?.maxX, layout.contentSize.width - expectedInsetValue)
  }

  func test_WhenHasResizableAxialInsets_AndVerticalDirection_ProducesSideGapsFittingProvidedSize() {
    let model = Blocks.verticalResizableModel

    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)

    let expectedInsetValue = (boundsSize.height - Blocks.resizableInsets.maxViewportSize) / 2
    XCTAssertEqual(layout.blockFrames.first?.minY, expectedInsetValue)
    XCTAssertEqual(layout.blockFrames.last?.maxY, layout.contentSize.height - expectedInsetValue)
  }

  func test_WhenHasResizableCrossInsets_AndVerticalDirection_ProducesSideGapsFittingProvidedSize() {
    let model = Blocks.makeModel(
      direction: .vertical,
      metrics: .resizableCross,
      crossAlignment: .leading
    )

    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)

    let expectedInsetValue = (boundsSize.width - Blocks.resizableInsets.maxViewportSize) / 2
    XCTAssertEqual(layout.blockFrames.first?.minX, expectedInsetValue)
    XCTAssertEqual(layout.blockFrames.first?.maxX, layout.contentSize.width - expectedInsetValue)
  }

  func test_WhenHasResizableCrossInsets_AndHorizontalDirection_ProducesSideGapsFittingProvidedSize(
  ) {
    let model = Blocks.makeModel(
      direction: .horizontal,
      metrics: .resizableCross,
      crossAlignment: .leading
    )

    let layout = GalleryViewLayout(model: model, boundsSize: boundsSize)

    let expectedInsetValue = (boundsSize.height - Blocks.resizableInsets.maxViewportSize) / 2
    XCTAssertEqual(layout.blockFrames.first?.minY, expectedInsetValue)
    XCTAssertEqual(layout.blockFrames.first?.maxY, layout.contentSize.height - expectedInsetValue)
  }

  func test_WhenHasHorizontalDirection_StartsLeftEdgeAtFirstGapAndFinishesRightEdgeBeforeLastGap() {
    let layout = GalleryViewLayout(model: Blocks.horizontalCenterModel)

    XCTAssertEqual(layout.blockFrames.first?.minX, Blocks.gapSize)
    XCTAssertEqual(layout.blockFrames.last?.maxX, Blocks.gapSize + Blocks.framesAxialSize)
  }

  func test_WhenHasHorizontalDirection_AndCenterVerticalAlignment_LayoutFramesAroundInsetedVerticalCenter(
  ) {
    let layout = GalleryViewLayout(model: Blocks.horizontalCenterModel, boundsSize: boundsSize)

    let center = floor(
      Blocks.fixedInsets
        .leading + (boundsSize.height - Blocks.fixedInsets.sum) / 2
    )
    XCTAssert(layout.blockFrames.allSatisfy { $0.midY.isApproximatelyEqualTo(center) })
  }

  func test_WhenHasHorizontalDirection_AndLeadingVerticalAlignment_LayoutFramesAboveCrossInsets() {
    let layout = GalleryViewLayout(model: Blocks.horizontalTrailingModel, boundsSize: boundsSize)

    let trailing = boundsSize.height - Blocks.fixedInsets.trailing
    XCTAssert(layout.blockFrames.allSatisfy { $0.maxY.isApproximatelyEqualTo(trailing) })
  }

  func test_WhenHasHorizontalDirection_AndTrailingVerticalAlignment_LayoutFramesBelowCrossInsets() {
    let layout = GalleryViewLayout(model: Blocks.horizontalLeadingModel, boundsSize: boundsSize)

    XCTAssert(
      layout.blockFrames
        .allSatisfy { $0.minY.isApproximatelyEqualTo(Blocks.fixedInsets.leading) }
    )
  }

  func test_WhenHasVerticalDirection_AndCenterHorizontalAlignment_LayoutFramesAroundInsetedHorizontalCenter(
  ) {
    let layout = GalleryViewLayout(model: Blocks.verticalCenterModel, boundsSize: boundsSize)

    let center = floor(Blocks.fixedInsets.leading + (boundsSize.width - Blocks.fixedInsets.sum) / 2)
    XCTAssert(layout.blockFrames.allSatisfy { $0.midX.isApproximatelyEqualTo(center) })
  }

  func test_WhenHasVerticalDirection_AndLeadingHorizontalAlignment_LayoutFramesAtLeadingOfCrossInset(
  ) {
    let layout = GalleryViewLayout(model: Blocks.verticalLeadingModel, boundsSize: boundsSize)

    XCTAssert(
      layout.blockFrames
        .allSatisfy { $0.minX.isApproximatelyEqualTo(Blocks.fixedInsets.leading) }
    )
  }

  func test_WhenHasVerticalDirection_AndTrailingHorizontalAlignment_LayoutFramesAtTrailingOfCrossInset(
  ) {
    let layout = GalleryViewLayout(model: Blocks.verticalTrailingModel, boundsSize: boundsSize)

    let trailing = boundsSize.width - Blocks.fixedInsets.trailing
    XCTAssert(layout.blockFrames.allSatisfy { $0.maxX.isApproximatelyEqualTo(trailing) })
  }

  func test_WhenHasVerticalDirection_StartsHeightAtFirstGapAndFinishesBeforeLastGap() {
    let layout = GalleryViewLayout(model: Blocks.verticalCenterModel)

    XCTAssertEqual(layout.blockFrames.first?.minY, Blocks.gapSize)
    XCTAssertEqual(layout.blockFrames.last?.maxY, Blocks.gapSize + Blocks.framesAxialSize)
  }

  func test_WhenHasPagingModel_ReturnsLastIndexForLastPageOrigin() {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel)
    let index = layout.blockFrames.count - 1
    let offset = layout.blockPages[index].origin

    let pageIndex = layout.pageIndex(forContentOffset: offset)

    XCTAssertEqual(pageIndex, CGFloat(index), accuracy: accuracy)
  }

  func test_WhenHasPagingModel_ReturnsZeroIndexForOffsetOutOfRange() {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel)

    XCTAssertEqual(layout.pageIndex(forContentOffset: -1), 0, accuracy: accuracy)
    XCTAssertEqual(layout.pageIndex(forContentOffset: .infinity), 0, accuracy: accuracy)
  }

  func test_WhenHasFixedPagingModel_ReturnsInitialOffsetForReverseConversion() {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel, boundsSize: boundsSize)
    let page = layout.blockPages[2]
    let offset = page.origin + page.size / 2
    let pageIndex = layout.pageIndex(forContentOffset: offset)
    let foundOffset = layout.contentOffset(pageIndex: pageIndex) + Blocks.gapSize

    XCTAssertEqual(offset, foundOffset, accuracy: accuracy)
  }

  func test_WhenHasAutoPagingModel_ReturnsInitialOffsetForReverseConversion() {
    let layout = GalleryViewLayout(model: Blocks.autoPagingModel, boundsSize: boundsSize)
    let page = layout.blockPages[2]
    let offset = page.origin + page.size / 2
    let pageIndex = layout.pageIndex(forContentOffset: offset)
    let foundOffset = layout.contentOffset(pageIndex: pageIndex) + Blocks.gapSize

    XCTAssertEqual(offset, foundOffset, accuracy: accuracy)
  }

  func test_WhenHasAutoPagingModel_ReturnsMaxOffsetForLastPage() {
    let layout = GalleryViewLayout(model: Blocks.autoPagingModel, boundsSize: boundsSize)
    let offset = layout.contentOffset(
      pageIndex: CGFloat(layout.blockPages.count - 1)
    )
    let maxOffset = layout.contentSize.width - boundsSize.width

    XCTAssertEqual(maxOffset, offset, accuracy: accuracy)
  }

  func test_WhenHasPagingModel_DetectsContainingGreaterOrEqualMinimumAndLowerMaximumWithAccuracy() {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel)

    if let page = layout.blockPages.first {
      let below = page.origin - .leastNonzeroMagnitude
      let middle = page.origin + page.size / 2
      let upper = page.origin + page.size - .leastNonzeroMagnitude

      XCTAssertTrue(page.contains(below))
      XCTAssertTrue(page.contains(middle))
      XCTAssertFalse(page.contains(upper))
    } else {
      XCTFail()
    }
  }

  func test_WhenHasPagingModel_ContainsContinuousPages() {
    let layout = GalleryViewLayout(model: Blocks.fixedPagingModel)

    let totalSpace = (1..<layout.blockPages.count).reduce(CGFloat(0)) { result, index in
      let page = layout.blockPages[index - 1]
      let nextPage = layout.blockPages[index]
      return result + nextPage.origin - (page.origin + page.size)
    }
    XCTAssertEqual(totalSpace, 0, accuracy: accuracy)
  }

  func test_Horizontal_ResizableHeightContent() {
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

    XCTAssertEqual(layout.blockFrames[0].height, 200)
  }

  func test_Vertical_ResizbaleWidthContent() {
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

    XCTAssertEqual(layout.blockFrames[0].width, 100)
  }
}

private enum Blocks {
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
    direction: GalleryViewModel.Direction = .horizontal,
    metrics: GalleryViewMetrics = .fixed,
    crossAlignment: Alignment = .center,
    scrollMode: GalleryViewModel.ScrollMode = .default
  ) -> GalleryViewModel {
    let resizableBlock = direction.isHorizontal ? verticallyResizable : horizontallyResizable

    let blocks = [Block](repeating: big, times: try! UInt(value: bigCount)) +
      [Block](repeating: small, times: try! UInt(value: smallCount)) +
      [Block](repeating: resizableBlock, times: try! UInt(value: resizableCount))

    return GalleryViewModel(
      blocks: blocks,
      metrics: metrics,
      scrollMode: scrollMode,
      path: UIElementPath("model"),
      direction: direction,
      crossAlignment: crossAlignment
    )
  }

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
  static let autoPagingModel = makeModel(scrollMode: .autoPaging)
  static let fixedInsets = SideInsets(leading: 4, trailing: 10)
  static let resizableInsets = InsetMode.Resizable(minValue: 10, maxViewportSize: 14)
  static let horizontalResizableModel = makeModel(metrics: .resizableAxial)
  static let verticalResizableModel = makeModel(
    direction: .vertical,
    metrics: .resizableAxial
  )
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
