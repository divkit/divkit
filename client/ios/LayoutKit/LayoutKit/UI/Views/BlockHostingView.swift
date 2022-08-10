import UIKit

public final class BlockHostingView: UIView {
  private var contentView: BlockView!

  public var block: UIViewRenderable! {
    didSet {
      contentView = block.reuse(contentView, superview: self)
      forceLayout()
    }
  }

  public override func layoutSubviews() {
    super.layoutSubviews()
    contentView.frame = bounds
  }
}
