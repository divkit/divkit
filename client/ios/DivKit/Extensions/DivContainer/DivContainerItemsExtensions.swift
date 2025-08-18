import LayoutKit
import VGSL

extension DivContainer {
  func makeChildren<T>(
    context: DivBlockModelingContext,
    mappedBy modificator: (Div, Block, DivBlockModelingContext) -> T
  ) -> [T] {
    if let itemBuilder {
      return itemBuilder.makeBlocks(context: context, mappedBy: modificator)
    }

    let expressionResolver = context.expressionResolver
    let orientation = resolveOrientation(expressionResolver)
    let filtredItems = nonNilItems.filter {
      guard resolveLayoutMode(expressionResolver) == .wrap else {
        return true
      }
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

    return filtredItems.makeBlocks(
      context: context,
      sizeModifier: DivContainerSizeModifier(
        context: context,
        container: self,
        orientation: orientation
      ),
      mappedBy: modificator
    )
  }
}
