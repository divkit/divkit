import CommonCore
import LayoutKit

extension Array where Element == Div {
  func makeBlocks<T>(
    context: DivBlockModelingContext,
    mappedBy modificator: (Div, Block) throws -> T
  ) throws -> [T] {
    try iterativeFlatMap { div, index in
      let itemContext = modified(context) { $0.parentPath += index }
      let block: Block
      do {
        block = try div.value.makeBlock(context: itemContext)
      } catch {
        DivKitLogger.error("Failed to create block: \(error)")
        return nil
      }
      return try modificator(div, block)
    }
  }

  func makeBlocks(context: DivBlockModelingContext) throws -> [Block] {
    try makeBlocks(context: context, mappedBy: { $1 })
  }
}
