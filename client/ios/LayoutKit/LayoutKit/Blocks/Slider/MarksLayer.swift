import UIKit

final class MarksLayer: CALayer {
  var firstThumbProgress: CGFloat = 0
  var secondThumbProgress: CGFloat = 0
  var configuration = MarksConfiguration.empty

  override func draw(in ctx: CGContext) {
    configureMarks(in: ctx)

    if configuration.layoutDirection == .rightToLeft {
      self.transform = CATransform3DMakeScale(-1, 1, 1)
    } else {
      self.transform = CATransform3DMakeScale(1, 1, 1)
    }
  }

  private func configureMarks(in ctx: CGContext) {
    let leftThumb = min(firstThumbProgress, secondThumbProgress).ceiled()
    let rightThumb = max(firstThumbProgress, secondThumbProgress).floored()
    if secondThumbProgress == configuration.minValue {
      makeMarks(
        from: 0,
        to: rightThumb - configuration.minValue,
        style: .active,
        in: ctx
      )
    } else {
      makeMarks(
        from: 0,
        to: leftThumb - 1 - configuration.minValue,
        style: .inactive,
        in: ctx
      )
      makeMarks(
        from: leftThumb - configuration.minValue,
        to: rightThumb - configuration.minValue,
        style: .active,
        in: ctx
      )
    }
    makeMarks(
      from: rightThumb + 1 - configuration.minValue,
      to: configuration.maxValue - configuration.minValue,
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
    guard startIndex <= endIndex else { return }
    let spaceWidth = (bounds.width - configuration.horizontalInset) /
      (configuration.maxValue - configuration.minValue)
    let markHeight = style == .active ? configuration.activeMark.size.height : configuration
      .inactiveMark.size.height
    let xActiveOrigin = spaceWidth * startIndex
      .floored() + (configuration.horizontalInset - configuration.activeMark.size.width) / 2
    let xInactiveOrigin = spaceWidth * startIndex
      .floored() + (configuration.horizontalInset - configuration.inactiveMark.size.width) / 2
    let yOrigin = bounds.midY - markHeight / 2
    var activeOrigin = CGPoint(x: xActiveOrigin, y: yOrigin)
    var inactiveOrigin = CGPoint(x: xInactiveOrigin, y: yOrigin)
    for _ in Int(startIndex - configuration.minValue)...Int(endIndex - configuration.minValue) {
      switch style {
      case .active:
        configuration.activeMark.render(in: ctx, with: activeOrigin)
      case .inactive:
        configuration.inactiveMark.render(in: ctx, with: inactiveOrigin)
      }
      activeOrigin.x += spaceWidth
      inactiveOrigin.x += spaceWidth
    }
  }

  override init() {
    super.init()
  }

  override init(layer: Any) {
    super.init(layer: layer)
    guard let markLayer = layer as? MarksLayer else {
      assertionFailure("unknown layer inside init(layer: Any)")
      return
    }
    configuration = markLayer.configuration
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
}

extension MarksConfiguration.RoundedRectangle {
  fileprivate func render(in ctx: CGContext, with origin: CGPoint = CGPoint(x: 0, y: 0)) {
    let rect = CGRect(origin: origin, size: size).insetBy(dx: borderWidth / 4, dy: borderWidth / 4)
    let path = CGPath(
      roundedRect: rect,
      cornerWidth: min(cornerRadius, rect.height / 2),
      cornerHeight: min(cornerRadius, rect.height / 2),
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
