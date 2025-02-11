import Foundation
import UIKit
import VGSL

public final class TooltipContainerView: UIView, UIActionEventPerforming {
  let isModal: Bool
  private let tooltipView: VisibleBoundsTrackingView
  private let closeByTapOutside: Bool
  private let tapOutsideActions: [UserInterfaceAction]
  private let handleAction: (LayoutKit.UIActionEvent) -> Void
  private let onCloseAction: Action

  private var isClosing = false
  private var lastNonZeroBounds: CGRect?
  private var onVisibleBoundsChanged: Action?

  public init(
    tooltipView: VisibleBoundsTrackingView,
    closeByTapOutside: Bool,
    tapOutsideActions: [UserInterfaceAction],
    isModal: Bool,
    handleAction: @escaping (LayoutKit.UIActionEvent) -> Void,
    onCloseAction: @escaping Action
  ) {
    self.tooltipView = tooltipView
    self.closeByTapOutside = closeByTapOutside
    self.tapOutsideActions = tapOutsideActions
    self.isModal = isModal
    self.handleAction = handleAction
    self.onCloseAction = onCloseAction
    let tooltipBounds = tooltipView.bounds
    onVisibleBoundsChanged = { [weak tooltipView] in
      tooltipView?.onVisibleBoundsChanged(from: .zero, to: tooltipBounds)
    }

    super.init(frame: .zero)

    if isModal {
      let tapRecognizer = UITapGestureRecognizer(target: self, action: #selector(handleTap))
      addGestureRecognizer(tapRecognizer)
    }

    addSubview(tooltipView)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError()
  }

  @objc private func handleTap(_ sender: UITapGestureRecognizer) {
    let point = sender.location(in: self)
    if !isPointInsideTooltip(point) {
      performTapOutsideActions()
    }
  }

  public override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    if !isPointInsideTooltip(point, event: event), !isModal {
      if closeByTapOutside {
        DispatchQueue.main.async {
          self.close()
        }
      }
      return nil
    } else {
      return super.hitTest(point, with: event)
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
    guard !isClosing else { return }
    isClosing = true
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

    if closeByTapOutside {
      close()
    }
  }

  private func isPointInsideTooltip(_ point: CGPoint, event: UIEvent? = nil) -> Bool {
    tooltipView.point(inside: tooltipView.convert(point, from: self), with: event)
  }
}
