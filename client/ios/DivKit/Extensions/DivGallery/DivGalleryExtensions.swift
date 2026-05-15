import CoreGraphics
import LayoutKit
import VGSL

extension DivGallery: DivBlockModeling, DivGalleryProtocol {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let context = modifiedContextParentPath(context)
    return try modifyError({ DivBlockModelingError($0.message, path: context.path) }) {
      try applyBaseProperties(
        to: { try makeBaseBlock(context: context) },
        context: context,
        actionsHolder: nil,
        applyPaddings: false
      )
    }
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let expressionResolver = context.expressionResolver
    let itemSpacing = resolveItemSpacing(expressionResolver)
    let model = try makeGalleryModel(
      context: context,
      direction: resolveOrientation(expressionResolver).direction,
      alignment: .center,
      spacing: CGFloat(itemSpacing),
      crossSpacing: CGFloat(resolveCrossSpacing(expressionResolver) ?? itemSpacing),
      defaultCrossAlignment: resolveCrossContentAlignment(expressionResolver).blockAlignment,
      scrollMode: resolveScrollMode(expressionResolver).blockScrollMode,
      columnCount: resolveColumnCount(expressionResolver),
      scrollbar: resolveScrollbar(expressionResolver).blockScrollbar
    )
    return try GalleryBlock(
      model: model,
      state: getState(context: context, itemsCount: model.items.count),
      widthTrait: resolveWidthTrait(context),
      heightTrait: resolveHeightTrait(context)
    )
  }

  private func getState(
    context: DivBlockModelingContext,
    itemsCount: Int
  ) -> GalleryViewState {
    let storage = context.blockStateStorage
    let path = context.path

    let sourceState: GalleryViewState? = if let pending = storage
      .takePendingState(path) as? GalleryViewState {
      pending
    } else {
      storage.getState(path)
    }

    let index: CGFloat
    let scrollRange: CGFloat?
    let animated: Bool
    if let sourceState {
      switch sourceState.contentPosition {
      case .offset:
        storage.setState(path: path, state: sourceState)
        return sourceState
      case let .paging(savedIndex):
        index = savedIndex
      }
      scrollRange = sourceState.scrollRange
      animated = sourceState.animated
    } else {
      index = CGFloat(resolveDefaultItem(context.expressionResolver))
      if index == 0 {
        let newState = GalleryViewState(contentOffset: 0, itemsCount: itemsCount)
        storage.setState(path: path, state: newState)
        return newState
      }
      scrollRange = nil
      animated = true
    }

    let clampedIndex = index.clamp(0.0...CGFloat(max(itemsCount - 1, 0)))
    let newState = GalleryViewState(
      contentPosition: .paging(index: clampedIndex),
      itemsCount: itemsCount,
      isScrolling: false,
      scrollRange: scrollRange,
      animated: animated
    )
    storage.setState(path: path, state: newState)
    return newState
  }
}

extension DivGallery.Orientation {
  fileprivate var direction: ScrollDirection {
    switch self {
    case .horizontal:
      .horizontal
    case .vertical:
      .vertical
    }
  }
}

extension DivGallery.ScrollMode {
  fileprivate var blockScrollMode: GalleryViewModel.ScrollMode {
    switch self {
    case .default:
      .default
    case .paging:
      .autoPaging(inertionEnabled: true)
    }
  }
}

extension DivGallery.CrossContentAlignment {
  fileprivate var blockAlignment: Alignment {
    switch self {
    case .start: .leading
    case .center: .center
    case .end: .trailing
    }
  }
}

extension DivGallery.Scrollbar {
  fileprivate var blockScrollbar: GalleryViewModel.Scrollbar {
    switch self {
    case .none:
      .none
    case .auto:
      .auto
    }
  }
}
