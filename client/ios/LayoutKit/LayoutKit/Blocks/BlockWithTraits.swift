import CoreGraphics

public protocol BlockWithTraits: BlockWithWidthTrait, BlockWithHeightTrait {}

public protocol BlockWithWidthTrait: Block {
  var widthTrait: LayoutTrait { get }
}

public protocol BlockWithHeightTrait: Block {
  var heightTrait: LayoutTrait { get }
}

extension BlockWithWidthTrait {
  public var isHorizontallyResizable: Bool { widthTrait.isResizable }

  public var isHorizontallyConstrained: Bool { widthTrait.isConstrained }

  public var widthOfHorizontallyNonResizableBlock: CGFloat {
    if case LayoutTrait.weighted = widthTrait {
      assertionFailure("cannot get widthOfHorizontallyNonResizableBlock for resizable block")
      return 0
    }
    return intrinsicContentWidth
  }

  public var weightOfHorizontallyResizableBlock: LayoutTrait.Weight {
    guard case let .weighted(value) = widthTrait else {
      assertionFailure("cannot get weightOfHorizontallyResizableBlock for non resizable block")
      return .default
    }
    return value
  }

  public var minWidth: CGFloat {
    switch widthTrait {
    case let .fixed(size):
      return size
    case .weighted:
      return 0
    case .intrinsic(constrained: _, minSize: let minSize, maxSize: _):
      return minSize
    }
  }
}

extension BlockWithHeightTrait {
  public var isVerticallyResizable: Bool { heightTrait.isResizable }

  public var isVerticallyConstrained: Bool { heightTrait.isConstrained }

  public func heightOfVerticallyNonResizableBlock(forWidth width: CGFloat) -> CGFloat {
    if case LayoutTrait.weighted = heightTrait {
      assertionFailure("cannot get heightOfVerticallyNonResizableBlock for resizable block")
      return 0
    }
    return intrinsicContentHeight(forWidth: width)
  }

  public var weightOfVerticallyResizableBlock: LayoutTrait.Weight {
    guard case let .weighted(value) = heightTrait else {
      assertionFailure("cannot get weightOfVerticallyResizableBlock for non resizable block")
      return .default
    }
    return value
  }

  public var minHeight: CGFloat {
    switch heightTrait {
    case let .fixed(size):
      return size
    case .weighted:
      return 0
    case .intrinsic(constrained: _, minSize: let minSize, maxSize: _):
      return minSize
    }
  }
}
