import Foundation
import LayoutKit
import VGSL

struct PagerCrossAxisAlignmentCase: Sendable, CustomStringConvertible {
  let crossAlignment: Alignment
  let expectedCrossAxisOffsets: [CGFloat]

  var description: String {
    "\(crossAlignment)"
  }

  func model(direction: ScrollDirection) -> GalleryViewModel {
    PagerTestFixtures.pagerModel(
      direction: direction,
      crossAxisItemSizes: PagerTestFixtures.crossAxisItemSizes,
      crossAlignment: crossAlignment
    )
  }
}

enum PagerTestFixtures {
  static let crossAxisAlignmentCases = [
    PagerCrossAxisAlignmentCase(crossAlignment: .leading, expectedCrossAxisOffsets: [0, 0]),
    PagerCrossAxisAlignmentCase(crossAlignment: .center, expectedCrossAxisOffsets: [50, 0]),
    PagerCrossAxisAlignmentCase(crossAlignment: .trailing, expectedCrossAxisOffsets: [100, 0]),
  ]

  static let mainAxisPageSize: CGFloat = 60
  static let crossAxisItemSizes: [CGFloat] = [100, 200]
  static let maxCrossAxisItemSize: CGFloat = 200

  static func pageItem(
    direction: ScrollDirection,
    crossAxisSize: CGFloat,
    crossAlignment: Alignment
  ) -> GalleryViewModel.Item {
    GalleryViewModel.Item(
      crossAlignment: crossAlignment,
      content: TestAxis(direction).textBlock(
        along: .fixed(mainAxisPageSize),
        across: .fixed(crossAxisSize)
      )
    )
  }

  static func pagerModel(
    direction: ScrollDirection,
    crossAxisItemSizes: [CGFloat],
    crossAlignment: Alignment
  ) -> GalleryViewModel {
    let gaps = [CGFloat](repeating: 0, times: UInt(crossAxisItemSizes.count + 1))
    return GalleryViewModel(
      items: crossAxisItemSizes.map { size in
        pageItem(direction: direction, crossAxisSize: size, crossAlignment: crossAlignment)
      },
      metrics: GalleryViewMetrics(gaps: gaps),
      path: UIElementPath(direction.isHorizontal ? "horizontal-pager" : "vertical-pager"),
      direction: direction
    )
  }

  static func verticalPagerModel(
    itemWidths: [CGFloat],
    crossAlignment: Alignment
  ) -> GalleryViewModel {
    pagerModel(direction: .vertical, crossAxisItemSizes: itemWidths, crossAlignment: crossAlignment)
  }

  static func horizontalPagerModel(
    itemHeights: [CGFloat],
    crossAlignment: Alignment
  ) -> GalleryViewModel {
    pagerModel(
      direction: .horizontal,
      crossAxisItemSizes: itemHeights,
      crossAlignment: crossAlignment
    )
  }
}

/// Items that are larger than the available cross-axis space (160 vs 100),
/// aligned leading, center and trailing respectively.
enum CrossAxisOverflowFixtures {
  static let availableCrossAxisSize: CGFloat = 100
  static let itemCrossAxisSize: CGFloat = 160
  static let itemMainAxisSize: CGFloat = 60
  static let mainAxisBoundsSize: CGFloat = 300

  static func boundsSize(_ axis: TestAxis) -> CGSize {
    axis.size(along: mainAxisBoundsSize, across: availableCrossAxisSize)
  }

  static func model(direction: ScrollDirection) -> GalleryViewModel {
    let axis = TestAxis(direction)
    let alignments: [Alignment] = [.leading, .center, .trailing]
    let gaps = [CGFloat](repeating: 0, times: UInt(alignments.count + 1))
    return GalleryViewModel(
      items: alignments.map { alignment in
        GalleryViewModel.Item(
          crossAlignment: alignment,
          content: axis.textBlock(
            along: .fixed(itemMainAxisSize),
            across: .fixed(itemCrossAxisSize)
          )
        )
      },
      metrics: GalleryViewMetrics(gaps: gaps),
      path: UIElementPath("cross-axis-overflow"),
      direction: direction
    )
  }
}

/// Items with content of 30 and 80 across the scroll axis that fill the cross-axis slot,
/// a non-filling item of 50 and a filling item limited by its max size of 40.
enum FillingItemsFixtures {
  static func model(direction: ScrollDirection) -> GalleryViewModel {
    let axis = TestAxis(direction)
    func item(across: CGFloat, fills: Bool) -> GalleryViewModel.Item {
      GalleryViewModel.Item(
        crossAlignment: .leading,
        content: axis.fixedWrapContentBlock(along: 50, across: across, constrainedAcross: true),
        fillsCrossAxis: fills
      )
    }
    let limitedItem = GalleryViewModel.Item(
      crossAlignment: .leading,
      content: axis.textBlock(
        along: .fixed(50),
        across: .intrinsic(constrained: true, minSize: 40, maxSize: 40)
      ),
      fillsCrossAxis: true
    )
    let items = [item(across: 30, fills: true), item(across: 80, fills: true)]
      + [item(across: 50, fills: false), limitedItem]
    return GalleryViewModel(
      items: items,
      metrics: GalleryViewMetrics(gaps: [CGFloat](repeating: 0, times: UInt(items.count + 1))),
      path: UIElementPath("filling-items"),
      direction: direction
    )
  }
}
