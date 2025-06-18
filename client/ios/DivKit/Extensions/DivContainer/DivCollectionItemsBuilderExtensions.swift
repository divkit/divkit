import LayoutKit
import VGSL

extension DivCollectionItemBuilder {
  func makeBlocks<T>(
    context: DivBlockModelingContext,
    mappedBy modificator: (Div, Block, DivBlockModelingContext) -> T
  ) -> [T] {
    makeItemDivAndContexts(context: context).compactMap { (div, itemContext) in
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
  
  func makeItemDivAndContexts(
    context: DivBlockModelingContext
  ) -> [(Div, DivBlockModelingContext)] {
    let items = resolveData(context.expressionResolver) ?? []
    return items.enumerated().compactMap { index, item -> (Div, DivBlockModelingContext)? in
      let itemDict = item as? DivDictionary ?? [:]
      let prototypeContext = context.modifying(
        prototypeParams: PrototypeParams(
          index: index,
          variableName: dataElementName,
          value: itemDict
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
        overridenId: id,
        pathSuffix: String(index)
      )
      itemContext.variablesStorage.initializeIfNeeded(path: itemContext.path, variables: [
        DivVariableName(rawValue: dataElementName): .dict(itemDict),
        "index": .integer(index),
      ])
      return (div, itemContext)
    }
  }
}
