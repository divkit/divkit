import LayoutKit

struct DivActionHandlingContext {
  let path: UIElementPath
  let expressionResolver: ExpressionResolver
  let variablesStorage: DivVariablesStorage
  let blockStateStorage: DivBlockStateStorage
  let updateCard: DivActionURLHandler.UpdateCardAction
}
