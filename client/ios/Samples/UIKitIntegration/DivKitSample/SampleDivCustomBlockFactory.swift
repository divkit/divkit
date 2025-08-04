import DivKit
import Foundation
import LayoutKit
import UIKit
import VGSL

struct SampleDivCustomBlockFactory: DivCustomBlockFactory {
  public func makeBlock(
    data: DivCustomData,
    context _: DivBlockModelingContext
  ) -> Block {
    switch data.name {
    case "sample_custom":
      let text = data.data["text"] as? String ?? ""
      return GenericViewBlock(
        content: .view(SampleView(text: text)),
        width: .resizable,
        height: .fixed(200)
      )
    default:
      return EmptyBlock()
    }
  }
}

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

  required init?(coder: NSCoder) {
    super.init(coder: coder)
    setupView()
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
