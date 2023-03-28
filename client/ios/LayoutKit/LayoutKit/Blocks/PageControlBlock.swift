import CoreGraphics
import Foundation

import CommonCorePublic

public final class PageControlBlock: BlockWithTraits {
  public typealias State = PagerViewState

  public let pagerPath: PagerPath?
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let configuration: PageIndicatorConfiguration
  public let state: State

  public init(
    pagerPath: PagerPath?,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait,
    configuration: PageIndicatorConfiguration,
    state: State
  ) {
    self.pagerPath = pagerPath
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.configuration = configuration
    self.state = state
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      let width: CGFloat
      switch configuration.itemPlacement {
      case let .fixed(spaceBetweenCenters):
        width = spaceBetweenCenters * CGFloat(state.numberOfPages)
      case .stretch:
        width = 0
      }

      return clamp(width, min: minSize, max: maxSize)
    case .weighted:
      return 0
    }
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      let height = configuration.pageSize.height * configuration.highlightedHeightScale
      return clamp(height, min: minSize, max: maxSize)
    case .weighted:
      return configuration.pageSize.height * configuration.highlightedHeightScale
    }
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? PageControlBlock else {
      return false
    }
    return self == other
  }

  public func getImageHolders() -> [ImageHolder] { [] }

  public func updated(withStates states: BlocksState) throws -> PageControlBlock {
    guard let newState = states.pagerViewState(for: pagerPath) else {
      return self
    }
    return PageControlBlock(
      pagerPath: pagerPath,
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      configuration: configuration,
      state: newState
    )
  }
}

public func ==(lhs: PageControlBlock, rhs: PageControlBlock) -> Bool {
  lhs.pagerPath == rhs.pagerPath &&
    lhs.widthTrait == rhs.widthTrait &&
    lhs.heightTrait == rhs.heightTrait &&
    lhs.configuration == rhs.configuration &&
    lhs.state == rhs.state
}

extension PageControlBlock: LayoutCachingDefaultImpl {}
