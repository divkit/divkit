import CoreGraphics
import Foundation

import CommonCorePublic
import LayoutKitInterface

public final class GalleryBlock: BlockWithTraits {
  public enum Error: NonEmptyString, BlockError, Equatable {
    case inconsistentChildLayoutTraits

    public var errorMessage: NonEmptyString { rawValue }
  }

  private lazy var contentSize: CGSize = model.intrinsicSize

  public let model: GalleryViewModel
  public let state: GalleryViewState
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  public init(
    model: GalleryViewModel,
    state: GalleryViewState,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait
  ) throws {
    self.model = model
    self.state = state.resetToModelIfInconsistent(model)
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait

    if case .intrinsic = widthTrait, case .vertical = model.direction {
      guard model.items.map({ $0.content }).hasHorizontallyNonResizable else {
        throw Error.inconsistentChildLayoutTraits
      }
    }
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      let width = contentSize.width
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
      let height = contentSize.height
      return clamp(height, min: minSize, max: maxSize)
    case .weighted:
      return 0
    }
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? GalleryBlock else { return false }
    return self == other
  }
}

extension GalleryBlock {
  public convenience init(
    gaps: [CGFloat],
    children: [Block],
    path: UIElementPath,
    direction: GalleryViewModel.Direction,
    crossAlignment: Alignment,
    scrollMode: GalleryViewModel.ScrollMode = .default,
    state: GalleryViewState? = nil,
    widthTrait: LayoutTrait? = nil,
    heightTrait: LayoutTrait? = nil,
    areEmptySpaceTouchesEnabled: Bool = true,
    alwaysBounceVertical: Bool = false,
    bounces: Bool = true
  ) throws {
    let model = GalleryViewModel(
      blocks: children,
      metrics: GalleryViewMetrics(gaps: gaps),
      scrollMode: scrollMode,
      path: path,
      direction: direction,
      crossAlignment: crossAlignment,
      areEmptySpaceTouchesEnabled: areEmptySpaceTouchesEnabled,
      alwaysBounceVertical: alwaysBounceVertical,
      bounces: bounces
    )

    try self.init(
      model: model,
      state: state ?? GalleryViewState(contentOffset: 0, itemsCount: children.count),
      widthTrait: widthTrait ?? (direction.isHorizontal ? .resizable : .intrinsic),
      heightTrait: heightTrait ?? (direction.isHorizontal ? .intrinsic : .resizable)
    )
  }

  public convenience init(
    gaps: [CGFloat],
    children: [Block],
    path: UIElementPath,
    direction: GalleryViewModel.Direction,
    crossAlignment: Alignment,
    scrollMode: GalleryViewModel.ScrollMode = .default,
    contentPosition: GalleryViewState.Position,
    widthTrait: LayoutTrait? = nil,
    heightTrait: LayoutTrait? = nil,
    areEmptySpaceTouchesEnabled: Bool = true,
    alwaysBounceVertical: Bool = false,
    bounces: Bool = true
  ) throws {
    try self.init(
      gaps: gaps,
      children: children,
      path: path,
      direction: direction,
      crossAlignment: crossAlignment,
      scrollMode: scrollMode,
      state: GalleryViewState(
        contentPosition: contentPosition,
        itemsCount: children.count,
        isScrolling: false
      ),
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      areEmptySpaceTouchesEnabled: areEmptySpaceTouchesEnabled,
      alwaysBounceVertical: alwaysBounceVertical,
      bounces: bounces
    )
  }
}

extension GalleryBlock: Equatable {
  public static func ==(lhs: GalleryBlock, rhs: GalleryBlock) -> Bool {
    lhs.model == rhs.model &&
      lhs.state == rhs.state &&
      lhs.widthTrait == rhs.widthTrait &&
      lhs.heightTrait == rhs.heightTrait
  }
}

extension GalleryBlock: ImageContaining {
  public func getImageHolders() -> [ImageHolder] {
    model.items.flatMap { $0.content.getImageHolders() }
  }
}

extension GalleryBlock: ElementStateUpdating {
  public func updated(withStates states: BlocksState) throws -> GalleryBlock {
    let newBlocks = try model.items.map { try $0.content.updated(withStates: states) }
    let blocksAreNotEqual = zip(model.items, newBlocks).contains(where: { $0.content !== $1 })

    let newModel = blocksAreNotEqual
      ? modified(model) { $0.items.apply(contents: newBlocks) }
      : model
    let newState = states.getState(at: model.path) ?? state

    if newState != state || newModel != model {
      return try GalleryBlock(
        model: newModel,
        state: newState,
        widthTrait: widthTrait,
        heightTrait: heightTrait
      )
    }

    return self
  }
}

extension GalleryBlock: LayoutCachingDefaultImpl {}

extension Array where Element == GalleryViewModel.Item {
  fileprivate mutating func apply(contents: [Block]) {
    for i in indices {
      self[i].content = contents[i]
    }
  }
}
