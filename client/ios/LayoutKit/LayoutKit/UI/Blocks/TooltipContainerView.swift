import BaseTinyPublic
import CommonCorePublic
import Foundation
import UIKit

public final class TooltipContainerView: UIView, UIActionEventPerforming {
  private let tooltipView: VisibleBoundsTrackingView
  private let tooltipID: String
  private let handleAction: (LayoutKit.UIActionEvent) -> Void
  private let onCloseAction: Action

  private var lastNonZeroBounds: CGRect?
  private var onVisibleBoundsChanged: Action?

  public init(
    tooltipView: VisibleBoundsTrackingView,
    tooltipID: String,
    handleAction: @escaping (LayoutKit.UIActionEvent) -> Void,
    onCloseAction: @escaping Action
  ) {
    self.tooltipView = tooltipView
    self.tooltipID = tooltipID
    self.handleAction = handleAction
    self.onCloseAction = onCloseAction
    let tooltipBounds = tooltipView.bounds
    onVisibleBoundsChanged = { [weak tooltipView] in
      tooltipView?.onVisibleBoundsChanged(from: .zero, to: tooltipBounds)
    }
    super.init(frame: .zero)
    addSubview(tooltipView)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError()
  }

  public override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
    if let touch = touches.first {
      let point = touch.location(in: self)
      if !tooltipView.point(inside: tooltipView.convert(point, from: self), with: event) {
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
}
