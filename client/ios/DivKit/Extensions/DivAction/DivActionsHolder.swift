import LayoutKit
import VGSL

protocol DivActionsHolder {
  var action: DivAction? { get }
  var actions: [DivAction]? { get }
  var actionAnimation: DivAnimation { get }
  var doubletapActions: [DivAction]? { get }
  var longtapActions: [DivAction]? { get }

  func resolveCaptureFocusOnAction(_ resolver: ExpressionResolver) -> Bool
}

extension DivText: DivActionsHolder {}
extension DivImage: DivActionsHolder {}
extension DivSeparator: DivActionsHolder {}
extension DivGifImage: DivActionsHolder {}
extension DivContainer: DivActionsHolder {}
extension DivGrid: DivActionsHolder {}

extension DivActionsHolder {
  fileprivate func makeActions(
    context: DivBlockModelingContext
  ) -> NonEmptyArray<UserInterfaceAction>? {
    let allActions = (actions ?? action.asArray()).uiActions(context: context)

    if let menuAction = allActions.first(where: { $0.payload.isMenu }) {
      // ignore other actions
      return NonEmptyArray(menuAction)
    }

    return NonEmptyArray(allActions)
  }

  fileprivate func makeDoubleTapActions(
    context: DivBlockModelingContext
  ) -> NonEmptyArray<UserInterfaceAction>? {
    if let actions = doubletapActions?.uiActions(context: context) {
      return NonEmptyArray(actions)
    }
    return nil
  }

  fileprivate func makeLongTapActions(
    context: DivBlockModelingContext
  ) -> LongTapActions? {
    if let actions = longtapActions?.uiActions(context: context) {
      return NonEmptyArray(actions).map(LongTapActions.actions)
    }
    return nil
  }
}

extension Block {
  func addActions(
    context: DivBlockModelingContext,
    actionsHolder: DivActionsHolder?,
    clipToBounds: Bool
  ) -> Block {
    guard let actionsHolder else {
      return self
    }

    let actions = actionsHolder.makeActions(context: context)
    let doubletapActions = actionsHolder.makeDoubleTapActions(context: context)
    let longtapActions = actionsHolder.makeLongTapActions(context: context)
    if actions == nil, doubletapActions == nil, longtapActions == nil {
      return self
    }

    return addingDecorations(
      boundary: clipToBounds ? nil : .noClip,
      actions: actions,
      actionAnimation: actionsHolder.actionAnimation
        .resolveActionAnimation(context.expressionResolver),
      doubleTapActions: doubletapActions,
      longTapActions: longtapActions,
      path: context.path,
      captureFocusOnAction: actionsHolder.resolveCaptureFocusOnAction(context.expressionResolver)
    )
  }
}

extension UserInterfaceAction.Payload {
  fileprivate var isMenu: Bool {
    if case .menu = self { return true }
    return false
  }
}
