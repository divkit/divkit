import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic

public enum TooltipFactory {
  public static func makeTooltip(
    text: String,
    position: BlockTooltip.Position,
    width: CGFloat? = nil,
    theme: Theme = .light
  ) -> Block {
    let sideInset: CGFloat = 12
    let text = TextBlock(
      widthTrait: width.flatMap { .fixed($0 - 2 * sideInset) } ?? .intrinsic,
      text: text.with(typo: textTypo.with(color: theme.textColor))
    )
    .addingEdgeInsets(EdgeInsets(top: 8, left: sideInset, bottom: 8, right: sideInset))
    .addingDecorations(boundary: .clipCorner(radius: 10), backgroundColor: theme.backgroundColor)
    return position.makeContainer(child: text, theme: theme)
  }
}

extension BlockTooltip.Position {
  private func makeTail(color: Color) -> Block {
    let imageHolder: ImageHolder?
    switch self {
    case .left:
      imageHolder = Image.imageOfSize(
        middleTailSize,
        drawingHandler: { drawMiddleTail(style: .right, color: color, context: $0) }
      )
    case .topLeft:
      imageHolder = Image.imageOfSize(
        cornerTailSize,
        drawingHandler: { drawCornerTail(style: .bottomRight, color: color, context: $0) }
      )
    case .top:
      imageHolder = Image.imageOfSize(
        middleTailSize.flipDimensions(),
        drawingHandler: { drawMiddleTail(style: .bottom, color: color, context: $0) }
      )
    case .topRight:
      imageHolder = Image.imageOfSize(
        cornerTailSize,
        drawingHandler: { drawCornerTail(style: .bottomLeft, color: color, context: $0) }
      )
    case .right:
      imageHolder = Image.imageOfSize(
        middleTailSize,
        drawingHandler: { drawMiddleTail(style: .left, color: color, context: $0) }
      )
    case .bottomRight:
      imageHolder = Image.imageOfSize(
        cornerTailSize,
        drawingHandler: { drawCornerTail(style: .topLeft, color: color, context: $0) }
      )
    case .bottom:
      imageHolder = Image.imageOfSize(
        middleTailSize.flipDimensions(),
        drawingHandler: { drawMiddleTail(style: .top, color: color, context: $0) }
      )
    case .bottomLeft:
      imageHolder = Image.imageOfSize(
        cornerTailSize,
        drawingHandler: { drawCornerTail(style: .topRight, color: color, context: $0) }
      )
    }
    return ImageBlock(imageHolder: imageHolder!)
  }

  fileprivate func makeContainer(child: Block, theme: Theme) -> Block {
    switch self {
    case .left:
      return try! ContainerBlock(
        layoutDirection: .horizontal,
        verticalChildrenAlignment: .center,
        gaps: [0, -1, 0],
        children: [child, makeTail(color: theme.backgroundColor)]
      )
    case .topLeft:
      return LayeredBlock(
        horizontalChildrenAlignment: .trailing,
        verticalChildrenAlignment: .trailing,
        children: [
          makeTail(color: theme.backgroundColor),
          child.addingEdgeInsets(EdgeInsets(top: 0, left: 0, bottom: 5, right: 1)),
        ]
      )
    case .top:
      return try! ContainerBlock(
        layoutDirection: .vertical,
        horizontalChildrenAlignment: .center,
        gaps: [0, -1, 0],
        children: [child, makeTail(color: theme.backgroundColor)]
      )
    case .topRight:
      return LayeredBlock(
        verticalChildrenAlignment: .trailing,
        children: [
          makeTail(color: theme.backgroundColor),
          child.addingEdgeInsets(EdgeInsets(top: 0, left: 1, bottom: 5, right: 0)),
        ]
      )
    case .right:
      return try! ContainerBlock(
        layoutDirection: .horizontal,
        verticalChildrenAlignment: .center,
        gaps: [0, -1, 0],
        children: [makeTail(color: theme.backgroundColor), child]
      )
    case .bottomRight:
      return LayeredBlock(children: [
        child.addingEdgeInsets(EdgeInsets(top: 5, left: 1, bottom: 0, right: 0)),
        makeTail(color: theme.backgroundColor),
      ])
    case .bottom:
      return try! ContainerBlock(
        layoutDirection: .vertical,
        horizontalChildrenAlignment: .center,
        gaps: [0, -1, 0],
        children: [makeTail(color: theme.backgroundColor), child]
      )
    case .bottomLeft:
      return LayeredBlock(
        horizontalChildrenAlignment: .trailing,
        children: [
          child.addingEdgeInsets(EdgeInsets(top: 5, left: 0, bottom: 0, right: 1)),
          makeTail(color: theme.backgroundColor),
        ]
      )
    }
  }
}

extension Theme {
  fileprivate var textColor: Color {
    switch self {
    case .light:
      return .colorWithHexCode(0xFF_FF_FF_FC)
    case .dark:
      return .black
    }
  }

  fileprivate var backgroundColor: Color {
    switch self {
    case .light:
      return .colorWithHexCode(0x2E_2F_34_FF)
    case .dark:
      return .white
    }
  }
}

private let textTypo = Typo(size: .textS, weight: .regular)
  .with(height: .textS)
private let cornerTailSize = CGSize(width: 13, height: 12)
private let middleTailSize = CGSize(width: 8, height: 16)

private enum CornerTailStyle {
  case topLeft
  case topRight
  case bottomRight
  case bottomLeft
}

private func drawCornerTail(
  style: CornerTailStyle,
  color: Color,
  context: CGContext
) {
  let transform: CGAffineTransform
  switch style {
  case .topLeft:
    transform = .identity
      .translatedBy(x: 0, y: cornerTailSize.height)
      .scaledBy(x: 1, y: -1)
  case .topRight:
    transform = .identity
      .translatedBy(x: cornerTailSize.width, y: cornerTailSize.height)
      .scaled(by: -1)
  case .bottomRight:
    transform = .identity
      .translatedBy(x: cornerTailSize.width, y: 0)
      .scaledBy(x: -1, y: 1)
  case .bottomLeft:
    transform = .identity
  }

  context.saveGState()
  context.concatenate(transform)
  context.setFillColor(color.cgColor)
  context.move(to: CGPoint(x: 0, y: 12))
  context.addCurve(
    to: CGPoint(x: 0, y: 12),
    control1: CGPoint(x: 0, y: 12),
    control2: CGPoint(x: 0, y: 12)
  )
  context.addCurve(
    to: CGPoint(x: 5.0, y: 0.0),
    control1: CGPoint(x: 4.5, y: 8.5),
    control2: CGPoint(x: 6, y: 5.5)
  )
  context.addLine(to: CGPoint(x: 13, y: 0))
  context.addLine(to: CGPoint(x: 13, y: 5.5))
  context.addCurve(
    to: CGPoint(x: 0, y: 12),
    control1: CGPoint(x: 12, y: 9),
    control2: CGPoint(x: 4.5, y: 12)
  )
  context.closePath()
  context.drawPath(using: .fill)
  context.restoreGState()
}

private enum MiddleTailStyle {
  case left
  case top
  case right
  case bottom
}

private func drawMiddleTail(
  style: MiddleTailStyle,
  color: Color,
  context: CGContext
) {
  let transform: CGAffineTransform
  switch style {
  case .left:
    transform = .identity
      .translatedBy(x: 0, y: middleTailSize.height)
      .scaledBy(x: 1, y: -1)
  case .top:
    transform = .identity
      .rotated(by: 3 * .pi / 2)
      .scaledBy(x: -1, y: 1)
  case .right:
    transform = .identity
      .translatedBy(x: middleTailSize.width, y: 0)
      .scaledBy(x: -1, y: 1)
  case .bottom:
    transform = .identity
      .translatedBy(x: middleTailSize.height, y: middleTailSize.width)
      .scaledBy(x: -1, y: 1)
      .rotated(by: 3 * .pi / 2)
  }

  context.saveGState()
  context.concatenate(transform)
  context.setFillColor(color.cgColor)
  context.move(to: CGPoint(x: 0, y: 8))
  context.addCurve(
    to: CGPoint(x: 8, y: 0),
    control1: CGPoint(x: 4.66249, y: 6.28954),
    control2: CGPoint(x: 6.73185, y: 3.88031)
  )
  context.addLine(to: CGPoint(x: 8, y: 16))
  context.addCurve(
    to: CGPoint(x: 0, y: 8),
    control1: CGPoint(x: 6.73185, y: 12.1201),
    control2: CGPoint(x: 4.66249, y: 9.71086)
  )
  context.closePath()
  context.drawPath(using: .fill)
  context.restoreGState()
}
