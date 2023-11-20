import CommonCorePublic
import LayoutKit

extension DivGrid: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actionsHolder: self
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
      throw DivBlockModelingError(
        "Unable to form grid",
        path: gridPath,
        causes: gridItemsContext.errorsStorage.errors
      )
    }

    let expressionResolver = gridContext.expressionResolver
    return try modifyError({ DivBlockModelingError($0.message, path: gridPath) }) {
      try GridBlock(
        widthTrait: resolveContentWidthTrait(gridContext),
        heightTrait: resolveContentHeightTrait(gridContext),
        contentAlignment: resolveContentAlignment(expressionResolver),
        items: gridItems,
        columnCount: resolveColumnCount(expressionResolver) ?? 0
      )
    }
  }

  private func resolveContentAlignment(
    _ expressionResolver: ExpressionResolver
  ) -> BlockAlignment2D {
    BlockAlignment2D(
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
        column: width.resolveWeight(expressionResolver),
        row: height.resolveWeight(expressionResolver)
      ),
      contents: block,
      alignment: resolveAlignment(context, defaultAlignment: .default)
    )
  }
}

extension DivSize {
  fileprivate func resolveWeight(
    _ expressionResolver: ExpressionResolver
  ) -> LayoutTrait.Weight? {
    switch self {
    case .divFixedSize, .divWrapContentSize:
      return nil
    case let .divMatchParentSize(size):
      return size.resolveWeight(expressionResolver)
        .flatMap { LayoutTrait.Weight(floatLiteral: $0) }
    }
  }
}
