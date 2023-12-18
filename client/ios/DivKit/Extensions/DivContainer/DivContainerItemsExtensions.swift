import CommonCorePublic
import LayoutKit

enum DivContainerDataSource {
  case divs([Div])
  case itemBuilder(DivCollectionItemBuilder)
}

extension DivContainer {
  private var dataSource: DivContainerDataSource? {
    if let itemBuilder {
      return .itemBuilder(itemBuilder)
    }
    if let items {
      return .divs(items)
    }
    return nil
  }

  func makeChildren<T>(
    context: DivBlockModelingContext,
    mappedBy modificator: (Div, Block, DivBlockModelingContext) throws -> T
  ) throws -> [T] {
    guard let dataSource else {
      throw DivBlockModelingError(
        "DivContainer has no items and itemBuilder property",
        path: context.parentPath
      )
    }
    
    switch dataSource {
    case let .divs(items):
      let expressionResolver = context.expressionResolver
      let orientation = resolveOrientation(expressionResolver)
      let filtredItems = items.filter {
        guard resolveLayoutMode(expressionResolver) == .wrap else { return true }
        if orientation == .vertical, $0.isHorizontallyMatchParent {
          context.addWarning(
            message: "Vertical DivContainer with wrap layout mode contains item with match_parent width"
          )
          return false
        }
        if orientation == .horizontal, $0.isVerticallyMatchParent {
          context.addWarning(
            message: "Horizontal DivContainer with wrap layout mode contains item with match_parent height"
          )
          return false
        }
        return true
      }

      return try filtredItems.makeBlocks(
        context: context,
        sizeModifier: DivContainerSizeModifier(
          context: context,
          container: self,
          orientation: orientation
        ),
        mappedBy: modificator
      )
    case let .itemBuilder(itemBuilder):
      return try itemBuilder.makeBlocks(
        context: context,
        mappedBy: modificator
      )
    }
  }
}
