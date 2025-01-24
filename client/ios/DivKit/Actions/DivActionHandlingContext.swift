import LayoutKit

struct DivActionHandlingContext {
  let info: DivActionInfo
  let expressionResolver: ExpressionResolver
  let variablesStorage: DivVariablesStorage
  let blockStateStorage: DivBlockStateStorage
  let actionHandler: DivActionHandler
  let updateCard: DivActionHandler.UpdateCardAction

  var cardId: DivCardID {
    info.cardId
  }

  var path: UIElementPath {
    info.path
  }
}
