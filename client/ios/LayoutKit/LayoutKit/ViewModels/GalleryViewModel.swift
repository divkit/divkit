import CoreGraphics
import Foundation
import VGSL

#if canImport(UIKit)
import UIKit
#else
import AppKit
#endif

public struct GalleryViewModel: Equatable {
  public enum ScrollMode: Equatable {
    case `default`
    case autoPaging(inertionEnabled: Bool)
    case fixedPaging(pageSize: CGFloat)
  }

  public struct Item: Equatable {
    public let crossAlignment: Alignment
    public var content: Block

    public init(crossAlignment: Alignment, content: Block) {
      self.crossAlignment = crossAlignment
      self.content = content
    }
  }

  public enum Scrollbar: Equatable {
    case none
    case auto

    public var show: Bool {
      switch self {
      case .none:
        false
      case .auto:
        true
      }
    }
  }

  public var items: [Item]
  public let layoutDirection: UserInterfaceLayoutDirection
  public let metrics: GalleryViewMetrics
  public let scrollMode: ScrollMode
  public let path: UIElementPath
  public let alignment: Alignment
  public let direction: ScrollDirection
  public let bufferSize: Int
  public let columnCount: Int
  public let areEmptySpaceTouchesEnabled: Bool
  public let alwaysBounceVertical: Bool
  public let bounces: Bool
  public let infiniteScroll: Bool
  public let scrollbar: Scrollbar
  public let transformation: ElementsTransformation?

  public init(
    blocks: [Block],
    layoutDirection: UserInterfaceLayoutDirection = .leftToRight,
    metrics: GalleryViewMetrics,
    scrollMode: ScrollMode = .default,
    path: UIElementPath,
    alignment: Alignment = .center,
    direction: ScrollDirection = .horizontal,
    bufferSize: Int = 0,
    columnCount: Int = 1,
    crossAlignment: Alignment = .leading,
    areEmptySpaceTouchesEnabled: Bool = true,
    alwaysBounceVertical: Bool = false,
    bounces: Bool = true,
    infiniteScroll: Bool = false,
    scrollbar: Scrollbar = .none,
    transformation: ElementsTransformation? = nil
  ) {
    self.init(
      items: blocks.map { Item(crossAlignment: crossAlignment, content: $0) },
      layoutDirection: layoutDirection,
      metrics: metrics,
      scrollMode: scrollMode,
      path: path,
      alignment: alignment,
      direction: direction,
      bufferSize: bufferSize,
      columnCount: columnCount,
      areEmptySpaceTouchesEnabled: areEmptySpaceTouchesEnabled,
      alwaysBounceVertical: alwaysBounceVertical,
      bounces: bounces,
      infiniteScroll: infiniteScroll,
      scrollbar: scrollbar,
      transformation: transformation
    )
  }

  public init(
    items: [Item],
    layoutDirection: UserInterfaceLayoutDirection = .leftToRight,
    metrics: GalleryViewMetrics,
    scrollMode: ScrollMode = .default,
    path: UIElementPath,
    alignment: Alignment = .center,
    direction: ScrollDirection = .horizontal,
    bufferSize: Int = 0,
    columnCount: Int = 1,
    areEmptySpaceTouchesEnabled: Bool = true,
    alwaysBounceVertical: Bool = false,
    bounces: Bool = true,
    infiniteScroll: Bool = false,
    scrollbar: Scrollbar = .none,
    transformation: ElementsTransformation? = nil
  ) {
    validateContent(of: items, with: direction)

    precondition(columnCount > 0)

    self.items = items
    self.layoutDirection = layoutDirection
    self.metrics = metrics
    self.scrollMode = scrollMode
    self.path = path
    self.alignment = alignment
    self.direction = direction
    self.bufferSize = bufferSize
    self.columnCount = columnCount
    self.areEmptySpaceTouchesEnabled = areEmptySpaceTouchesEnabled
    self.alwaysBounceVertical = alwaysBounceVertical
    self.bounces = bounces
    self.infiniteScroll = infiniteScroll
    self.scrollbar = scrollbar
    self.transformation = transformation
  }

  public func modifying(
    items: [Item]? = nil,
    metrics: GalleryViewMetrics? = nil,
    scrollMode: ScrollMode? = nil,
    path: UIElementPath? = nil,
    bufferSize: Int? = nil,
    direction: ScrollDirection? = nil,
    areEmptySpaceTouchesEnabled: Bool? = nil
  ) -> GalleryViewModel {
    GalleryViewModel(
      items: items ?? self.items,
      metrics: metrics ?? self.metrics,
      scrollMode: scrollMode ?? self.scrollMode,
      path: path ?? self.path,
      direction: direction ?? self.direction,
      bufferSize: bufferSize ?? self.bufferSize,
      areEmptySpaceTouchesEnabled: areEmptySpaceTouchesEnabled ?? self.areEmptySpaceTouchesEnabled
    )
  }

  var itemsCountWithoutInfinite: Int {
    items.count - infiniteCorrection * 2
  }

  var infiniteCorrection: Int {
    infiniteScroll && items.count > 0 ? bufferSize : 0
  }
}

extension GalleryViewModel.Item {
  public static func ==(lhs: GalleryViewModel.Item, rhs: GalleryViewModel.Item) -> Bool {
    lhs.crossAlignment == rhs.crossAlignment && lhs.content == rhs.content
  }
}

private func validateContent(
  of items: [GalleryViewModel.Item],
  with direction: ScrollDirection
) {
  let blocks = items.map(\.content)
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
}
