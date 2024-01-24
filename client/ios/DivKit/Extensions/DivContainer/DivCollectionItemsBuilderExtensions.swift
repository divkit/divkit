import CommonCorePublic
import LayoutKit

extension DivCollectionItemBuilder {
  func makeBlocks<T>(
    context: DivBlockModelingContext,
    sizeModifier: DivSizeModifier? = nil,
    mappedBy modificator: (Div, Block, DivBlockModelingContext) throws -> T
  ) rethrows -> [T] {
    let items = resolveData(context.expressionResolver) ?? []
    return items.iterativeFlatMap { item, index in
      let itemContext = context.modifying(
        parentPath: context.parentPath + index,
        sizeModifier: sizeModifier
      )
      let item = (item as? [String: AnyHashable]) ?? [:]
      do {
        return try modifyError({ DivBlockModelingError($0.message, path: itemContext.parentPath)
        }) {
          let prototypeContext = itemContext
            .modifying(prototypesData: (dataElementName, item))
          let prototype = prototypes
            .first { $0.resolveSelector(prototypeContext.expressionResolver) }
          guard let prototype else {
            return nil
          }
          let block = try prototype.div.value.makeBlock(context: prototypeContext)
          return try modificator(prototype.div, block, prototypeContext)
        }
      } catch {
        context.addError(error: error)
        return nil
      }
    }
  }
}
