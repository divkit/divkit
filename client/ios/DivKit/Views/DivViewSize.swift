import Foundation
import LayoutKit
import VGSL

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
        lhs == rhs
      case (.matchParent, .matchParent),
           (.dependsOnOtherDimensionSize, dependsOnOtherDimensionSize):
        true
      default:
        false
      }
    }
  }

  public let width: DivDimension
  public let height: DivDimension

  /// Size limits of the card. Internal, so that ``DivHostingView`` can clamp the maximum size it
  /// reports to SwiftUI.
  let minWidth: CGFloat
  let maxWidth: CGFloat
  let minHeight: CGFloat
  let maxHeight: CGFloat

  private let constrainedWidth: Bool
  private let constrainedHeight: Bool

  private let contentWidth: LazyContentSize<() -> CGFloat>
  private let contentHeight: LazyContentSize<(CGFloat) -> CGFloat>

  public init(block: Block) {
    let width: DivDimension = block
      .isHorizontallyResizable ? .matchParent : .desired(block.widthOfHorizontallyNonResizableBlock)
    let height = makeHeightDimensionSize(block: block, width: width)
    self.width = width
    self.height = height
    self.constrainedWidth = block.isHorizontallyConstrained
    self.constrainedHeight = block.isVerticallyConstrained
    self.minWidth = block.minWidth
    self.maxWidth = block.maxWidth
    self.minHeight = block.minHeight
    self.maxHeight = block.maxHeight
    self.contentWidth = LazyContentSize(compute: { block.intrinsicContentWidth })
    self.contentHeight = LazyContentSize(compute: { block.intrinsicContentHeight(forWidth: $0) })
  }

  /// Computes the actual size for a ``DivView`` given its parent's size.
  ///
  /// A dimension of `parentViewSize` that is greater than or equal to
  /// `CGFloat.greatestFiniteMagnitude` (including `CGFloat.infinity`, e.g. an unconstrained
  /// SwiftUI proposal) means "no parent constraint" in that dimension. A `match_parent`
  /// dimension then resolves to the intrinsic size of the card, clamped by its min and max size.
  /// Nested `match_parent` elements contribute their `min_size` to the intrinsic size, so the
  /// result is the size of the content only when the elements along that dimension are sized by
  /// `fixed` or `wrap_content`.
  ///
  /// - Parameters:
  /// - parentViewSize: The size of the parent view.
  ///
  /// - Returns:
  ///     A `CGSize` that represents the actual size for the ``DivView``.
  public func sizeFor(parentViewSize: CGSize) -> CGSize {
    let width = resolveWidth(parentWidth: parentViewSize.width)
    return CGSize(
      width: width,
      height: resolveHeight(width: width, parentHeight: parentViewSize.height)
    )
  }

  /// Resolves the width of the card for the given width of its parent, the same way as
  /// ``sizeFor(parentViewSize:)`` does.
  func resolveWidth(parentWidth: CGFloat) -> CGFloat {
    switch width {
    case .matchParent:
      if parentWidth.isUnboundedDimension {
        return clamp(contentWidth.compute(), min: minWidth, max: maxWidth)
      }
      return clamp(parentWidth, min: minWidth, max: maxWidth)
    case let .desired(value):
      return constrainedWidth ? min(value, parentWidth) : value
    case .dependsOnOtherDimensionSize:
      assertionFailure("Unexpected width")
      return -1
    }
  }

  /// Resolves the height of the card laid out with the given width for the given height of its
  /// parent, the same way as ``sizeFor(parentViewSize:)`` does.
  func resolveHeight(width: CGFloat, parentHeight: CGFloat) -> CGFloat {
    switch height {
    case .matchParent:
      if parentHeight.isUnboundedDimension {
        clamp(contentHeight.compute(width), min: minHeight, max: maxHeight)
      } else {
        clamp(parentHeight, min: minHeight, max: maxHeight)
      }
    case let .desired(value):
      value
    case let .dependsOnOtherDimensionSize(heightForWidth):
      if constrainedHeight {
        min(heightForWidth(width), parentHeight)
      } else {
        heightForWidth(width)
      }
    }
  }
}

/// Content size of a card, computed on demand. All values are equal to each other, so the content
/// size does not take part in ``DivViewSize`` equality.
private struct LazyContentSize<Compute>: Equatable {
  let compute: Compute

  static func ==(_: LazyContentSize, _: LazyContentSize) -> Bool {
    true
  }
}

private func makeHeightDimensionSize(block: Block, width: DivViewSize.DivDimension) -> DivViewSize
  .DivDimension {
  guard !block.isVerticallyResizable else {
    return .matchParent
  }
  switch width {
  case .matchParent, .desired:
    return .dependsOnOtherDimensionSize { block.heightOfVerticallyNonResizableBlock(forWidth: $0) }
  case .dependsOnOtherDimensionSize:
    assertionFailure("Unexpected width")
    return .desired(.zero)
  }
}

extension CGFloat {
  fileprivate var isUnboundedDimension: Bool {
    self >= CGFloat.greatestFiniteMagnitude
  }
}
