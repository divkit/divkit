#if os(iOS)
import UIKit
import VGSL

final class PageIndicatorView: UIView {
  override class var layerClass: AnyClass { ScrollPageIndicatorLayer.self }

  var currentIndexPosition: CGFloat {
    get {
      contentLayer.currentIndexPosition
    }
    set {
      contentLayer.currentIndexPosition = newValue
      setNeedsDisplay()
    }
  }

  var numberOfPages: Int {
    get {
      contentLayer.numberOfPages
    }
    set {
      contentLayer.numberOfPages = newValue
      setNeedsDisplay()
    }
  }

  var configuration: PageIndicatorConfiguration {
    get {
      contentLayer.configuration
    }
    set {
      contentLayer.configuration = newValue
      setNeedsDisplay()
    }
  }

  private var contentLayer: ScrollPageIndicatorLayer {
    layer as! ScrollPageIndicatorLayer
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
