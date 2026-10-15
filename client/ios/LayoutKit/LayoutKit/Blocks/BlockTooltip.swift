import CoreGraphics
import Foundation
import VGSL

#if os(iOS)
public typealias TooltipViewFactory = () async -> VisibleBoundsTrackingView
#else
public typealias TooltipViewFactory = () async -> ViewType?
#endif

public struct BlockTooltip: Equatable {
  public enum Position: String, CaseIterable {
    case left
    case topLeft
    case top
    case topRight
    case right
    case bottomRight
    case bottom
    case bottomLeft
    case center
  }

  public enum Mode: Equatable {
    case modal
    case nonModal
  }

  public enum ViewSource: Equatable {
    case block(Block)
    case factory(key: any Equatable, TooltipViewFactory)

    public static func ==(
      lhs: BlockTooltip.ViewSource,
      rhs: BlockTooltip.ViewSource
    ) -> Bool {
      switch (lhs, rhs) {
      case let (.block(lhsBlock), .block(rhsBlock)):
        lhsBlock.equals(rhsBlock)
      case let (.factory(lhsKey, _), .factory(rhsKey, _)):
        lhsKey.isEqual(to: rhsKey)
      default:
        false
      }
    }
  }

  public let params: BlockTooltipParams
  public let viewSource: ViewSource
  public let offset: CGPoint
  public let position: Position
  public let useLegacyWidth: Bool
  public let bringToTopId: String?
  public let substrateViewFactory: TooltipViewFactory?

  public var id: String {
    params.id
  }

  public init(
    viewSource: ViewSource,
    params: BlockTooltipParams,
    offset: CGPoint,
    position: BlockTooltip.Position,
    useLegacyWidth: Bool = true,
    bringToTopId: String? = nil,
    substrateViewFactory: TooltipViewFactory? = nil
  ) {
    self.viewSource = viewSource
    self.offset = offset
    self.position = position
    self.useLegacyWidth = useLegacyWidth
    self.bringToTopId = bringToTopId
    self.substrateViewFactory = substrateViewFactory
    self.params = params
  }

  public static func ==(lhs: BlockTooltip, rhs: BlockTooltip) -> Bool {
    lhs.params == rhs.params &&
      lhs.offset == rhs.offset &&
      lhs.position == rhs.position &&
      lhs.useLegacyWidth == rhs.useLegacyWidth &&
      lhs.bringToTopId == rhs.bringToTopId &&
      lhs.viewSource == rhs.viewSource
  }
}

extension Equatable {
  fileprivate func isEqual(to other: any Equatable) -> Bool {
    type(of: other) == Self.self && (other as? Self) == self
  }
}

/// Supplies the size of a tooltip view that was created by a `TooltipViewFactory`, since the
/// tooltip model carries no block for it. Returns nil while the size is not known yet. A tooltip
/// whose content size is empty is not shown unless it has a substrate.
public protocol TooltipContentSizeProviding {
  func tooltipContentSize(
    constrainedBy size: CGSize,
    useLegacyWidth: Bool
  ) -> CGSize?
}

/// Notifies content created by a tooltip view factory that it has been closed or discarded
/// without being shown.
public protocol TooltipContentClosing {
  func tooltipDidClose()
}
