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
        to: leftThumb - 1 - minValue,
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
      from: rightThumb + 1 - minValue,
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
    let spaceWidth = (bounds.width - configuration.horizontalInset) / (maxValue - minValue)
    guard startIndex <= endIndex, spaceWidth >= 0 else { return }

    let markHeight = style == .active ? activeMark.size.height : inactiveMark.size.height
    let xActiveOrigin = spaceWidth * startIndex
      .floored() + (configuration.horizontalInset - activeMark.size.width) / 2
    let xInactiveOrigin = spaceWidth * startIndex
      .floored() + (configuration.horizontalInset - inactiveMark.size.width) / 2
    let yOrigin = bounds.midY - markHeight / 2
    var activeOrigin = CGPoint(x: xActiveOrigin, y: yOrigin)
    var inactiveOrigin = CGPoint(x: xInactiveOrigin, y: yOrigin)
    for _ in Int(startIndex - minValue)...Int(endIndex - minValue) {
      switch style {
      case .active:
        activeMark.render(in: ctx, with: activeOrigin)
      case .inactive:
        inactiveMark.render(in: ctx, with: inactiveOrigin)
      }
      activeOrigin.x += spaceWidth
      inactiveOrigin.x += spaceWidth
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
