import CoreGraphics

import BaseUIPublic
import CommonCorePublic
import LayoutKit

extension DivGallery: DivBlockModeling, DivGalleryProtocol {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actions: nil,
      actionAnimation: nil,
      doubleTapActions: nil,
      longTapActions: nil,
      options: .noPaddings
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let galleryPath = context.parentPath + (id ?? DivGallery.type)
    let expressionResolver = context.expressionResolver
    let galleryContext = modified(context) {
      $0.parentPath = galleryPath
      $0.galleryResizableInsets = nil
    }
    let defaultAlignment = resolveCrossContentAlignment(expressionResolver)
      .blockAlignment
    let itemSpacing = resolveItemSpacing(expressionResolver)
    let model = try makeGalleryModel(
      context: galleryContext,
      direction: resolveOrientation(expressionResolver).direction,
      spacing: CGFloat(itemSpacing),
      crossSpacing: CGFloat(resolveCrossSpacing(expressionResolver) ?? itemSpacing),
      defaultAlignment: defaultAlignment,
      resizableInsets: context.galleryResizableInsets,
      scrollMode: resolveScrollMode(expressionResolver).blockScrollMode,
      columnCount: resolveColumnCount(expressionResolver)
    )

    let width = context.override(width: width)
    let height = context.override(height: height)

    return try GalleryBlock(
      model: model,
      state: getState(context: galleryContext, itemsCount: model.items.count),
      widthTrait: width.makeLayoutTrait(with: expressionResolver),
      // horizontal paddings are managed by gallery internally
      heightTrait: height.makeLayoutTrait(with: expressionResolver)
    )
  }

  private func getState(
    context: DivBlockModelingContext,
    itemsCount: Int
  ) -> GalleryViewState {
    let path = context.parentPath
    let index: CGFloat
    if let state: GalleryViewState = context.blockStateStorage.getState(path) {
      switch state.contentPosition {
      case .offset:
        return state
      case let .paging(savedIndex):
        index = savedIndex
      }
    } else {
      index = CGFloat(resolveDefaultItem(context.expressionResolver))
      if index == 0 {
        return GalleryViewState(contentOffset: 0, itemsCount: itemsCount)
      }
    }

    let newState = GalleryViewState(
      contentPageIndex: index.clamp(0.0...CGFloat(itemsCount - 1)),
      itemsCount: itemsCount
    )
    context.blockStateStorage.setState(path: path, state: newState)
    return newState
  }
}

extension DivGallery.Orientation {
  fileprivate var direction: GalleryViewModel.Direction {
    switch self {
    case .horizontal:
      return .horizontal
    case .vertical:
      return .vertical
    }
  }
}

extension DivGallery.ScrollMode {
  fileprivate var blockScrollMode: GalleryViewModel.ScrollMode {
    switch self {
    case .default:
      return .default
    case .paging:
      return .autoPaging
    }
  }
}

extension DivGallery.CrossContentAlignment {
  fileprivate var blockAlignment: Alignment {
    switch self {
    case .start: return .leading
    case .center: return .center
    case .end: return .trailing
    }
  }
}
