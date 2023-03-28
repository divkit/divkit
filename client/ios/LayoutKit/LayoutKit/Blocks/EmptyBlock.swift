import CoreGraphics
import Foundation

import CommonCorePublic

public final class EmptyBlock: BlockWithTraits {
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  public init(
    widthTrait: LayoutTrait = .resizable,
    heightTrait: LayoutTrait = .resizable
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
  }

  public var intrinsicContentWidth: CGFloat {
    widthTrait.intrinsicSize
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    heightTrait.intrinsicSize
  }

  public func getImageHolders() -> [ImageHolder] { [] }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? EmptyBlock else {
      return false
    }

    return self == other
  }
}

public func ==(lhs: EmptyBlock, rhs: EmptyBlock) -> Bool {
  lhs.widthTrait == rhs.widthTrait && lhs.heightTrait == rhs.heightTrait
}

extension EmptyBlock {
  public static let zeroSized = EmptyBlock(widthTrait: .fixed(0), heightTrait: .fixed(0))
}

extension EmptyBlock: LayoutCachingDefaultImpl {}
extension EmptyBlock: ElementStateUpdatingDefaultImpl {}

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
