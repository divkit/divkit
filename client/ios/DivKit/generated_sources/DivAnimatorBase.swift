// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public protocol DivAnimatorBase {
  var cancelActions: [DivAction]? { get }
  var direction: Expression<DivAnimationDirection> { get } // default value: normal
  var duration: Expression<Int> { get } // constraint: number >= 0
  var endActions: [DivAction]? { get }
  var id: String { get }
  var interpolator: Expression<DivAnimationInterpolator> { get } // default value: linear
  var repeatCount: DivCount { get } // default value: .divFixedCount(DivFixedCount(value: .value(1)))
  var startDelay: Expression<Int> { get } // constraint: number >= 0; default value: 0
  var variableName: String { get }
  func resolveDirection(_ resolver: ExpressionResolver) -> DivAnimationDirection
  func resolveDuration(_ resolver: ExpressionResolver) -> Int?
  func resolveInterpolator(_ resolver: ExpressionResolver) -> DivAnimationInterpolator
  func resolveStartDelay(_ resolver: ExpressionResolver) -> Int
}
