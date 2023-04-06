// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public protocol DivBase: DivBlockModeling {
  var accessibility: DivAccessibility { get }
  var alignmentHorizontal: Expression<DivAlignmentHorizontal>? { get }
  var alignmentVertical: Expression<DivAlignmentVertical>? { get }
  var alpha: Expression<Double> { get } // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  var background: [DivBackground]? { get } // at least 1 elements
  var border: DivBorder { get }
  var columnSpan: Expression<Int>? { get } // constraint: number >= 0
  var extensions: [DivExtension]? { get } // at least 1 elements
  var focus: DivFocus? { get }
  var height: DivSize { get } // default value: .divWrapContentSize(DivWrapContentSize())
  var id: String? { get } // at least 1 char
  var margins: DivEdgeInsets { get }
  var paddings: DivEdgeInsets { get }
  var rowSpan: Expression<Int>? { get } // constraint: number >= 0
  var selectedActions: [DivAction]? { get } // at least 1 elements
  var tooltips: [DivTooltip]? { get } // at least 1 elements
  var transform: DivTransform { get }
  var transitionChange: DivChangeTransition? { get }
  var transitionIn: DivAppearanceTransition? { get }
  var transitionOut: DivAppearanceTransition? { get }
  var transitionTriggers: [DivTransitionTrigger]? { get } // at least 1 elements
  var visibility: Expression<DivVisibility> { get } // default value: visible
  var visibilityAction: DivVisibilityAction? { get }
  var visibilityActions: [DivVisibilityAction]? { get } // at least 1 elements
  var width: DivSize { get } // default value: .divMatchParentSize(DivMatchParentSize())
  func resolveAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal?
  func resolveAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical?
  func resolveAlpha(_ resolver: ExpressionResolver) -> Double
  func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int?
  func resolveRowSpan(_ resolver: ExpressionResolver) -> Int?
  func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility
}
