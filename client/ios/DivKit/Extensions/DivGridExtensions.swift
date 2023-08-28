import CommonCorePublic
import LayoutKit

extension DivGrid: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actions: makeActions(context: context),
      actionAnimation: actionAnimation.makeActionAnimation(with: context.expressionResolver),
      doubleTapActions: makeDoubleTapActions(context: context),
      longTapActions: makeLongTapActions(context: context)
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let gridPath = context.parentPath + DivGrid.type
    let gridContext = modified(context) {
      $0.parentPath = gridPath
    }
    let gridItemsContext = modified(gridContext) {
      $0.errorsStorage = DivErrorsStorage(errors: [])
    }
    let gridItems = items.enumerated().compactMap { tuple in
      let itemContext = modified(gridItemsContext) {
        $0.parentPath = $0.parentPath + tuple.offset
      }
      do {
        return try tuple.element.value.makeGridItem(
          context: itemContext
        )
      } catch {
        itemContext.addError(error: error)
        return nil
      }
    }
    if items.count != gridItems.count {
      throw DivBlockModelingError("Unable to form grid", path: gridPath, causes: gridItemsContext.errorsStorage.errors)
    }

    let expressionResolver = gridContext.expressionResolver
    return try modifyError({ DivBlockModelingError($0.message, path: gridPath) }) {
      try GridBlock(
        widthTrait: makeContentWidthTrait(with: gridContext),
        heightTrait: makeContentHeightTrait(with: gridContext),
        contentAlignment: contentAlignment(with: expressionResolver),
        items: gridItems,
        columnCount: resolveColumnCount(expressionResolver) ?? 0
      )
    }
  }

  private func contentAlignment(with expressionResolver: ExpressionResolver) -> BlockAlignment2D {
    .init(
      horizontal: resolveContentAlignmentHorizontal(expressionResolver).alignment,
      vertical: resolveContentAlignmentVertical(expressionResolver).alignment
    )
  }
}

extension DivBase {
  fileprivate func makeGridItem(
    context: DivBlockModelingContext
  ) throws -> GridBlock.Item {
    let block = try makeBlock(context: context)
    let expressionResolver = context.expressionResolver
    return .init(
      span: GridBlock.Span(
        rows: resolveRowSpan(expressionResolver) ?? 1,
        columns: resolveColumnSpan(expressionResolver) ?? 1
      ),
      weight: .init(
        column: width.makeWeight(with: expressionResolver),
        row: height.makeWeight(with: expressionResolver)
      ),
      contents: block,
      alignment: alignment2D(withDefault: .default, context: context)
    )
  }
}

extension DivSize {
  fileprivate func makeWeight(with expressionResolver: ExpressionResolver) -> LayoutTrait.Weight? {
    switch self {
    case .divFixedSize, .divWrapContentSize:
      return nil
    case let .divMatchParentSize(size):
      return size.resolveWeight(expressionResolver)
        .flatMap { LayoutTrait.Weight(floatLiteral: $0) }
    }
  }
}
