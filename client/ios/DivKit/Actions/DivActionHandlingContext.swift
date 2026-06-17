import LayoutKit

public struct DivActionHandlingContext {
  public let info: DivActionInfo
  public let expressionResolver: ExpressionResolver

  let actionHandler: DivActionHandler
  let blockStateStorage: DivBlockStateStorage
  let variablesStorage: DivVariablesStorage
  let updateCard: DivActionHandler.UpdateCardAction
  let sourcePath: UIElementPath
  var scopePath: UIElementPath?

  public var cardId: DivCardID {
    info.cardId
  }

  var path: UIElementPath {
    scopePath ?? info.path
  }
}
