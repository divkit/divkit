import CommonCorePublic
import LayoutKit

extension DivCollectionItemBuilder {
  func makeBlocks<T>(
    context: DivBlockModelingContext,
    mappedBy modificator: (Div, Block, DivBlockModelingContext) -> T
  ) -> [T] {
    let items = resolveData(context.expressionResolver) ?? []
    return items.iterativeFlatMap { item, index in
      let path = context.parentPath + index
      let item = (item as? DivDictionary) ?? [:]
      do {
        return try modifyError({
          DivBlockModelingError($0.message, path: path)
        }) {
          let itemContext = context.modifying(
            parentPath: path,
            prototypeParams: PrototypeParams(
              index: index,
              variableName: dataElementName,
              value: item
            )
          )
          let prototype = prototypes
            .first { $0.resolveSelector(itemContext.expressionResolver) }
          guard let prototype else {
            return nil
          }
          let block = try prototype.div.value.makeBlock(context: itemContext)
          return modificator(prototype.div, block, itemContext)
        }
      } catch {
        context.addError(error: error)
        return nil
      }
    }
  }
}
