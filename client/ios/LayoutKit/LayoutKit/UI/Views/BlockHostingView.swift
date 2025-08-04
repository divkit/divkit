#if os(iOS)
import UIKit

public final class BlockHostingView: UIView {
  public var block: UIViewRenderable! {
    didSet {
      contentView = block.reuse(contentView, superview: self)
      forceLayout()
    }
  }

  private var contentView: BlockView!

  public override func layoutSubviews() {
    super.layoutSubviews()
    contentView.frame = bounds
  }
}
#endif
