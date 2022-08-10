import Foundation
import UIKit

public final class CompositeView: UIView {
  public static let defaultBlendingCoefficient: Float = 1

  public let backView: BlockView
  public let frontView: BlockView
  public var blendingCoefficient: Float? {
    didSet {
      frontView.layer.opacity = blendingCoefficient ?? CompositeView.defaultBlendingCoefficient
    }
  }

  public init(backView: BlockView, frontView: BlockView) {
    self.backView = backView
    self.frontView = frontView
    super.init(frame: .zero)
    addSubview(backView)
    addSubview(frontView)
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func layoutSubviews() {
    super.layoutSubviews()
    backView.frame = bounds
    frontView.frame = bounds
  }
}
