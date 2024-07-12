import CoreGraphics

import LayoutKit
import VGSL

extension DivGallery: DivBlockModeling, DivGalleryProtocol {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let path = context.parentPath + (id ?? DivGallery.type)
    let galleryContext = context.modifying(parentPath: path)
    return try modifyError({ DivBlockModelingError($0.message, path: path) }) {
      try applyBaseProperties(
        to: { try makeBaseBlock(context: galleryContext) },
        context: galleryContext,
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
      spacing: CGFloat(itemSpacing),
      crossSpacing: CGFloat(resolveCrossSpacing(expressionResolver) ?? itemSpacing),
      defaultAlignment: resolveCrossContentAlignment(expressionResolver).blockAlignment,
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
    let path = context.parentPath
    let index: CGFloat
    let scrollRange: CGFloat?
    if let state: GalleryViewState = context.blockStateStorage.getState(path) {
      switch state.contentPosition {
      case .offset:
        return state
      case let .paging(savedIndex):
        index = savedIndex
      }
      scrollRange = state.scrollRange
    } else {
      index = CGFloat(resolveDefaultItem(context.expressionResolver))
      if index == 0 {
        let newState = GalleryViewState(contentOffset: 0, itemsCount: itemsCount)
        context.blockStateStorage.setState(path: path, state: newState)
        return newState
      }
      scrollRange = nil
    }

    let newState = GalleryViewState(
      contentPosition: .paging(index: index.clamp(0.0...CGFloat(itemsCount - 1))),
      itemsCount: itemsCount,
      isScrolling: false,
      scrollRange: scrollRange
    )
    context.blockStateStorage.setState(path: path, state: newState)
    return newState
  }
}

extension DivGallery.Orientation {
  fileprivate var direction: GalleryViewModel.Direction {
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
