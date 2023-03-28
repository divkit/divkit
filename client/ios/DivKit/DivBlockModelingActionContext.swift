import BaseUIPublic
import CommonCorePublic
import LayoutKit

struct DivBlockModelingActionContext {
  let cardId: DivCardID
  let cardLogId: String?
  let expressionResolver: ExpressionResolver

  init(
    cardId: DivCardID,
    cardLogId: String?,
    expressionResolver: ExpressionResolver
  ) {
    self.cardId = cardId
    self.cardLogId = cardLogId
    self.expressionResolver = expressionResolver
  }
}

extension DivBlockModelingContext {
  var actionContext: DivBlockModelingActionContext {
    DivBlockModelingActionContext(
      cardId: cardId,
      cardLogId: cardLogId,
      expressionResolver: expressionResolver
    )
  }
}
