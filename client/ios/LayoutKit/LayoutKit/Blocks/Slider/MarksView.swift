import UIKit

final class MarksView: UIView {
  var configuration: MarksConfiguration {
    get {
      contentLayer.configuration
    }
    set {
      contentLayer.configuration = newValue
      contentLayer.setNeedsDisplay()
    }
  }

  var firstThumbProgress: CGFloat {
    get {
      contentLayer.firstThumbProgress
    }
    set {
      contentLayer.firstThumbProgress = newValue
      contentLayer.setNeedsDisplay()
    }
  }

  var secondThumbProgress: CGFloat {
    get {
      contentLayer.secondThumbProgress
    }
    set {
      contentLayer.secondThumbProgress = newValue
      contentLayer.setNeedsDisplay()
    }
  }

  private var contentLayer: MarksLayer {
    layer as! MarksLayer
  }

  override class var layerClass: AnyClass { MarksLayer.self }

  init() {
    super.init(frame: .zero)
    commonInit()
  }

  override init(frame: CGRect) {
    super.init(frame: frame)
    commonInit()
  }

  required init?(coder: NSCoder) {
    super.init(coder: coder)
    commonInit()
  }

  private func commonInit() {
    backgroundColor = .clear
  }

  override func draw(_: CGRect) {
    // overriden draw is required for redrawing of layer
  }
}
