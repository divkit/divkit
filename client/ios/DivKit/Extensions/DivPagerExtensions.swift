import CoreGraphics

import BaseUIPublic
import CommonCorePublic
import LayoutKit

extension DivPager: DivBlockModeling, DivGalleryProtocol {
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
    let pagerPath = context.parentPath + (id ?? DivPager.type)
    let expressionResolver = context.expressionResolver
    let itemContext = modified(context) {
      $0.parentPath = pagerPath
      $0.galleryResizableInsets = nil
    }
    let pagerModelPath = id.map {
      PagerPath(
        cardId: context.cardId.rawValue,
        pagerId: $0
      )
    }
    let gallery = try makeGalleryModel(
      context: itemContext,
      direction: resolveOrientation(expressionResolver).direction,
      spacing: CGFloat(itemSpacing.resolveValue(expressionResolver) ?? 0),
      crossSpacing: 0,
      defaultAlignment: .center,
      resizableInsets: context.galleryResizableInsets,
      scrollMode: .autoPaging
    )
    let width = context.override(width: width)
    let height = context.override(height: height)
    return try PagerBlock(
      pagerPath: pagerModelPath,
      layoutMode: layoutMode.cast(with: expressionResolver),
      gallery: gallery,
      selectedActions: items.map { $0.value.makeSelectedActions(context: itemContext) },
      state: getState(context: context, path: pagerPath, numberOfPages: gallery.items.count),
      widthTrait: width.makeLayoutTrait(with: expressionResolver),
      heightTrait: height.makeLayoutTrait(with: expressionResolver)
    )
  }

  private func getState(
    context: DivBlockModelingContext,
    path: UIElementPath,
    numberOfPages: Int
  ) -> PagerViewState {
    if let state: PagerViewState = context.blockStateStorage.getState(path) {
      return state
    }

    let defaultItem = resolveDefaultItem(context.expressionResolver)
    return defaultItem == 0 ? .default : PagerViewState(
      numberOfPages: numberOfPages,
      currentPage: defaultItem
    )
  }
}

extension DivPager.Orientation {
  var direction: GalleryViewModel.Direction {
    switch self {
    case .horizontal: return .horizontal
    case .vertical: return .vertical
    }
  }
}

extension DivPagerLayoutMode {
  func cast(with expressionResolver: ExpressionResolver) -> PagerBlock.LayoutMode {
    switch self {
    case let .divPageSize(pageSize):
      return .pageSize(
        RelativeValue(
          double: (pageSize.pageWidth.resolveValue(expressionResolver) ?? 0) / 100.0
        )
      )
    case let .divNeighbourPageSize(neighbourPageSize):
      let size = neighbourPageSize.neighbourPageWidth.resolveValue(expressionResolver) ?? 0
      return .neighbourPageSize(CGFloat(size))
    }
  }
}

extension DivBase {
  fileprivate func makeSelectedActions(
    context: DivBlockModelingContext
  ) -> [UserInterfaceAction] {
    selectedActions?.map { $0.uiAction(context: context.actionContext) } ?? []
  }
}
