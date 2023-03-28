import CoreGraphics
import Foundation

import CommonCorePublic
import LayoutKitInterface

public struct GalleryViewModel: Equatable {
  public enum ScrollMode: Equatable {
    case `default`
    case autoPaging
    case fixedPaging(pageSize: CGFloat)
  }

  @frozen
  public enum Direction: Equatable {
    case horizontal
    case vertical

    public var isHorizontal: Bool {
      switch self {
      case .horizontal:
        return true
      case .vertical:
        return false
      }
    }
  }

  public struct Item: Equatable {
    public let crossAlignment: Alignment
    public var content: Block

    public init(crossAlignment: Alignment, content: Block) {
      self.crossAlignment = crossAlignment
      self.content = content
    }
  }

  public var items: [Item]
  public let metrics: GalleryViewMetrics
  public let scrollMode: ScrollMode
  public let path: UIElementPath
  public let direction: Direction
  public let columnCount: Int
  public let areEmptySpaceTouchesEnabled: Bool
  public let alwaysBounceVertical: Bool
  public let bounces: Bool

  public init(
    blocks: [Block],
    metrics: GalleryViewMetrics,
    scrollMode: ScrollMode = .default,
    path: UIElementPath,
    direction: Direction = .horizontal,
    columnCount: Int = 1,
    crossAlignment: Alignment = .leading,
    areEmptySpaceTouchesEnabled: Bool = true,
    alwaysBounceVertical: Bool = false,
    bounces: Bool = true
  ) {
    self.init(
      items: blocks.map { Item(crossAlignment: crossAlignment, content: $0) },
      metrics: metrics,
      scrollMode: scrollMode,
      path: path,
      direction: direction,
      columnCount: columnCount,
      areEmptySpaceTouchesEnabled: areEmptySpaceTouchesEnabled,
      alwaysBounceVertical: alwaysBounceVertical,
      bounces: bounces
    )
  }

  public init(
    items: [Item],
    metrics: GalleryViewMetrics,
    scrollMode: ScrollMode = .default,
    path: UIElementPath,
    direction: Direction = .horizontal,
    columnCount: Int = 1,
    areEmptySpaceTouchesEnabled: Bool = true,
    alwaysBounceVertical: Bool = false,
    bounces: Bool = true
  ) {
    validateContent(of: items, for: metrics, with: direction)

    precondition(columnCount > 0)

    self.items = items
    self.metrics = metrics
    self.scrollMode = scrollMode
    self.path = path
    self.direction = direction
    self.columnCount = columnCount
    self.areEmptySpaceTouchesEnabled = areEmptySpaceTouchesEnabled
    self.alwaysBounceVertical = alwaysBounceVertical
    self.bounces = bounces
  }

  public func modifying(
    items: [Item]? = nil,
    metrics: GalleryViewMetrics? = nil,
    scrollMode: ScrollMode? = nil,
    path: UIElementPath? = nil,
    direction: Direction? = nil,
    areEmptySpaceTouchesEnabled: Bool? = nil
  ) -> GalleryViewModel {
    GalleryViewModel(
      items: items ?? self.items,
      metrics: metrics ?? self.metrics,
      scrollMode: scrollMode ?? self.scrollMode,
      path: path ?? self.path,
      direction: direction ?? self.direction,
      areEmptySpaceTouchesEnabled: areEmptySpaceTouchesEnabled ?? self.areEmptySpaceTouchesEnabled
    )
  }
}

extension GalleryViewModel.Item {
  public static func ==(lhs: GalleryViewModel.Item, rhs: GalleryViewModel.Item) -> Bool {
    lhs.crossAlignment == rhs.crossAlignment && lhs.content == rhs.content
  }
}

private func validateContent(
  of items: [GalleryViewModel.Item],
  for metrics: GalleryViewMetrics,
  with direction: GalleryViewModel.Direction
) {
  precondition(!items.isEmpty)

  let blocks = items.map { $0.content }
  switch direction {
  case .horizontal:
    precondition(
      blocks.allSatisfy {
        !$0.isHorizontallyResizable || $0.weightOfHorizontallyResizableBlock == .default
      }
    )
  case .vertical:
    precondition(
      blocks.allSatisfy {
        !$0.isVerticallyResizable || $0.weightOfVerticallyResizableBlock == .default
      }
    )
  }

  precondition(items.count - 1 == metrics.spacings.count)
}
