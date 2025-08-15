import Foundation
import LayoutKit
import VGSL

extension DivDisappearAction: DivVisibilityActionBase {
  var actionType: LayoutKit.VisibilityActionType {
    .disappear
  }

  func resolveRequiredDuration(_ resolver: ExpressionResolver) -> Int {
    resolveDisappearDuration(resolver)
  }
}
