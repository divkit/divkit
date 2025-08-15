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

    return DebugBlock(
      child: self,
      errorCollector: debugErrorCollector,
      showDebugInfo: debugParams.showDebugInfo
    )
  }
}
