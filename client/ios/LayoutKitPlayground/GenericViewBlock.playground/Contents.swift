import LayoutKit
import LayoutKitPlayground
import PlaygroundSupport
import UIKit
import VGSL

private final class SampleView: UIView {
  private let gradientLayer: CALayer = {
    let gradientLayer = CAGradientLayer()
    gradientLayer.colors = [UIColor.red.cgColor, UIColor.blue.cgColor]
    gradientLayer.startPoint = CGPoint(x: 0, y: 0)
    gradientLayer.endPoint = CGPoint(x: 1, y: 1)
    return gradientLayer
  }()

  private let label: UILabel = {
    let label = UILabel()
    label.textColor = .white
    return label
  }()

  init(text: String) {
    super.init(frame: .zero)
    setupView(text: text)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    label.center = bounds.center
    gradientLayer.frame = bounds
  }

  private func setupView(text: String = "") {
    label.text = text
    label.sizeToFit()
    self.layer.insertSublayer(gradientLayer, at: 0)
    self.addSubview(label)
  }

}

private func createBlock() -> Block {
  GenericViewBlock(
    content: .view(SampleView(text: "Just another text.")),
    width: .resizable,
    height: .fixed(200)
  )
}

PlaygroundPage.current.liveView = LayoutKitPlaygroundViewController(blockProvider: createBlock)
