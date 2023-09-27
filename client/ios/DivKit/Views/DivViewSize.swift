import Foundation
import LayoutKit

/// ``DivViewSize`` represents the sizing information for a division card.
///
/// The structure offers flexibility in specifying sizes, ranging from
/// matching the parent's size, having a fixed desired size, to a dynamic size
/// dependent on another dimension.
public struct DivViewSize: Equatable {
  /// Represents the different ways to specify a dimension for the `DivViewSize`.
  ///
  /// - ``matchParent``: This dimension should match the parent's size.
  /// - ``desired(_:)``: This dimension has a fixed size.
  /// - ``dependsOnOtherDimensionSize(_:)``: The size depends on the size of another dimension.
  public enum DivDimension: Equatable {
    case matchParent
    case desired(CGFloat)
    case dependsOnOtherDimensionSize((CGFloat) -> CGFloat)

    public static func ==(lhs: DivDimension, rhs: DivDimension) -> Bool {
      switch (lhs, rhs) {
      case let (.desired(lhs), .desired(rhs)):
        return lhs == rhs
      case (.matchParent, .matchParent),
           (.dependsOnOtherDimensionSize, dependsOnOtherDimensionSize):
        return true
      default:
        return false
      }
    }
  }

  public init(block: Block) {
    let width: DivDimension = block
      .isHorizontallyResizable ? .matchParent : .desired(block.widthOfHorizontallyNonResizableBlock)
    let height = makeHeightDimensionSize(block: block, width: width)
    self.width = width
    self.height = height
  }

  public let width: DivDimension
  public let height: DivDimension

  /// Computes the actual size for a ``DivView`` given its parent's size.
  ///
  /// - Parameters:
  /// - parentViewSize: The size of the parent view.
  ///
  /// - Returns:
  ///     A `CGSize` that represents the actual size for the ``DivView``.
  public func sizeFor(parentViewSize: CGSize) -> CGSize {
    let width: CGFloat
    switch self.width {
    case .matchParent:
      width = parentViewSize.width
    case let .desired(value):
      width = value
    case .dependsOnOtherDimensionSize:
      assertionFailure("Unexpected width")
      width = -1
    }
    let height: CGFloat
    switch self.height {
    case .matchParent:
      height = parentViewSize.width
    case let .desired(value):
      height = value
    case let .dependsOnOtherDimensionSize(heightForWidth):
      height = heightForWidth(width)
    }
    return CGSize(width: width, height: height)
  }
}

private func makeHeightDimensionSize(block: Block, width: DivViewSize.DivDimension) -> DivViewSize
  .DivDimension {
  guard !block.isVerticallyResizable else {
    return .matchParent
  }
  switch width {
  case .matchParent:
    return .dependsOnOtherDimensionSize { block.heightOfVerticallyNonResizableBlock(forWidth: $0) }
  case let .desired(width):
    return .desired(block.heightOfVerticallyNonResizableBlock(forWidth: width))
  case .dependsOnOtherDimensionSize:
    assertionFailure("Unexpected width")
    return .desired(.zero)
  }
}
