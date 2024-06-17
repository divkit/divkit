import CommonCorePublic
import LayoutKit

extension DivCollectionItemBuilder {
  func makeBlocks<T>(
    context: DivBlockModelingContext,
    mappedBy modificator: (Div, Block, DivBlockModelingContext) -> T
  ) -> [T] {
    let items = resolveData(context.expressionResolver) ?? []
    return items.enumerated().compactMap { index, item in
      let prototypeContext = context.modifying(
        prototypeParams: PrototypeParams(
          index: index,
          variableName: dataElementName,
          value: (item as? DivDictionary) ?? [:]
        )
      )
      let prototype = prototypes
        .first { $0.resolveSelector(prototypeContext.expressionResolver) }
      guard let prototype else {
        return nil
      }

      let div = prototype.div
      let id = prototype.resolveId(prototypeContext.expressionResolver)
      let itemContext = prototypeContext.modifying(
        elementId: id,
        parentPath: context.parentPath + (id ?? div.id ?? String(index))
      )
      do {
        return try modifyError({
          DivBlockModelingError($0.message, path: itemContext.parentPath)
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
}
