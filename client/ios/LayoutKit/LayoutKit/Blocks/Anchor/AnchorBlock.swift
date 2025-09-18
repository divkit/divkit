import CoreGraphics
import VGSL

public final class AnchorBlock: BlockWithLayout, BlockWithTraits {
  public struct Layout: Equatable {
    let leadingFrame: CGRect
    let centerFrame: CGRect
    let trailingFrame: CGRect
  }

  public let direction: ContainerBlock.LayoutDirection
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let crossAlignment: Alignment
  public let leading: Block?
  public let center: Block
  public let trailing: Block?

  public var intrinsicContentWidth: CGFloat {
    if case let .fixed(value) = widthTrait {
      return value
    }

    let widths = contents.map(\.intrinsicContentWidth)
    var result: CGFloat = switch direction {
    case .horizontal:
      widths.reduce(0, +)
    case .vertical:
      widths.max()!
    }

    if case let .intrinsic(_, minSize, maxSize) = widthTrait {
      result = clamp(result, min: minSize, max: maxSize)
    }

    return result
  }

  public var debugDescription: String {
    "Anchored"
  }

  private var contents: [Block] {
    [leading, center, trailing].compactMap { $0 }
  }

  public convenience init(
    direction: ContainerBlock.LayoutDirection = .horizontal,
    axialWeight: LayoutTrait.Weight = .default,
    crossTrait: LayoutTrait = .intrinsic,
    crossAlignment: Alignment = .center,
    leading: Block? = nil,
    center: Block,
    trailing: Block? = nil
  ) {
    let widthTrait: LayoutTrait
    let heightTrait: LayoutTrait
    switch direction {
    case .horizontal:
      widthTrait = .weighted(axialWeight)
      heightTrait = crossTrait
    case .vertical:
      heightTrait = .weighted(axialWeight)
      widthTrait = crossTrait
    }

    self.init(
      direction: direction,
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      crossAlignment: crossAlignment,
      leading: leading,
      center: center,
      trailing: trailing
    )
  }

  private init(
    direction: ContainerBlock.LayoutDirection,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait,
    crossAlignment: Alignment,
    leading: Block?,
    center: Block,
    trailing: Block?
  ) {
    self.direction = direction
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.crossAlignment = crossAlignment
    self.leading = leading
    self.center = center
    self.trailing = trailing

    for content in contents {
      switch direction {
      case .vertical: precondition(!content.isVerticallyResizable)
      case .horizontal: precondition(!content.isHorizontallyResizable)
      }
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    if case let .fixed(value) = widthTrait {
      return value
    }
    let heights = [leading, center, trailing].compactMap {
      $0?.intrinsicContentHeight(forWidth: width)
    }

    var result: CGFloat = switch direction {
    case .horizontal:
      heights.max()!
    case .vertical:
      heights.reduce(0, +)
    }

    if case let .intrinsic(_, minSize, maxSize) = widthTrait {
      result = clamp(result, min: minSize, max: maxSize)
    }

    return result
  }

  public func laidOutHierarchy(for size: CGSize) -> (AnchorBlock, Layout) {
    let layout = makeLayout(for: size)
    let laidOutSelf = AnchorBlock(
      direction: direction,
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      crossAlignment: crossAlignment,
      leading: leading?.laidOut(for: layout.leadingFrame.size),
      center: center.laidOut(for: layout.centerFrame.size),
      trailing: trailing?.laidOut(for: layout.trailingFrame.size)
    )

    return (laidOutSelf, makeLayout(for: size))
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? Self else { return false }
    return direction == other.direction
      && widthTrait == other.widthTrait
      && heightTrait == other.heightTrait
      && crossAlignment == other.crossAlignment
      && leading == other.leading
      && center == other.center
      && trailing == other.trailing
  }

  public func getImageHolders() -> [ImageHolder] {
    contents.flatMap { $0.getImageHolders() }
  }

  public func updated(withStates states: BlocksState) throws -> Self {
    try Self(
      direction: direction,
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      crossAlignment: crossAlignment,
      leading: leading?.updated(withStates: states),
      center: center.updated(withStates: states),
      trailing: trailing?.updated(withStates: states)
    )
  }

  public func updated(path: UIElementPath, isFocused: Bool) throws -> Self {
    try Self(
      direction: direction,
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      crossAlignment: crossAlignment,
      leading: leading?.updated(path: path, isFocused: isFocused),
      center: center.updated(path: path, isFocused: isFocused),
      trailing: trailing?.updated(path: path, isFocused: isFocused)
    )
  }

  func makeLayout(for size: CGSize) -> Layout {
    Layout(
      size: size,
      direction: direction,
      crossAlignment: crossAlignment,
      leading: leading,
      center: center,
      trailing: trailing
    )
  }
}
