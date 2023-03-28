import CoreGraphics
import Foundation

import CommonCorePublic
import LayoutKitInterface

public final class PagerBlock: BlockWithTraits {
  public enum LayoutMode: Equatable {
    case pageSize(RelativeValue)
    case neighbourPageSize(CGFloat)
  }

  public enum Error: NonEmptyString, BlockError, Equatable {
    case inconsistentChildLayoutTraits

    public var errorMessage: NonEmptyString { rawValue }
  }

  public let pagerPath: PagerPath?
  public let layoutMode: LayoutMode
  public let gallery: GalleryViewModel
  public let state: PagerViewState
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  let selectedActions: [[UserInterfaceAction]]

  public init(
    pagerPath: PagerPath?,
    layoutMode: LayoutMode,
    gallery: GalleryViewModel,
    selectedActions: [[UserInterfaceAction]],
    state: PagerViewState,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait
  ) throws {
    self.pagerPath = pagerPath
    self.layoutMode = layoutMode
    self.gallery = gallery
    self.selectedActions = selectedActions
    self.state = state.synchronized(with: gallery)
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait

    if case .intrinsic = widthTrait, case .vertical = gallery.direction {
      guard gallery.items.map({ $0.content }).hasHorizontallyNonResizable else {
        throw Error.inconsistentChildLayoutTraits
      }
    }
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      let width = gallery.intrinsicPagerSize(
        forWidth: CGFloat.greatestFiniteMagnitude,
        pageIndex: Int(round(state.currentPage)),
        layoutMode: layoutMode
      ).width
      return clamp(width, min: minSize, max: maxSize)
    case .weighted:
      return 0
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      let height = gallery.intrinsicPagerSize(
        forWidth: width,
        pageIndex: Int(round(state.currentPage)),
        layoutMode: layoutMode
      ).height
      return clamp(height, min: minSize, max: maxSize)
    case .weighted:
      return 0
    }
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? PagerBlock else { return false }
    return self == other
  }
}

extension PagerBlock: Equatable {
  public static func ==(lhs: PagerBlock, rhs: PagerBlock) -> Bool {
    lhs.pagerPath == rhs.pagerPath &&
      lhs.layoutMode == rhs.layoutMode &&
      lhs.gallery == rhs.gallery &&
      lhs.state == rhs.state &&
      lhs.widthTrait == rhs.widthTrait &&
      lhs.heightTrait == rhs.heightTrait
  }
}

extension PagerBlock: ImageContaining {
  public func getImageHolders() -> [ImageHolder] {
    gallery.items.flatMap { $0.content.getImageHolders() }
  }
}

extension PagerBlock: ElementStateUpdating {
  public func updated(withStates states: BlocksState) throws -> PagerBlock {
    let newBlocks = try gallery.items.map { try $0.content.updated(withStates: states) }
    let blocksAreNotEqual = zip(gallery.items, newBlocks)
      .contains(where: { $0.content !== $1 })

    let newState = states.pagerViewState(for: pagerPath)

    guard blocksAreNotEqual || state != newState else {
      return self
    }

    let newModel: GalleryViewModel
    if blocksAreNotEqual {
      newModel = modified(gallery) {
        $0.items.apply(contents: newBlocks)
      }
    } else {
      newModel = gallery
    }

    return try PagerBlock(
      pagerPath: pagerPath,
      layoutMode: layoutMode,
      gallery: newModel,
      selectedActions: selectedActions,
      state: newState ?? state,
      widthTrait: widthTrait,
      heightTrait: heightTrait
    )
  }
}

extension PagerBlock: LayoutCachingDefaultImpl {}

extension Array where Element == GalleryViewModel.Item {
  fileprivate mutating func apply(contents: [Block]) {
    for i in indices {
      self[i].content = contents[i]
    }
  }
}
