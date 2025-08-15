#if os(iOS)
import UIKit
import VGSL

public protocol TooltipEventPerforming {
  func perform(tooltipEvent event: TooltipEvent, from sender: AnyObject)
}

public final class TooltipEvent: AppActionEventProtocol {
  public let tooltipView: VisibleBoundsTrackingView
  public private(set) weak var tooltipAnchorView: ViewType?
  public let info: TooltipInfo
  public let params: BlockTooltipParams

  public init(
    info: TooltipInfo,
    params: BlockTooltipParams,
    tooltipView: VisibleBoundsTrackingView,
    tooltipAnchorView: ViewType?
  ) {
    self.tooltipView = tooltipView
    self.tooltipAnchorView = tooltipAnchorView
    self.info = info
    self.params = params
  }

  public func makeHandler(responder: UIResponder) -> Handler? {
    (responder as? TooltipEventPerforming)?.perform(tooltipEvent:from:)
  }
}
#endif
