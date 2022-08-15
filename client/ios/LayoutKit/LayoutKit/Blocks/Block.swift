import CoreGraphics
import Foundation

import CommonCore

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
  func heightOfVerticallyNonResizableBlock(forWidth: CGFloat) -> CGFloat

  var intrinsicContentWidth: CGFloat { get }
  func intrinsicContentHeight(forWidth: CGFloat) -> CGFloat

  var weightOfHorizontallyResizableBlock: LayoutTrait.Weight { get }
  var weightOfVerticallyResizableBlock: LayoutTrait.Weight { get }

  func equals(_ other: Block) -> Bool
}

extension Block {
  public func sizeFor(
    widthOfHorizontallyResizableBlock: CGFloat,
    heightOfVerticallyResizableBlock: CGFloat
  ) -> CGSize {
    let width = isHorizontallyResizable ? widthOfHorizontallyResizableBlock :
      widthOfHorizontallyNonResizableBlock
    let height = isVerticallyResizable ? heightOfVerticallyResizableBlock :
      heightOfVerticallyNonResizableBlock(forWidth: width)
    return CGSize(width: width, height: height)
  }

  public func size(forResizableBlockSize resizableBlockSize: CGSize) -> CGSize {
    sizeFor(
      widthOfHorizontallyResizableBlock: resizableBlockSize.width,
      heightOfVerticallyResizableBlock: resizableBlockSize.height
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
