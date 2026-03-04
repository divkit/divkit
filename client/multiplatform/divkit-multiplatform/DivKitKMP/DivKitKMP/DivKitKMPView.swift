#if os(iOS)
import UIKit

import DivKit

final class DivKitKMPView: UIView {
  override var intrinsicContentSize: CGSize {
    divView.intrinsicContentSize
  }

  override func sizeThatFits(_ size: CGSize) -> CGSize {
    divView.sizeThatFits(size)
  }

  private let divView: DivView

  init(divView: DivView) {
    self.divView = divView
    super.init(frame: .zero)

    addSubview(divView)
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    divView.frame = bounds
    divView.onVisibleBoundsChanged(to: bounds)
  }
}
#endif
