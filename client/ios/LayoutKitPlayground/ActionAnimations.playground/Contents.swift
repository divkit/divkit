import UIKit
import PlaygroundSupport
import LayoutKit
import VGSL
import LayoutKitPlayground

private func textStyle(
  _ text: String
) -> TextBlock {
  return TextBlock(
    widthTrait: .intrinsic,
    text: text.with(typo: Typo(size: 14.0, weight: .medium).with(color: .blue)),
    verticalAlignment: .center,
    canSelect: true
  )
}

private func createAnimatedTextBlock(
  _ text: String,
  color: UIColor,
  cornerRadius: CGFloat,
  backgroundColor: UIColor,
  duration: TimeInterval
) throws -> Block {
  let keyframes = [0, 1, 2, 3, 4].map {
    CGAffineTransform(rotationAngle: $0 * .pi / 2)
  }.map(CATransform3DMakeAffineTransform)
  
  return try ContainerBlock(
    layoutDirection: .horizontal,
    children: [
      textStyle(text)
    ],
    contentAnimation: BlockAnimation(
      changes: AnimationChanges.transform(keyframes),
      keyTimes: [0.0, 0.25, 0.5, 0.75, 1.0],
      duration: 2.0
    )
  ).addingEdgeInsets(EdgeInsets(top: 125.0, left: 125.0, bottom: 125.0, right: 125.0))
    .addingDecorations(
      boundary: BoundaryTrait.clipCorner(CornerRadii(cornerRadius)),
      border: BlockBorder(color: .black, width: 1.0),
      backgroundColor: .cyan
    )
}

private func createBlock() throws -> Block {
  try ContainerBlock(
    layoutDirection: .vertical,
    horizontalChildrenAlignment: .center,
    verticalChildrenAlignment: .center,
    children: [
      createAnimatedTextBlock(
        "Fade, interpolator: ease, duration: 500",
        color: .black,
        cornerRadius: 8.0,
        backgroundColor: UIColor.black.withAlphaComponent(0.05),
        duration: 0.5
      ).addingEdgeGaps(10.0),
      createAnimatedTextBlock(
        "Scale, interpolator: ease, duration: 500",
        color: .black,
        cornerRadius: 8.0,
        backgroundColor: UIColor.black.withAlphaComponent(0.05),
        duration: 0.5
      ).addingEdgeGaps(10.0)
    ]
  ).addingDecorations(
    boundary: BoundaryTrait.clipCorner(CornerRadii(8.0)),
    border: BlockBorder(color: .black, width: 3.0)
  )
}

PlaygroundPage.current.liveView = LayoutKitPlaygroundViewController(blockProvider: createBlock)
