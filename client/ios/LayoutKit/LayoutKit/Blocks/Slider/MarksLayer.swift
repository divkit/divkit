#if os(iOS)
import UIKit

final class MarksLayer: CALayer {
  var firstThumbProgress: CGFloat = 0
  var secondThumbProgress: CGFloat = 0
  var configuration = MarksConfiguration.empty

  private var maxValue: CGFloat {
    configuration.modelConfiguration.maxValue
  }

  private var minValue: CGFloat {
    configuration.modelConfiguration.minValue
  }

  private var activeMark: MarksConfigurationModel.RoundedRectangle {
    configuration.modelConfiguration.activeMark
  }

  private var inactiveMark: MarksConfigurationModel.RoundedRectangle {
    configuration.modelConfiguration.inactiveMark
  }

  private var marksStep: CGFloat {
    configuration.marksStep
  }

  override init() {
    super.init()
    setProperties()
  }

  override init(layer: Any) {
    super.init(layer: layer)
    guard let markLayer = layer as? MarksLayer else {
      assertionFailure("unknown layer inside init(layer: Any)")
      return
    }
    configuration = markLayer.configuration
    setProperties()
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func draw(in ctx: CGContext) {
    configureMarks(in: ctx)

    if configuration.modelConfiguration.layoutDirection == .rightToLeft {
      self.transform = CATransform3DMakeScale(-1, 1, 1)
    } else {
      self.transform = CATransform3DMakeScale(1, 1, 1)
    }
  }

  private func setProperties() {
    needsDisplayOnBoundsChange = true
  }

  private func configureMarks(in ctx: CGContext) {
    let leftThumb = min(firstThumbProgress, secondThumbProgress).ceiled()
    let rightThumb = max(firstThumbProgress, secondThumbProgress).floored()
    if secondThumbProgress == minValue {
      makeMarks(
        from: 0,
        to: rightThumb - minValue,
        style: .active,
        in: ctx
      )
    } else {
      makeMarks(
        from: 0,
        to: leftThumb - marksStep - minValue,
        style: .inactive,
        in: ctx
      )
      makeMarks(
        from: leftThumb - minValue,
        to: rightThumb - minValue,
        style: .active,
        in: ctx
      )
    }
    makeMarks(
      from: rightThumb + marksStep - minValue,
      to: maxValue - minValue,
      style: .inactive,
      in: ctx
    )
  }

  private func makeMarks(
    from startIndex: CGFloat,
    to endIndex: CGFloat,
    style: MarkStyle,
    in ctx: CGContext
  ) {
    let range = maxValue - minValue
    let maxWidth = bounds.width - configuration.horizontalInset
    let spaceWidth = maxWidth / range * marksStep
    let markConfiguration = markConfiguration(for: style)

    guard markConfiguration != .empty,
          startIndex <= endIndex,
          spaceWidth >= 0 else {
      return
    }

    let startIndex = startIndex / marksStep
    let endIndex = endIndex / marksStep

    let markHeight = markConfiguration.size.height
    let xOrigin = spaceWidth * startIndex
      .floored() + (configuration.horizontalInset - markConfiguration.size.width) / 2
    let yOrigin = bounds.midY - markHeight / 2
    var origin = CGPoint(x: xOrigin, y: yOrigin)

    let steps = Int(ceil(endIndex - startIndex))

    for _ in 0...steps {
      markConfiguration.render(in: ctx, with: origin)
      origin.x = min(
        origin.x + spaceWidth,
        maxWidth + (configuration.horizontalInset - markConfiguration.size.width) / 2
      )
    }
  }

  private func markConfiguration(
    for style: MarkStyle
  ) -> MarksConfigurationModel.RoundedRectangle {
    switch style {
    case .active:
      activeMark
    case .inactive:
      inactiveMark
    }
  }
}

extension MarksConfigurationModel.RoundedRectangle {
  fileprivate func render(in ctx: CGContext, with origin: CGPoint = CGPoint(x: 0, y: 0)) {
    let rect = CGRect(origin: origin, size: size).insetBy(dx: borderWidth / 4, dy: borderWidth / 4)
    let radius = min(cornerRadius, rect.height / 2, rect.width / 2)
    let path = CGPath(
      roundedRect: rect,
      cornerWidth: radius,
      cornerHeight: radius,
      transform: nil
    )
    ctx.addPath(path)
    ctx.setLineWidth(borderWidth / 2)
    ctx.setStrokeColor(borderColor.cgColor)
    ctx.setFillColor(color.cgColor)
    ctx.closePath()
    ctx.drawPath(using: .fillStroke)
  }
}

private enum MarkStyle {
  case active
  case inactive
}
#endif
