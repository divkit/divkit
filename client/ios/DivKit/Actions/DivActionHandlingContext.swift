import LayoutKit

public struct DivActionHandlingContext {
  public let info: DivActionInfo
  public let expressionResolver: ExpressionResolver

  let actionHandler: DivActionHandler
  let blockStateStorage: DivBlockStateStorage
  let variablesStorage: DivVariablesStorage
  let updateCard: DivActionHandler.UpdateCardAction
  /// Path the action was built with, before `scope_id` replaces `path`. The originating view
  /// is recovered from this path: a `tooltip#` segment names a tooltip, and its absence means
  /// the host card. `info.path` is the same path; `sourcePath` is the one that stays the
  /// source when `path` is rewritten to the scope element.
  let sourcePath: UIElementPath
  var scopePath: UIElementPath?

  public var cardId: DivCardID {
    info.cardId
  }

  var path: UIElementPath {
    scopePath ?? info.path
  }

  var sourceViewId: DivViewId {
    sourcePath.viewId
  }
}
