#if os(iOS)
import Foundation
import UIKit
import VGSL

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
    let on: Binding<Bool>
    let enabled: Bool
    let action: UserInterfaceAction?
    let onTintColor: Color?
    let accessibility: AccessibilityElement?
  }

  var model: Model! {
    didSet {
      aSwitch.isOn = model.on.value
      aSwitch.isEnabled = model.enabled
      aSwitch.onTintColor = model.onTintColor?.systemColor
      applyAccessibilityFromScratch(model.accessibility)
    }
  }

  private var aSwitch: UISwitch

  var effectiveBackgroundColor: UIColor? { backgroundColor }

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

  override func layoutSubviews() {
    super.layoutSubviews()

    aSwitch.center = bounds.center
  }

  @objc func handleTap() {
    if let action = model.action {
      action.perform(sendingFrom: self)
    }
    model.on.value = aSwitch.isOn
  }
}
#endif
