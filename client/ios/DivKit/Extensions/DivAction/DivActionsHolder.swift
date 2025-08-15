import LayoutKit
import VGSL

protocol DivActionsHolder {
  var action: DivAction? { get }
  var actions: [DivAction]? { get }
  var actionAnimation: DivAnimation { get }
  var doubletapActions: [DivAction]? { get }
  var longtapActions: [DivAction]? { get }
  var pressStartActions: [DivAction]? { get }
  var pressEndActions: [DivAction]? { get }
  var hoverStartActions: [DivAction]? { get }
  var hoverEndActions: [DivAction]? { get }

  func resolveCaptureFocusOnAction(_ resolver: ExpressionResolver) -> Bool
}

extension DivText: DivActionsHolder {}
extension DivImage: DivActionsHolder {}
extension DivSeparator: DivActionsHolder {}
extension DivGifImage: DivActionsHolder {}
extension DivContainer: DivActionsHolder {}
extension DivGrid: DivActionsHolder {}
extension DivState: DivActionsHolder {}

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
    let doubletapActions = actionsHolder.doubletapActions?.nonEmptyUIActions(context: context)
    let longtapActions = actionsHolder.longtapActions?.nonEmptyUIActions(context: context)
      .map(LongTapActions.actions)
    let pressStartActions = actionsHolder.pressStartActions?.nonEmptyUIActions(context: context)
    let pressEndActions = actionsHolder.pressEndActions?.nonEmptyUIActions(context: context)
    let hoverStartActions = actionsHolder.hoverStartActions?.nonEmptyUIActions(context: context)
    let hoverEndActions = actionsHolder.hoverEndActions?.nonEmptyUIActions(context: context)

    if actions == nil, doubletapActions == nil, longtapActions == nil,
       pressStartActions == nil, pressEndActions == nil,
       hoverStartActions == nil, hoverEndActions == nil {
      return self
    }

    return addingDecorations(
      boundary: clipToBounds ? nil : .noClip,
      actions: actions,
      actionAnimation: actionsHolder.actionAnimation
        .resolveActionAnimation(context.expressionResolver),
      doubleTapActions: doubletapActions,
      longTapActions: longtapActions,
      pressStartActions: pressStartActions,
      pressEndActions: pressEndActions,
      hoverStartActions: hoverStartActions,
      hoverEndActions: hoverEndActions,
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
