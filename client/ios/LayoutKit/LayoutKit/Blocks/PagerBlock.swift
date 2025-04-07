import CoreGraphics
import Foundation
import VGSL

public final class PagerBlock: BlockWithTraits {
  public enum LayoutMode: Equatable {
    case pageSize(RelativeValue)
    case neighbourPageSize(CGFloat)
    case pageContentSize
  }

  public let pagerPath: PagerPath?
  public let layoutMode: LayoutMode
  public let alignment: Alignment
  public let gallery: GalleryViewModel
  public let state: PagerViewState
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  let selectedActions: [[UserInterfaceAction]]

  public init(
    pagerPath: PagerPath?,
    alignment: Alignment,
    layoutMode: LayoutMode,
    gallery: GalleryViewModel,
    selectedActions: [[UserInterfaceAction]],
    state: PagerViewState,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait
  ) throws {
    self.pagerPath = pagerPath
    self.alignment = alignment
    self.layoutMode = layoutMode
    self.gallery = gallery
    self.selectedActions = selectedActions
    self.state = state.synchronized(with: gallery)
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait

    if case .intrinsic = widthTrait, case .vertical = gallery.direction {
      guard gallery.items.map(\.content).hasHorizontallyNonResizable else {
        throw BlockError(
          "Pager block error: in intrinsic-width vertical pager all children have resizable width"
        )
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
      .contains { $0.content !== $1 }

    let newState = states.getState(at: gallery.path) ?? state

    guard blocksAreNotEqual || state != newState else {
      return self
    }

    let newModel: GalleryViewModel = if blocksAreNotEqual {
      modified(gallery) {
        $0.items.apply(contents: newBlocks)
      }
    } else {
      gallery
    }

    return try PagerBlock(
      pagerPath: pagerPath,
      alignment: alignment,
      layoutMode: layoutMode,
      gallery: newModel,
      selectedActions: selectedActions,
      state: newState,
      widthTrait: widthTrait,
      heightTrait: heightTrait
    )
  }
}

extension PagerBlock: ElementFocusUpdating {
  public func updated(path: UIElementPath, isFocused: Bool) throws -> PagerBlock {
    let newBlocks = try gallery.items.map {
      try $0.content.updated(path: path, isFocused: isFocused)
    }
    let blocksAreNotEqual = zip(gallery.items, newBlocks)
      .contains { $0.content !== $1 }

    guard blocksAreNotEqual else { return self }

    let newModel: GalleryViewModel = modified(gallery) {
      $0.items.apply(contents: newBlocks)
    }

    return try PagerBlock(
      pagerPath: pagerPath,
      alignment: alignment,
      layoutMode: layoutMode,
      gallery: newModel,
      selectedActions: selectedActions,
      state: state,
      widthTrait: widthTrait,
      heightTrait: heightTrait
    )
  }
}

extension PagerBlock: LayoutCachingDefaultImpl {}

extension [GalleryViewModel.Item] {
  fileprivate mutating func apply(contents: [Block]) {
    for i in indices {
      self[i].content = contents[i]
    }
  }
}

extension BlocksState {
  public func pagerViewState(for pagerPath: PagerPath?) -> PagerViewState? {
    pagerPath.flatMap { pagerPath in
      first(where: { pagerPath.matches($0.key) })?.value as? PagerViewState
    }
  }

  func pagerViewState(
    for pagerControlPath: UIElementPath?
  ) -> PagerViewState? {
    pagerControlPath.flatMap { pagerControlPath in
      var currentPath: UIElementPath? = pagerControlPath

      while let current = currentPath {
        let relatedPaths = keys.filter { $0.starts(with: current) }

        for relatedPath in relatedPaths {
          if self[relatedPath] is PagerViewState {
            return self[relatedPath] as? PagerViewState
          }
        }

        currentPath = current.parent
      }

      return nil
    }
  }
}

extension PagerPath {
  fileprivate func matches(_ path: UIElementPath) -> Bool {
    if path.root != cardId {
      return false
    }
    return path.leaf == pagerId
  }
}
