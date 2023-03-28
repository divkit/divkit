import CoreGraphics
import Foundation

import CommonCorePublic

#if os(iOS)
public typealias BlockRenderingImpl = UIViewRenderable
#else
public protocol BlockRenderingImpl {}
#endif

public protocol Block: AnyObject,
  CustomDebugStringConvertible,
  BlockRenderingImpl,
  ImageContaining,
  LayoutCaching,
  ElementStateUpdating,
  AccessibilityContaining {
  var isVerticallyResizable: Bool { get }
  var isHorizontallyResizable: Bool { get }

  var isVerticallyConstrained: Bool { get }
  var isHorizontallyConstrained: Bool { get }

  var widthOfHorizontallyNonResizableBlock: CGFloat { get }
  var heightOfVerticallyNonResizableBlock: CGFloat { get }
  func widthOfHorizontallyNonResizableBlock(forHeight: CGFloat) -> CGFloat
  func heightOfVerticallyNonResizableBlock(forWidth: CGFloat) -> CGFloat

  var calculateWidthFirst: Bool { get }
  var intrinsicContentWidth: CGFloat { get }
  func intrinsicContentHeight(forWidth: CGFloat) -> CGFloat

  /// The top offset from the baseline.
  /// Returns nil if the block has no baseline.
  func ascent(forWidth: CGFloat) -> CGFloat?

  var weightOfHorizontallyResizableBlock: LayoutTrait.Weight { get }
  var weightOfVerticallyResizableBlock: LayoutTrait.Weight { get }

  var minWidth: CGFloat { get }
  var minHeight: CGFloat { get }

  func equals(_ other: Block) -> Bool
}

extension Block {
  public var calculateWidthFirst: Bool { true }

  public func widthOfHorizontallyNonResizableBlock(forHeight _: CGFloat) -> CGFloat {
    assertionFailure("Method should be overridden when calculateWidthFirst was set to false")
    return .zero
  }

  public var heightOfVerticallyNonResizableBlock: CGFloat {
    assertionFailure("Method should be overridden when calculateWidthFirst was set to false")
    return .zero
  }

  public func sizeFor(
    widthOfHorizontallyResizableBlock: CGFloat,
    heightOfVerticallyResizableBlock: CGFloat,
    constrainedWidth: CGFloat,
    constrainedHeight: CGFloat
  ) -> CGSize {
    if calculateWidthFirst {
      let width: CGFloat
      if isHorizontallyResizable {
        width = widthOfHorizontallyResizableBlock
      } else {
        let intrinsicWidth = widthOfHorizontallyNonResizableBlock
        width = isHorizontallyConstrained ? min(intrinsicWidth, constrainedWidth) : intrinsicWidth
      }
      let height: CGFloat
      if isVerticallyResizable {
        height = heightOfVerticallyResizableBlock
      } else {
        let intrinsicHeight = heightOfVerticallyNonResizableBlock(forWidth: width)
        height = isVerticallyConstrained ? min(intrinsicHeight, constrainedHeight) : intrinsicHeight
      }
      return CGSize(width: width, height: height)
    } else {
      let height: CGFloat
      if isVerticallyResizable {
        height = heightOfVerticallyResizableBlock
      } else {
        let intrinsicHeight = heightOfVerticallyNonResizableBlock
        height = isVerticallyConstrained ? min(intrinsicHeight, constrainedHeight) : intrinsicHeight
      }
      let width: CGFloat
      if isHorizontallyResizable {
        width = widthOfHorizontallyResizableBlock
      } else {
        let intrinsicWidth = widthOfHorizontallyNonResizableBlock(forHeight: height)
        width = isHorizontallyConstrained ? min(intrinsicWidth, constrainedWidth) : intrinsicWidth
      }
      return CGSize(width: width, height: height)
    }
  }

  public func size(forResizableBlockSize resizableBlockSize: CGSize) -> CGSize {
    sizeFor(
      widthOfHorizontallyResizableBlock: resizableBlockSize.width,
      heightOfVerticallyResizableBlock: resizableBlockSize.height,
      constrainedWidth: .infinity,
      constrainedHeight: .infinity
    )
  }

  public var intrinsicSize: CGSize {
    let width = intrinsicContentWidth
    let height = intrinsicContentHeight(forWidth: width)

    return CGSize(width: width, height: height)
  }

  public var calculatedWidthTrait: LayoutTrait {
    isHorizontallyResizable
      ? .weighted(weightOfHorizontallyResizableBlock)
      : .intrinsic
  }

  public var calculatedHeightTrait: LayoutTrait {
    isVerticallyResizable
      ? .weighted(weightOfVerticallyResizableBlock)
      : .intrinsic
  }

  public func ascent(forWidth _: CGFloat) -> CGFloat? {
    nil
  }

  public var minWidth: CGFloat { 0 }
  public var minHeight: CGFloat { 0 }
}

public func ==(lhs: Block, rhs: Block) -> Bool {
  if lhs === rhs {
    return true
  }

  return lhs.equals(rhs)
}

public func ==(lhs: Block?, rhs: Block?) -> Bool {
  switch (lhs, rhs) {
  case (.none, .none):
    return true
  case let (.some(value1), .some(value2)):
    return value1 == value2
  default:
    return false
  }
}

public func !=(lhs: Block, rhs: Block) -> Bool {
  !(lhs == rhs)
}

public func !=(lhs: Block?, rhs: Block?) -> Bool {
  !(lhs == rhs)
}

public func ==(lhs: [Block], rhs: [Block]) -> Bool {
  guard lhs.count == rhs.count else { return false }

  for pair in zip(lhs, rhs) {
    if pair.0 != pair.1 { return false }
  }

  return true
}

public func !=(lhs: [Block], rhs: [Block]) -> Bool {
  !(lhs == rhs)
}

// Could not do it with <T: AnyObject> because of:
// Using 'Block' as a concrete type conforming to protocol 'AnyObject' is not supported
public func ===(lhs: [Block], rhs: [Block]) -> Bool {
  guard lhs.count == rhs.count else { return false }

  for pair in zip(lhs, rhs) {
    guard pair.0 === pair.1 else {
      return false
    }
  }

  return true
}

public func !==(lhs: [Block], rhs: [Block]) -> Bool {
  !(lhs === rhs)
}

extension Sequence where Element == Block {
  public var hasVerticallyNonResizable: Bool {
    contains { !$0.isVerticallyResizable }
  }

  public var hasHorizontallyNonResizable: Bool {
    contains { !$0.isHorizontallyResizable }
  }

  public var hasVerticallyResizable: Bool {
    contains { $0.isVerticallyResizable }
  }

  public var hasHorizontallyResizable: Bool {
    contains { $0.isHorizontallyResizable }
  }

  public var allVerticallyResizable: Bool {
    !hasVerticallyNonResizable
  }

  public var allHorizontallyResizable: Bool {
    !hasHorizontallyNonResizable
  }

  public var allVerticallyNonResizable: Bool {
    !hasVerticallyResizable
  }

  public var allHorizontallyNonResizable: Bool {
    !hasHorizontallyResizable
  }

  public func maxHeightOfVerticallyNonResizableBlocks(for widths: [CGFloat]) -> CGFloat? {
    zip(self, widths)
      .filter { !$0.0.isVerticallyResizable }
      .map { $0.0.heightOfVerticallyNonResizableBlock(forWidth: $0.1) }
      .max()
  }

  var maxWidthOfHorizontallyNonResizableBlocks: CGFloat? {
    self
      .filter { !$0.isHorizontallyResizable }
      .map { $0.widthOfHorizontallyNonResizableBlock }
      .max()
  }

  func maxHeightOfVerticallyNonResizableBlocks(for width: CGFloat) -> CGFloat? {
    self
      .filter { !$0.isVerticallyResizable }
      .map { $0.heightOfVerticallyNonResizableBlock(forWidth: width) }
      .max()
  }
}
