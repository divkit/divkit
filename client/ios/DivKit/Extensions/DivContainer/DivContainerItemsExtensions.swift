import LayoutKit
import VGSL

extension DivContainer {
  func makeChildren<T>(
    context: DivBlockModelingContext,
    mappedBy modificator: (Div, Block, DivBlockModelingContext) -> T
  ) -> [T] {
    let expressionResolver = context.expressionResolver
    let orientation = resolveOrientation(expressionResolver)
    let layoutMode = resolveLayoutMode(expressionResolver)

    if let itemBuilder {
      let divAndContexts = itemBuilder.makeItemDivAndContexts(context: context)
      // The modifier decisions are made over the unfiltered item list, matching the static
      // items path below.
      let sizeModifier = DivContainerSizeModifier(
        context: context,
        container: self,
        items: divAndContexts.map(\.0),
        orientation: orientation
      )
      return divAndContexts
        .filter { div, _ in
          isValidLayoutModeItem(
            div,
            orientation: orientation,
            layoutMode: layoutMode,
            context: context
          )
        }
        .compactMap { div, itemContext in
          // modifying() resets overridenId unless it is passed explicitly, so carry it over.
          let itemContext = itemContext.modifying(
            overridenId: itemContext.overridenId,
            sizeModifier: sizeModifier
          )
          do {
            return try modifyError({
              DivBlockModelingError($0.message, path: itemContext.path)
            }) {
              let block = try div.value.makeBlock(context: itemContext)
              return modificator(div, block, itemContext)
            }
          } catch {
            context.addError(error: error)
            return nil
          }
        }
    }

    let filtredItems = nonNilItems.filter {
      isValidLayoutModeItem(
        $0,
        orientation: orientation,
        layoutMode: layoutMode,
        context: context
      )
    }

    return filtredItems.makeBlocks(
      context: context.modifying(
        parentVisibility: resolveVisibility(context.expressionResolver)
      ),
      sizeModifier: DivContainerSizeModifier(
        context: context,
        container: self,
        items: nonNilItems,
        orientation: orientation
      ),
      mappedBy: modificator
    )
  }

  private func isValidLayoutModeItem(
    _ div: Div,
    orientation: DivContainer.Orientation,
    layoutMode: DivContainer.LayoutMode,
    context: DivBlockModelingContext
  ) -> Bool {
    guard layoutMode == .wrap else {
      return true
    }
    if orientation == .vertical, div.isHorizontallyMatchParent {
      context.addWarning(
        message: "Vertical DivContainer with wrap layout mode contains item with match_parent width"
      )
      return false
    }
    if orientation == .horizontal, div.isVerticallyMatchParent {
      context.addWarning(
        message: "Horizontal DivContainer with wrap layout mode contains item with match_parent height"
      )
      return false
    }
    return true
  }
}
