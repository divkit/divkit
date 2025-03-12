import LayoutKit
import VGSL

extension [Div] {
  func makeBlocks<T>(
    context: DivBlockModelingContext,
    sizeModifier: DivSizeModifier? = nil,
    mappedBy modificator: (Div, Block, DivBlockModelingContext) -> T
  ) -> [T] {
    iterativeFlatMap { div, index in
      let itemContext = context.modifying(
        parentPath: context.parentPath + index,
        sizeModifier: sizeModifier
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

  func makeBlocks(context: DivBlockModelingContext) -> [Block] {
    makeBlocks(context: context, mappedBy: { _, block, _ in block })
  }
}

extension Array where Element: Hashable {
  var nonUniqueElements: [Element] {
    countElements()
      .filter { $0.value > 1 }
      .map(\.key)
  }
}
