// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public protocol DivTransitionBase {
  var duration: Expression<Int> { get } // constraint: number >= 0; default value: 200
  var interpolator: Expression<DivAnimationInterpolator> { get } // default value: ease_in_out
  var startDelay: Expression<Int> { get } // constraint: number >= 0; default value: 0
  func resolveDuration(_ resolver: ExpressionResolver) -> Int
  func resolveInterpolator(_ resolver: ExpressionResolver) -> DivAnimationInterpolator
  func resolveStartDelay(_ resolver: ExpressionResolver) -> Int
}
