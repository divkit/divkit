import CoreGraphics
import Foundation
import LayoutKit

/// Projects test geometry onto the scroll axis (`along`) and the cross axis (`across`),
/// so that a single test body can run for both scroll directions.
struct TestAxis {
  let direction: ScrollDirection

  init(_ direction: ScrollDirection) {
    self.direction = direction
  }

  func size(along: CGFloat, across: CGFloat) -> CGSize {
    direction.isHorizontal
      ? CGSize(width: along, height: across)
      : CGSize(width: across, height: along)
  }

  func along(_ size: CGSize) -> CGFloat {
    direction.isHorizontal ? size.width : size.height
  }

  func across(_ size: CGSize) -> CGFloat {
    direction.isHorizontal ? size.height : size.width
  }

  func minAlong(_ rect: CGRect) -> CGFloat {
    direction.isHorizontal ? rect.minX : rect.minY
  }

  func midAlong(_ rect: CGRect) -> CGFloat {
    direction.isHorizontal ? rect.midX : rect.midY
  }

  func maxAlong(_ rect: CGRect) -> CGFloat {
    direction.isHorizontal ? rect.maxX : rect.maxY
  }

  func alongSize(_ rect: CGRect) -> CGFloat {
    along(rect.size)
  }

  func minAcross(_ rect: CGRect) -> CGFloat {
    direction.isHorizontal ? rect.minY : rect.minX
  }

  func midAcross(_ rect: CGRect) -> CGFloat {
    direction.isHorizontal ? rect.midY : rect.midX
  }

  func maxAcross(_ rect: CGRect) -> CGFloat {
    direction.isHorizontal ? rect.maxY : rect.maxX
  }

  func acrossSize(_ rect: CGRect) -> CGFloat {
    across(rect.size)
  }

  func isAcrossResizable(_ block: Block) -> Bool {
    direction.isHorizontal ? block.isVerticallyResizable : block.isHorizontallyResizable
  }

  func textBlock(
    along: LayoutTrait,
    across: LayoutTrait,
    text: String = ""
  ) -> TextBlock {
    TextBlock(
      widthTrait: direction.isHorizontal ? along : across,
      heightTrait: direction.isHorizontal ? across : along,
      text: NSAttributedString(string: text),
      accessibilityElement: nil
    )
  }

  func fixedWrapContentBlock(
    along: CGFloat,
    across: CGFloat,
    constrainedAcross: Bool = false
  ) -> BlockWithFixedWrapContent {
    BlockWithFixedWrapContent(
      width: direction.isHorizontal ? along : across,
      height: direction.isHorizontal ? across : along,
      constrainedHorizontally: !direction.isHorizontal && constrainedAcross,
      constrainedVertically: direction.isHorizontal && constrainedAcross
    )
  }
}
