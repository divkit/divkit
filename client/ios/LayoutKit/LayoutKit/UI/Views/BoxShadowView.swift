import UIKit

import CommonCorePublic

public final class BoxShadowView: UIView {
  public var shadowColor: Color {
    didSet { innerShadowLayer.shadowColor = shadowColor.cgColor }
  }

  private var innerShadowLayer: CALayer!

  public init(shadowColor: Color) {
    self.shadowColor = shadowColor
    super.init(frame: .zero)
    innerShadowLayer = makeInnerShadowLayer()
    layer.addSublayer(innerShadowLayer)
  }

  @available(*, unavailable)
  public required init(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  private func makeInnerShadowLayer() -> CALayer {
    let layer = CALayer()
    layer.masksToBounds = true
    layer.shadowColor = shadowColor.cgColor
    layer.shadowOffset = .zero
    layer.shadowOpacity = 1
    layer.shadowRadius = 30
    return layer
  }

  public override func layoutSubviews() {
    super.layoutSubviews()
    innerShadowLayer.frame = bounds
    let path = UIBezierPath(rect: innerShadowLayer.bounds.insetBy(dx: -30, dy: -30))
    let cutout = UIBezierPath(rect: innerShadowLayer.bounds).reversing()
    path.append(cutout)
    innerShadowLayer.shadowPath = path.cgPath
  }
}
