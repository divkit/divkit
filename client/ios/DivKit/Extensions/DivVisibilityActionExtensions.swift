import Foundation
import LayoutKit
import VGSL

extension DivVisibilityAction: DivVisibilityActionBase {
  var actionType: LayoutKit.VisibilityActionType {
    .appear
  }

  func resolveRequiredDuration(_ resolver: ExpressionResolver) -> Int {
    resolveVisibilityDuration(resolver)
  }
}
