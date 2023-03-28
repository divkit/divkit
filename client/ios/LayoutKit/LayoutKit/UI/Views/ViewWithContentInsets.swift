import Foundation
import UIKit

import CommonCorePublic

final class ViewWithContentInsets: UIView {
  let innerView: BlockView
  var contentInsets: EdgeInsets

  init(innerView: BlockView, contentInsets: EdgeInsets) {
    self.innerView = innerView
    self.contentInsets = contentInsets
    super.init(frame: .zero)
    addSubview(innerView)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    innerView.frame = bounds.inset(by: contentInsets)
  }
}
