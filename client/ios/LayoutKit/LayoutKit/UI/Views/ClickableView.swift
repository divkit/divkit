import UIKit

import LayoutKitInterface

final class ClickableView: UIView {
  var action: UserInterfaceAction

  init(action: UserInterfaceAction) {
    self.action = action
    super.init(frame: .zero)
    let tapRecognizer = UITapGestureRecognizer(
      target: self,
      action: #selector(handleTap)
    )
    addGestureRecognizer(tapRecognizer)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  @objc func handleTap() {
    action.perform(sendingFrom: self)
  }
}
