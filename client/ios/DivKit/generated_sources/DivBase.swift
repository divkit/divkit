// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public protocol DivBase: DivBlockModeling {
  var accessibility: DivAccessibility? { get }
  var alignmentHorizontal: Expression<DivAlignmentHorizontal>? { get }
  var alignmentVertical: Expression<DivAlignmentVertical>? { get }
  var alpha: Expression<Double> { get } // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  var animators: [DivAnimator]? { get }
  var background: [DivBackground]? { get }
  var border: DivBorder? { get }
  var columnSpan: Expression<Int>? { get } // constraint: number >= 0
  var disappearActions: [DivDisappearAction]? { get }
  var extensions: [DivExtension]? { get }
  var focus: DivFocus? { get }
  var functions: [DivFunction]? { get }
  var height: DivSize { get } // default value: .divWrapContentSize(DivWrapContentSize())
  var id: String? { get }
  var layoutProvider: DivLayoutProvider? { get }
  var margins: DivEdgeInsets? { get }
  var paddings: DivEdgeInsets? { get }
  var reuseId: Expression<String>? { get }
  var rowSpan: Expression<Int>? { get } // constraint: number >= 0
  var selectedActions: [DivAction]? { get }
  var tooltips: [DivTooltip]? { get }
  var transform: DivTransform? { get }
  var transitionChange: DivChangeTransition? { get }
  var transitionIn: DivAppearanceTransition? { get }
  var transitionOut: DivAppearanceTransition? { get }
  var transitionTriggers: [DivTransitionTrigger]? { get } // at least 1 elements
  var variableTriggers: [DivTrigger]? { get }
  var variables: [DivVariable]? { get }
  var visibility: Expression<DivVisibility> { get } // default value: visible
  var visibilityAction: DivVisibilityAction? { get }
  var visibilityActions: [DivVisibilityAction]? { get }
  var width: DivSize { get } // default value: .divMatchParentSize(DivMatchParentSize())
  func resolveAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal?
  func resolveAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical?
  func resolveAlpha(_ resolver: ExpressionResolver) -> Double
  func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int?
  func resolveReuseId(_ resolver: ExpressionResolver) -> String?
  func resolveRowSpan(_ resolver: ExpressionResolver) -> Int?
  func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility
}
