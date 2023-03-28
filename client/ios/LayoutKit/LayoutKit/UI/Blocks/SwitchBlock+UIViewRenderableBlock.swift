import Foundation
import UIKit

import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

extension SwitchBlock {
  public static func makeBlockView() -> BlockView { SwitchBlockView() }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    (view as! SwitchBlockView).model = SwitchBlockView.Model(
      on: on,
      enabled: enabled,
      action: action,
      onTintColor: onTintColor,
      accessibility: accessibilityElement
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is SwitchBlockView
  }
}

private final class SwitchBlockView: BlockView, VisibleBoundsTrackingLeaf {
  struct Model {
    let on: Bool
    let enabled: Bool
    let action: UserInterfaceAction?
    let onTintColor: Color?
    let accessibility: AccessibilityElement?
  }

  var model: Model! {
    didSet {
      aSwitch.isOn = model.on
      aSwitch.isEnabled = model.enabled
      aSwitch.onTintColor = model.onTintColor?.systemColor
      applyAccessibility(model.accessibility)
    }
  }

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  private var aSwitch: UISwitch

  init() {
    aSwitch = UISwitch()
    aSwitch.tintColor = UIColor(white: 0, alpha: 0.1)
    aSwitch.isExclusiveTouch = true

    super.init(frame: aSwitch.frame)

    addSubview(aSwitch)

    aSwitch.addTarget(
      self,
      action: #selector(handleTap),
      for: .valueChanged
    )
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  @objc func handleTap() {
    if let action = model.action {
      action.perform(sendingFrom: self)
    }
  }
}
