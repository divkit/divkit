import Foundation
import UIKit
import VGSL

public final class TooltipContainerView: UIView, UIActionEventPerforming {
  private let tooltipView: VisibleBoundsTrackingView
  private let closeByTapOutside: Bool
  private let tapOutsideActions: [UserInterfaceAction]
  private let handleAction: (LayoutKit.UIActionEvent) -> Void
  private let onCloseAction: Action

  private var lastNonZeroBounds: CGRect?
  private var onVisibleBoundsChanged: Action?

  public init(
    tooltipView: VisibleBoundsTrackingView,
    closeByTapOutside: Bool,
    tapOutsideActions: [UserInterfaceAction],
    handleAction: @escaping (LayoutKit.UIActionEvent) -> Void,
    onCloseAction: @escaping Action
  ) {
    self.tooltipView = tooltipView
    self.closeByTapOutside = closeByTapOutside
    self.tapOutsideActions = tapOutsideActions
    self.handleAction = handleAction
    self.onCloseAction = onCloseAction
    let tooltipBounds = tooltipView.bounds
    onVisibleBoundsChanged = { [weak tooltipView] in
      tooltipView?.onVisibleBoundsChanged(from: .zero, to: tooltipBounds)
    }

    super.init(frame: .zero)

    let tapRecognizer = UITapGestureRecognizer(target: self, action: #selector(handleTap))
    addGestureRecognizer(tapRecognizer)

    addSubview(tooltipView)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError()
  }

  @objc private func handleTap(_ sender: UITapGestureRecognizer) {
    let point = sender.location(in: self)
    let isPointInsideTooltip = tooltipView.point(
      inside: tooltipView.convert(point, from: self),
      with: nil
    )
    if !isPointInsideTooltip {
      performTapOutsideActions()

      if closeByTapOutside {
        close()
      }
    }
  }

  public override func layoutSubviews() {
    super.layoutSubviews()

    if let lastNonZeroBounds,
       lastNonZeroBounds != bounds {
      close()
    }

    if bounds != .zero {
      lastNonZeroBounds = bounds
    }

    onVisibleBoundsChanged?()
    onVisibleBoundsChanged = nil
  }

  public func perform(uiActionEvent event: LayoutKit.UIActionEvent, from _: AnyObject) {
    handleAction(event)
  }

  public func close() {
    self.tooltipView.onVisibleBoundsChanged(from: tooltipView.bounds, to: .zero)
    removeFromParentAnimated(completion: {
      self.onCloseAction()
    })
  }

  private func performTapOutsideActions() {
    let uiActionEvents = tapOutsideActions.map {
      UIActionEvent(uiAction: $0, originalSender: self)
    }
    perform(uiActionEvents: uiActionEvents, from: self)
  }
}
