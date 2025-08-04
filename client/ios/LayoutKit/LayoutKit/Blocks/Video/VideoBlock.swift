import Foundation
import VGSL

public final class VideoBlock: BlockWithTraits {
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  public let model: VideoBlockViewModel
  public let state: VideoBlockViewState
  public let playerFactory: PlayerFactory
  public var debugDescription: String = ""

  public var path: UIElementPath? {
    model.path
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      value
    case .intrinsic, .weighted:
      0
    }
  }

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

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      value
    case .intrinsic, .weighted:
      0
    }
  }

  public func getImageHolders() -> [ImageHolder] { [] }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? VideoBlock else {
      return false
    }

    return self.model == other.model && self.state == other.state
  }

}

extension VideoBlock: LayoutCachingDefaultImpl {}

extension VideoBlock: ElementStateUpdating {
  public func updated(withStates states: BlocksState) throws -> Self {
    guard let state: VideoBlockViewState = states.getState(at: model.path) else { return self }

    return Self(
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      model: model,
      state: state,
      playerFactory: playerFactory
    )
  }
}
