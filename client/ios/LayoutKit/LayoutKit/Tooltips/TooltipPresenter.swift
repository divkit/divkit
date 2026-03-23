#if os(iOS)
import UIKit
import VGSL

protocol TooltipPresenter: AnyObject {
  /// Prepares for presentation and returns the layout constraint rect and the coordinate space
  /// view to use for anchor-to-container coordinate conversion. Returns nil if presentation is
  /// not possible (e.g. no active window scene).
  func prepare() -> (constraint: CGRect, coordinateSpace: UIView?)?

  /// Installs the container view into the appropriate parent and sets its frame.
  func present(_ view: TooltipContainerView, for tooltip: DefaultTooltipManager.Tooltip)

  /// Used only for tests
  func currentTooltipView() async -> UIView?

  /// Called after a tooltip is removed from `showingTooltips`. `hasRemainingModals` is true if
  /// there are
  /// still-visible modal tooltips after the removal.
  func onClosed(tooltipID: String, hasRemainingModals: Bool)

  /// Tears down any presenter-owned state (windows, view controllers, etc.).
  func reset()
}
#endif
