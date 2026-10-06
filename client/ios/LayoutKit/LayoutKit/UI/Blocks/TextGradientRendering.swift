#if os(iOS)
import CoreText
import UIKit
import VGSL

final class GradientContainerView: UIView {
  let gradientView: UIView

  private var maskingView: UIView?

  private let model: TextBlock.GradientModel
  private let rangedTextWithColorTextBlockView: TextBlockView

  init?(model: TextBlock.GradientModel?, mask: UIView) {
    guard let model else {
      return nil
    }
    self.model = model
    self.maskingView = mask
    gradientView = model.gradient.uiView
    rangedTextWithColorTextBlockView = TextBlockView()
    super.init(frame: .zero)
    gradientView.addSubview(rangedTextWithColorTextBlockView)
    addSubview(gradientView)
  }

  @available(*, unavailable)
  required init(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    gradientView.frame = bounds
    if let maskingView {
      gradientView.mask = maskingView
      self.maskingView = nil
    }
    rangedTextWithColorTextBlockView.frame = bounds
  }

  func configureRangedTextColor(textBlockViewModel: TextBlockView.Model) {
    rangedTextWithColorTextBlockView.model = textBlockViewModel
      .updated(with: model.rangedTextWithColor)
  }
}

final class TextSelectionOverlayView: UIView {
  var selection: TextSelection? {
    didSet { setNeedsDisplay() }
  }

  override init(frame: CGRect) {
    super.init(frame: frame)
    backgroundColor = .clear
    isUserInteractionEnabled = false
    contentMode = .redraw
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func draw(_ rect: CGRect) {
    selection?.draw(rect)
  }
}

private struct PositionedGlyphRun {
  let run: CTRun
  let lineOrigin: CGPoint
}

private final class AnimatedTextGlyphRenderer: GlyphRenderer {
  private(set) var runs: [PositionedGlyphRun] = []

  func drawGlyphs(of run: CTRun, at lineOrigin: CGPoint, in _: CGContext) -> Bool {
    let attributes = CTRunGetAttributes(run) as NSDictionary
    guard attributes[NSAttributedString.Key.animatedTextGradient] as? Bool == true,
          attributes[NSAttributedString.Key.attachment] == nil else {
      return false
    }
    runs.append(PositionedGlyphRun(run: run, lineOrigin: lineOrigin))
    return true
  }
}

extension TextBlockView {
  func prepareAnimatedTextIfNeeded() {
    guard animatedTextImage == nil, let model,
          let gradientView = animatedGradientView,
          animatedGradientModel?.animation != nil,
          bounds.width > 0, bounds.height > 0 else {
      return
    }
    let format = UIGraphicsImageRendererFormat()
    format.scale = contentScaleFactor
    let renderer = UIGraphicsImageRenderer(size: bounds.size, format: format)
    let glyphRenderer = AnimatedTextGlyphRenderer()
    animatedTextImage = renderer.image { textImage in
      textLayout = drawText(
        in: textImage.cgContext,
        rect: bounds,
        glyphRenderer: glyphRenderer
      )
    }
    let image = renderer.image { maskImage in
      let maskContext = maskImage.cgContext
      maskContext.translateBy(x: 0, y: bounds.height)
      maskContext.scaleBy(x: 1, y: -1)
      maskContext.setFillColor(UIColor.white.cgColor)
      maskContext.setTextDrawingMode(.fill)
      for glyphRun in glyphRenderer.runs {
        glyphRun.run.drawGlyphs(at: glyphRun.lineOrigin, in: maskContext)
      }
    }
    gradientView.textInsets = model.additionalTextInsets
    gradientView.setGlyphMask(image)
  }
}

extension Gradient {
  fileprivate var uiView: UIView {
    switch self {
    case let .linear(gradient):
      LinearGradientView(gradient)
    case let .radial(gradient):
      RadialGradientView(gradient)
    case let .box(color):
      BoxShadowView(shadowColor: color)
    }
  }
}
#endif
