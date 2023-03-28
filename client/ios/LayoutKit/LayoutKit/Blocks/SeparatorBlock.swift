import CoreGraphics
import Foundation

import CommonCorePublic

public final class SeparatorBlock: BlockWithTraits {
  public enum Direction {
    case horizontal
    case vertical
  }

  public let color: Color
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  private init(
    color: Color = .clear,
    direction: Direction = .horizontal,
    trait: LayoutTrait = .resizable
  ) {
    self.color = color
    switch direction {
    case .horizontal:
      widthTrait = trait
      heightTrait = .fixed(1)
    case .vertical:
      widthTrait = .fixed(1)
      heightTrait = trait
    }
  }

  public convenience init(
    color: Color = .clear,
    direction: Direction = .horizontal,
    weight: LayoutTrait.Weight = .default
  ) {
    self.init(color: color, direction: direction, trait: .weighted(weight))
  }

  public convenience init(
    color: Color = .clear,
    direction: Direction = .horizontal,
    size: CGFloat
  ) {
    self.init(color: color, direction: direction, trait: .fixed(size))
  }

  public var intrinsicContentWidth: CGFloat {
    widthTrait.intrinsicSize
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    heightTrait.intrinsicSize
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? SeparatorBlock else {
      return false
    }

    return self == other
  }

  public func getImageHolders() -> [ImageHolder] { [] }
}

public func ==(lhs: SeparatorBlock, rhs: SeparatorBlock) -> Bool {
  lhs.color == rhs.color && lhs.widthTrait == rhs.widthTrait && lhs.heightTrait == rhs.heightTrait
}

extension SeparatorBlock: LayoutCachingDefaultImpl {}
extension SeparatorBlock: ElementStateUpdatingDefaultImpl {}

extension LayoutTrait {
  fileprivate var intrinsicSize: CGFloat {
    switch self {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, _):
      return minSize
    case .weighted:
      return 0
    }
  }
}
