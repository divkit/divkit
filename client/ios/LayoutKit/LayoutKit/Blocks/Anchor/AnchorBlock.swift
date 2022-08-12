// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics

import CommonCore

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

  private var contents: [Block] {
    [leading, center, trailing].compactMap { $0 }
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

    contents.forEach {
      switch direction {
      case .vertical: precondition(!$0.isVerticallyResizable)
      case .horizontal: precondition(!$0.isHorizontallyResizable)
      }
    }
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

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case .intrinsic, .weighted:
      let widths = contents.map(\.intrinsicContentWidth)
      switch direction {
      case .horizontal: return widths.reduce(0, +)
      case .vertical: return widths.max()!
      }
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      return value
    case .intrinsic, .weighted:
      let heights = [leading, center, trailing].compactMap {
        $0?.intrinsicContentHeight(forWidth: width)
      }
      switch direction {
      case .horizontal: return heights.max()!
      case .vertical: return heights.reduce(0, +)
      }
    }
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

  public var debugDescription: String {
    "Anchored"
  }

  public func getImageHolders() -> [ImageHolder] {
    contents.flatMap { $0.getImageHolders() }
  }

  public func updated(withStates states: BlocksState) throws -> Self {
    Self(
      direction: direction,
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      crossAlignment: crossAlignment,
      leading: try leading?.updated(withStates: states),
      center: try center.updated(withStates: states),
      trailing: try trailing?.updated(withStates: states)
    )
  }
}
