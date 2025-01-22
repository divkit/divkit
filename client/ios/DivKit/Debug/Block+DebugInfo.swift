import CoreGraphics
import Foundation
import LayoutKit
import VGSL

extension Block {
  func addingDebugInfo(context: DivBlockModelingContext) -> Block {
    let debugParams = context.debugParams
    guard debugParams.isDebugInfoEnabled,
          let debugErrorCollector = context.debugErrorCollector else {
      return self
    }

    let debugBlock = DebugBlock(
      errorCollector: debugErrorCollector,
      showDebugInfo: debugParams.showDebugInfo
    )

    let block = LayeredBlock(
      widthTrait: calculatedWidthTrait,
      heightTrait: calculatedHeightTrait,
      verticalChildrenAlignment: .center,
      children: [self, debugBlock]
    )

    return block
  }
}
