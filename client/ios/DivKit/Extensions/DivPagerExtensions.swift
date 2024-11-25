import CoreGraphics

import LayoutKit
import VGSL

extension DivPager: DivBlockModeling, DivGalleryProtocol {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let path = context.parentPath + (id ?? DivPager.type)
    let pagerContext = context.modifying(parentPath: path)
    return try modifyError({ DivBlockModelingError($0.message, path: path) }) {
      try applyBaseProperties(
        to: { try makeBaseBlock(context: pagerContext) },
        context: pagerContext,
        actionsHolder: nil,
        applyPaddings: false
      )
    }
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let expressionResolver = context.expressionResolver
    let pagerPath = id.map {
      PagerPath(
        cardId: context.cardId.rawValue,
        pagerId: $0
      )
    }
    let items = nonNilItems
    let scrollDirection = resolveOrientation(expressionResolver).direction
    let gallery = try makeGalleryModel(
      context: context,
      direction: resolveOrientation(expressionResolver).direction,
      spacing: CGFloat(itemSpacing.resolveValue(expressionResolver) ?? 0),
      crossSpacing: 0,
      defaultAlignment: .center,
      scrollMode: .autoPaging(inertionEnabled: false),
      infiniteScroll: resolveInfiniteScroll(expressionResolver),
      transformation: pageTransformation?.resolve(
        expressionResolver,
        scrollDirection: scrollDirection
      )
    )
    return try PagerBlock(
      pagerPath: pagerPath,
      layoutMode: layoutMode.resolve(expressionResolver),
      gallery: gallery,
      selectedActions: items.map { $0.value.makeSelectedActions(context: context) },
      state: getState(context: context, path: context.parentPath, numberOfPages: items.count),
      widthTrait: resolveWidthTrait(context),
      heightTrait: resolveHeightTrait(context)
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
  var direction: ScrollDirection {
    switch self {
    case .horizontal: .horizontal
    case .vertical: .vertical
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

extension DivPageTransformation {
  fileprivate func resolve(
    _ resolver: ExpressionResolver,
    scrollDirection: ScrollDirection
  ) -> ElementsTransformation {
    switch self {
    case let .divPageTransformationSlide(transformation):
      .init(
        nextElementAlpha: transformation.resolveNextPageAlpha(resolver),
        previousElementAlpha: transformation.resolvePreviousPageAlpha(resolver),
        nextElementScale: transformation.resolveNextPageScale(resolver),
        previousElementScale: transformation.resolvePreviousPageScale(resolver),
        style: .slide,
        scrollDirection: scrollDirection
      )
    case let .divPageTransformationOverlap(transformation):
      .init(
        nextElementAlpha: transformation.resolveNextPageAlpha(resolver),
        previousElementAlpha: transformation.resolvePreviousPageAlpha(resolver),
        nextElementScale: transformation.resolveNextPageScale(resolver),
        previousElementScale: transformation.resolvePreviousPageScale(resolver),
        style: .overlap,
        scrollDirection: scrollDirection
      )
    }
  }
}
