#if os(iOS)
import UIKit
import VGSL

final class ViewTooltipPresenter: TooltipPresenter {
  private let containerView: TooltipHostView

  init(containerView: TooltipHostView) {
    self.containerView = containerView
  }

  func prepare() -> (constraint: CGRect, coordinateSpace: UIView?)? {
    (containerView.tooltipContainerBounds, containerView.coordinateSpace)
  }

  func present(_ view: TooltipContainerView, for tooltip: DefaultTooltipManager.Tooltip) {
    containerView.addTooltipView(view)
    view.frame = containerView.tooltipContainerBounds
    if tooltip.params.mode == .modal {
      view.accessibilityViewIsModal = true
    }
  }

  func currentTooltipView() async -> UIView? {
    await MainActor.run {
      containerView.coordinateSpace.subviews.first
    }
  }

  func onClosed(tooltipID _: String, hasRemainingModals _: Bool) {}

  func reset() {}
}
#endif
