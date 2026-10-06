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
    case factory(TooltipViewFactory)

    public static func ==(
      lhs: BlockTooltip.ViewSource,
      rhs: BlockTooltip.ViewSource
    ) -> Bool {
      switch (lhs, rhs) {
      case let (.block(lhsBlock), .block(rhsBlock)):
        lhsBlock.equals(rhsBlock)
      case (.factory, .factory):
        true
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

/// Holds the current content of one tooltip anchored to a `DecoratingBlock`, reused by identity
/// across remodels of the same anchor instead of being replaced. This lets `DecoratingBlock`
/// treat a tooltip's content refresh as invisible to its own reuse/diffing decisions (see
/// `DecoratingBlock.equals`), while whoever reads `tooltip` at display time always sees the
/// latest value. The value is read and written under a lock. Equality falls back to content
/// comparison only for trees built independently of the normal find-or-create modeling path
/// (e.g. tests); production remodeling always reuses the same holder for a given anchor, so
/// that fallback is never exercised there.
public final class TooltipContentHolder: Equatable {
  private let lock = AllocatedUnfairLock()
  private var _tooltip: BlockTooltip

  public var tooltip: BlockTooltip {
    lock.withLock { _tooltip }
  }

  public init(tooltip: BlockTooltip) {
    _tooltip = tooltip
  }

  public static func ==(lhs: TooltipContentHolder, rhs: TooltipContentHolder) -> Bool {
    if lhs === rhs {
      return true
    }
    return lhs.tooltip == rhs.tooltip
  }

  @_spi(Internal)
  public func update(_ tooltip: BlockTooltip) {
    lock.withLock { _tooltip = tooltip }
  }
}

/// Supplies the size of a tooltip view that was created by a `TooltipViewFactory`, since the
/// tooltip model carries no block for it. Returns nil while the size is not known yet.
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
