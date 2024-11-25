import DivKitExtensions
import UIKit

struct AccessoryViewProvider: InputAccessoryViewProvider {
  func getView(actions: [String: () -> Void]) -> UIView {
    InputAccessoryView(actions: actions)
  }
}

private final class InputAccessoryView: UIView {
  private let actions: [String: () -> Void]

  private lazy var toolbar: UIToolbar = {
    let toolbar = UIToolbar()
    toolbar.items = [
      UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil),
      UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(doneTapped)),
    ]
    return toolbar
  }()

  init(actions: [String: () -> Void]) {
    self.actions = actions
    super.init(frame: CGRect(origin: .zero, size: CGSize(width: 0, height: 44)))
    addSubview(toolbar)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    toolbar.frame = bounds
  }

  @objc private func doneTapped() {
    actions["dismiss_keyboard"]?()
  }
}
