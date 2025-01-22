import Foundation
import LayoutKit
import VGSL

final class DebugBlock: WrapperBlock, LayoutCachingDefaultImpl {
  
  let child: Block

  let errorCollector: DebugErrorCollector
  let showDebugInfo: (ViewType) -> Void

  init(
    child: Block,
    errorCollector: DebugErrorCollector,
    showDebugInfo: @escaping (ViewType) -> Void
  ) {
    self.child = child
    self.errorCollector = errorCollector
    self.showDebugInfo = showDebugInfo
  }

  func makeCopy(wrapping block: Block) -> DebugBlock {
    DebugBlock(
      child: block,
      errorCollector: errorCollector,
      showDebugInfo: showDebugInfo
    )
  }
  
  func equals(_ other: any LayoutKit.Block) -> Bool {
    if self === other { return true }
    guard let other = other as? DebugBlock else { return false }
    return errorCollector === other.errorCollector && child.equals(other.child)
  }

  var debugDescription: String {
    "DebugBlock errors: \(errorCollector.debugDescription). Child: \(child)"
  }

  func getImageHolders() -> [any VGSLUI.ImageHolder] {
    []
  }

  func updated(withStates _: LayoutKit.BlocksState) throws -> Self {
    self
  }
}
