import LayoutKit
import VGSL

extension DivGrid: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let context = modifiedContextParentPath(context)
    return try modifyError({ DivBlockModelingError($0.message, path: context.path) }) {
      try applyBaseProperties(
        to: { try makeBaseBlock(context: context) },
        context: context,
        actionsHolder: self
      )
    }
  }

  var nonNilItems: [Div] {
    items ?? []
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let path = context.path
    let itemsContext = context.modifying(errorsStorage: DivErrorsStorage(errors: []))
    let items = nonNilItems.enumerated().compactMap { tuple in
      let itemContext = itemsContext.modifying(
        pathSuffix: String(tuple.offset)
      )
      do {
        return try tuple.element.value.makeGridItem(context: itemContext)
      } catch {
        itemContext.addError(error: error)
        return nil
      }
    }

    if nonNilItems.count != items.count {
      throw DivBlockModelingError(
        "Unable to form grid",
        path: path,
        causes: itemsContext.errorsStorage.errors
      )
    }

    if items.isEmpty {
      return EmptyBlock(
        widthTrait: resolveContentWidthTrait(context),
        heightTrait: resolveContentHeightTrait(context)
      )
    }

    let expressionResolver = context.expressionResolver
    return try GridBlock(
      widthTrait: resolveContentWidthTrait(context),
      heightTrait: resolveContentHeightTrait(context),
      contentAlignment: resolveContentAlignment(
        expressionResolver,
        isRTLLayout: context.layoutDirection == .rightToLeft
      ),
      items: items,
      columnCount: resolveColumnCount(expressionResolver) ?? 0,
      path: path
    )
  }

  private func resolveContentAlignment(
    _ expressionResolver: ExpressionResolver,
    isRTLLayout: Bool
  ) -> BlockAlignment2D {
    BlockAlignment2D(
      horizontal: resolveContentAlignmentHorizontal(expressionResolver).alignment(isRTLLayout: isRTLLayout),
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
      nil
    case let .divMatchParentSize(size):
      size.resolveWeight(expressionResolver)
        .flatMap { LayoutTrait.Weight(floatLiteral: $0) }
    }
  }
}
