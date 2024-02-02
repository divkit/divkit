struct DivActionHandlingContext {
  let cardId: DivCardID
  let expressionResolver: ExpressionResolver
  let variablesStorage: DivVariablesStorage
  let blockStateStorage: DivBlockStateStorage
  let updateCard: DivActionURLHandler.UpdateCardAction
}
