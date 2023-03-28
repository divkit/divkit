import CommonCorePublic
import LayoutKit

extension DivGrid: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actions: makeActions(context: context.actionContext),
      actionAnimation: actionAnimation.makeActionAnimation(with: context.expressionResolver),
      doubleTapActions: makeDoubleTapActions(context: context.actionContext),
      longTapActions: makeLongTapActions(context: context.actionContext)
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let gridItems = try items.enumerated().map { tuple in
      try tuple.element.value.makeGridItem(
        context: modified(context) {
          $0.parentPath = $0.parentPath + DivGrid.type + tuple.offset
        }
      )
    }
    let expressionResolver = context.expressionResolver
    return try GridBlock(
      widthTrait: makeContentWidthTrait(with: context),
      heightTrait: makeContentHeightTrait(with: context),
      contentAlignment: contentAlignment(with: expressionResolver),
      items: gridItems,
      columnCount: resolveColumnCount(expressionResolver) ?? 0,
      path: context.parentPath + DivGrid.type
    )
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
    return try .init(
      span: GridBlock.Span(
        rows: resolveRowSpan(context.expressionResolver) ?? 1,
        columns: resolveColumnSpan(context.expressionResolver) ?? 1
      ),
      weight: .init(
        column: width.makeWeight(with: context.expressionResolver),
        row: height.makeWeight(with: context.expressionResolver)
      ),
      contents: block,
      alignment: alignment2D(withDefault: .default, expressionResolver: context.expressionResolver)
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
