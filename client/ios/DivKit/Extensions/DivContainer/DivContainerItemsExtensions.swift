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
    containerContext: DivBlockModelingContext,
    mappedBy modificator: (Div, Block, DivBlockModelingContext) throws -> T
  ) throws -> [T] {
    guard let dataSource else {
      throw DivBlockModelingError(
        "DivContainer has no items and itemBuilder property",
        path: containerContext.parentPath,
        causes: containerContext.errorsStorage.errors
      )
    }
    let resolver = containerContext.expressionResolver
    let childrenContext = containerContext.modifying(errorsStorage: DivErrorsStorage(errors: []))
    let children: [T]
    switch dataSource {
    case let .divs(items):
      let orientation = resolveOrientation(resolver)
      let filtredItems = items.filter {
        guard resolveLayoutMode(resolver) == .wrap else { return true }
        if orientation == .vertical, $0.isHorizontallyMatchParent {
          childrenContext.addWarning(
            message: "Vertical DivContainer with wrap layout mode contains item with match_parent width"
          )
          return false
        }
        if orientation == .horizontal, $0.isVerticallyMatchParent {
          childrenContext.addWarning(
            message: "Horizontal DivContainer with wrap layout mode contains item with match_parent height"
          )
          return false
        }
        return true
      }

      children = try filtredItems.makeBlocks(
        context: childrenContext,
        sizeModifier: DivContainerSizeModifier(
          context: childrenContext,
          container: self,
          orientation: orientation
        ),
        mappedBy: modificator
      )
    case let .itemBuilder(itemBuilder):
      children = try itemBuilder.makeBlocks(
        context: childrenContext,
        mappedBy: modificator
      )
    }
    if children.isEmpty {
      throw DivBlockModelingError(
        "DivContainer is empty",
        path: containerContext.parentPath,
        causes: childrenContext.errorsStorage.errors
      )
    } else {
      containerContext.errorsStorage.add(contentsOf: childrenContext.errorsStorage)
    }
    return children
  }
}
