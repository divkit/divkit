#if os(iOS)
import UIKit

final class MarksView: UIView {
  override class var layerClass: AnyClass { MarksLayer.self }

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

  override func draw(_: CGRect) {
    // overriden draw is required for redrawing of layer
  }

  private func commonInit() {
    backgroundColor = .clear
  }

}
#endif
