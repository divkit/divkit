import Foundation

import BasePublic

public final class VideoBlock: BlockWithTraits {
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  public let model: VideoBlockViewModel
  public let state: VideoBlockViewState
  public let playerFactory: PlayerFactory

  public init(
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait,
    model: VideoBlockViewModel,
    state: VideoBlockViewState,
    playerFactory: PlayerFactory
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.model = model
    self.state = state
    self.playerFactory = playerFactory
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case .intrinsic, .weighted:
      return 0
    }
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      return value
    case .intrinsic, .weighted:
      return 0
    }
  }

  public func getImageHolders() -> [ImageHolder] { [] }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? VideoBlock else {
      return false
    }

    return self == other
  }

  public var debugDescription: String = ""
}

extension VideoBlock: LayoutCachingDefaultImpl {}
extension VideoBlock: ElementStateUpdatingDefaultImpl {}
