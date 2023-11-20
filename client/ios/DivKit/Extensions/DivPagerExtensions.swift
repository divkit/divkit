import CoreGraphics

import BaseUIPublic
import CommonCorePublic
import LayoutKit

extension DivPager: DivBlockModeling, DivGalleryProtocol {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actionsHolder: nil,
      options: .noPaddings
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let pagerPath = context.parentPath + (id ?? DivPager.type)
    let expressionResolver = context.expressionResolver
    let itemContext = modified(context) {
      $0.parentPath = pagerPath
    }
    let pagerModelPath = id.map {
      PagerPath(
        cardId: context.cardId.rawValue,
        pagerId: $0
      )
    }

    return try modifyError({ DivBlockModelingError($0.message, path: pagerPath) }) {
      let gallery = try makeGalleryModel(
        context: itemContext,
        direction: resolveOrientation(expressionResolver).direction,
        spacing: CGFloat(itemSpacing.resolveValue(expressionResolver) ?? 0),
        crossSpacing: 0,
        defaultAlignment: .center,
        scrollMode: .autoPaging,
        infiniteScroll: resolveInfiniteScroll(expressionResolver)
      )
      return try PagerBlock(
        pagerPath: pagerModelPath,
        layoutMode: layoutMode.resolve(expressionResolver),
        gallery: gallery,
        selectedActions: items.map { $0.value.makeSelectedActions(context: itemContext) },
        state: getState(context: context, path: pagerPath, numberOfPages: items.count),
        widthTrait: resolveWidthTrait(context),
        heightTrait: resolveHeightTrait(context)
      )
    }
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
  fileprivate func resolve(_ expressionResolver: ExpressionResolver) -> PagerBlock.LayoutMode {
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
    selectedActions?.uiActions(context: context) ?? []
  }
}
