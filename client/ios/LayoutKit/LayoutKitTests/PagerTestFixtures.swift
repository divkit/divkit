import Foundation
import LayoutKit
import VGSL

struct PagerCrossAxisAlignmentCase: @unchecked Sendable {
  let model: GalleryViewModel
  let expectedCrossAxisOffsets: [CGFloat]
}

enum PagerTestFixtures {
  enum Vertical {
    static let leadingCase = PagerCrossAxisAlignmentCase(
      model: verticalPagerModel(itemWidths: crossAxisItemSizes, crossAlignment: .leading),
      expectedCrossAxisOffsets: [0, 0]
    )
    static let centerCase = PagerCrossAxisAlignmentCase(
      model: verticalPagerModel(itemWidths: crossAxisItemSizes, crossAlignment: .center),
      expectedCrossAxisOffsets: [50, 0]
    )
    static let trailingCase = PagerCrossAxisAlignmentCase(
      model: verticalPagerModel(itemWidths: crossAxisItemSizes, crossAlignment: .trailing),
      expectedCrossAxisOffsets: [100, 0]
    )
  }

  enum Horizontal {
    static let leadingCase = PagerCrossAxisAlignmentCase(
      model: horizontalPagerModel(itemHeights: crossAxisItemSizes, crossAlignment: .leading),
      expectedCrossAxisOffsets: [0, 0]
    )
    static let centerCase = PagerCrossAxisAlignmentCase(
      model: horizontalPagerModel(itemHeights: crossAxisItemSizes, crossAlignment: .center),
      expectedCrossAxisOffsets: [50, 0]
    )
    static let trailingCase = PagerCrossAxisAlignmentCase(
      model: horizontalPagerModel(itemHeights: crossAxisItemSizes, crossAlignment: .trailing),
      expectedCrossAxisOffsets: [100, 0]
    )
  }

  static let mainAxisPageSize: CGFloat = 60
  static let crossAxisItemSizes: [CGFloat] = [100, 200]
  static let maxCrossAxisItemSize: CGFloat = 200

  static func verticalPageItem(
    crossAxisWidth: CGFloat,
    crossAlignment: Alignment
  ) -> GalleryViewModel.Item {
    GalleryViewModel.Item(
      crossAlignment: crossAlignment,
      content: TextBlock(
        widthTrait: .fixed(crossAxisWidth),
        heightTrait: .fixed(mainAxisPageSize),
        text: NSAttributedString(string: "")
      )
    )
  }

  static func horizontalPageItem(
    crossAxisHeight: CGFloat,
    crossAlignment: Alignment
  ) -> GalleryViewModel.Item {
    GalleryViewModel.Item(
      crossAlignment: crossAlignment,
      content: TextBlock(
        widthTrait: .fixed(mainAxisPageSize),
        heightTrait: .fixed(crossAxisHeight),
        text: NSAttributedString(string: "")
      )
    )
  }

  static func verticalPagerModel(
    itemWidths: [CGFloat],
    crossAlignment: Alignment
  ) -> GalleryViewModel {
    let gaps = [CGFloat](repeating: 0, count: itemWidths.count + 1)
    return GalleryViewModel(
      items: itemWidths.map { width in
        verticalPageItem(crossAxisWidth: width, crossAlignment: crossAlignment)
      },
      metrics: GalleryViewMetrics(gaps: gaps),
      path: UIElementPath("vertical-pager"),
      direction: .vertical
    )
  }

  static func horizontalPagerModel(
    itemHeights: [CGFloat],
    crossAlignment: Alignment
  ) -> GalleryViewModel {
    let gaps = [CGFloat](repeating: 0, count: itemHeights.count + 1)
    return GalleryViewModel(
      items: itemHeights.map { height in
        horizontalPageItem(crossAxisHeight: height, crossAlignment: crossAlignment)
      },
      metrics: GalleryViewMetrics(gaps: gaps),
      path: UIElementPath("horizontal-pager"),
      direction: .horizontal
    )
  }
}
