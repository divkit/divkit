#if os(iOS)
import UIKit
import VGSL

final class ViewTooltipPresenter: TooltipPresenter {
  private let containerView: UIView

  init(containerView: UIView) {
    self.containerView = containerView
  }

  func prepare() -> (constraint: CGRect, coordinateSpace: UIView?)? {
    (containerView.bounds, containerView)
  }

  func present(_ view: TooltipContainerView, for tooltip: DefaultTooltipManager.Tooltip) {
    containerView.addSubview(view)
    view.frame = containerView.bounds
    if tooltip.params.mode == .modal {
      view.accessibilityViewIsModal = true
    }
  }

  func currentTooltipView() async -> UIView? {
    await MainActor.run {
      containerView.subviews.first
    }
  }

  func onClosed(tooltipID _: String, hasRemainingModals _: Bool) {}

  func reset() {}
}
#endif
