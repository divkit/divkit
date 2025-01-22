import UIKit
import VGSL

public protocol TooltipEventPerforming {
  func perform(tooltipEvent event: TooltipEvent, from sender: AnyObject)
}

public final class TooltipEvent: AppActionEventProtocol {
  public let tooltipID: String
  public let tooltipView: VisibleBoundsTrackingView
  public let duration: TimeInterval
  public let showsOnStart: Bool
  public let multiple: Bool

  public init(
    info: TooltipInfo,
    tooltipView: VisibleBoundsTrackingView,
    duration: TimeInterval
  ) {
    self.tooltipID = info.id
    self.tooltipView = tooltipView
    self.duration = duration
    self.showsOnStart = info.showsOnStart
    self.multiple = info.multiple
  }

  public func makeHandler(responder: UIResponder) -> Handler? {
    (responder as? TooltipEventPerforming)?.perform(tooltipEvent:from:)
  }
}
