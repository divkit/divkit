import CommonCorePublic
import LayoutKit

extension Array where Element == Div {
  func makeBlocks<T>(
    context: DivBlockModelingContext,
    overridenWidth: DivOverridenSize? = nil,
    overridenHeight: DivOverridenSize? = nil,
    mappedBy modificator: (Div, Block) throws -> T
  ) rethrows -> [T] {
    try iterativeFlatMap { div, index in
      let itemContext = modified(context) {
        $0.parentPath += index
        $0.overridenWidth = overridenWidth
        $0.overridenHeight = overridenHeight
      }
      let block: Block
      do {
        block = try modifyError({ DivBlockModelingError($0.message.string, path: itemContext.parentPath) }) {
          try div.value.makeBlock(context: itemContext)
        }
      } catch {
        context.addError(error: error)
        return nil
      }
      return try modificator(div, block)
    }
  }

  func makeBlocks(context: DivBlockModelingContext) -> [Block] {
    makeBlocks(context: context, mappedBy: { $1 })
  }
}
