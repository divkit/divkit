import CoreGraphics

import CommonCorePublic

public final class LayeredBlock: BlockWithTraits, BlockWithLayout {
  typealias Layout = [CGRect]

  public struct Child: Equatable {
    public var content: Block
    public let alignment: BlockAlignment2D

    public init(content: Block, alignment: BlockAlignment2D = .default) {
      self.content = content
      self.alignment = alignment
    }
  }

  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let children: [Child]

  public convenience init(
    widthTrait: LayoutTrait = .resizable,
    heightTrait: LayoutTrait = .intrinsic,
    horizontalChildrenAlignment: Alignment = .leading,
    verticalChildrenAlignment: Alignment = .leading,
    children: [Block]
  ) {
    let childrenAlignment = BlockAlignment2D(
      horizontal: horizontalChildrenAlignment,
      vertical: verticalChildrenAlignment
    )
    let alignedChildren = children.map {
      Child(content: $0, alignment: childrenAlignment)
    }
    self.init(
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      children: alignedChildren
    )
  }

  public init(
    widthTrait: LayoutTrait = .resizable,
    heightTrait: LayoutTrait = .intrinsic,
    children: [Child]
  ) {
    precondition(!children.isEmpty)

    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.children = children
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(width):
      return width
    case let .intrinsic(_, minSize, maxSize):
      let width = children.map { $0.content.intrinsicContentWidth }.max()!
      return clamp(width, min: minSize, max: maxSize)
    case .weighted:
      return children.map { $0.content.intrinsicContentWidth }.max()!
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(height):
      return height
    case let .intrinsic(_, minSize, maxSize):
      let height = children.map { $0.content }.intrinsicHeights(forWidth: width).max()!
      return clamp(height, min: minSize, max: maxSize)
    case .weighted:
      return children.map { $0.content }.intrinsicHeights(forWidth: width).max()!
    }
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? LayeredBlock else {
      return false
    }

    return self == other
  }

  func laidOutHierarchy(for size: CGSize) -> (LayeredBlock, Layout) {
    let layout = makeChildrenFrames(size: size)
    let children = zip(self.children, layout).map { child, frame -> Child in
      modified(child) { $0.content = $0.content.laidOut(for: frame.size) }
    }
    let block = LayeredBlock(
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      children: children
    )
    return (block, layout)
  }
}

extension LayeredBlock: ImageContaining {
  public func getImageHolders() -> [ImageHolder] {
    children.flatMap { $0.content.getImageHolders() }
  }
}

extension LayeredBlock: ElementStateUpdating {
  public func updated(withStates states: BlocksState) throws -> LayeredBlock {
    let newChildren = try children.map {
      LayeredBlock.Child(
        content: try $0.content.updated(withStates: states),
        alignment: $0.alignment
      )
    }

    let childrenChanged = zip(newChildren, children).contains { $0.content !== $1.content }

    return childrenChanged
      ? LayeredBlock(
        widthTrait: widthTrait,
        heightTrait: heightTrait,
        children: newChildren
      )
      : self
  }
}

extension LayeredBlock: Equatable {
  public static func ==(lhs: LayeredBlock, rhs: LayeredBlock) -> Bool {
    lhs.widthTrait == rhs.widthTrait &&
      lhs.heightTrait == rhs.heightTrait &&
      lhs.children == rhs.children
  }
}

extension LayeredBlock.Child {
  public static func ==(lhs: LayeredBlock.Child, rhs: LayeredBlock.Child) -> Bool {
    lhs.alignment == rhs.alignment && lhs.content == rhs.content
  }
}
