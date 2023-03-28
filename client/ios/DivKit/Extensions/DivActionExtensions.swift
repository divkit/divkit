import CommonCorePublic
import LayoutKit

protocol ActionHolder {
  var action: DivAction? { get }
}

protocol ActionsHolder: ActionHolder {
  var actions: [DivAction]? { get }
}

protocol DoubleTapActionsHolder: ActionHolder {
  var doubletapActions: [DivAction]? { get }
}

protocol LongTapActionsHolder {
  var longtapActions: [DivAction]? { get }
}

extension DivTabs.Item: ActionHolder {
  var action: DivAction? { titleClickAction }
}

extension DivText: ActionsHolder, DoubleTapActionsHolder, LongTapActionsHolder {}
extension DivImage: ActionsHolder, DoubleTapActionsHolder, LongTapActionsHolder {}
extension DivSeparator: ActionsHolder, DoubleTapActionsHolder, LongTapActionsHolder {}
extension DivGifImage: ActionsHolder, DoubleTapActionsHolder, LongTapActionsHolder {}
extension DivContainer: ActionsHolder, DoubleTapActionsHolder, LongTapActionsHolder {}
extension DivGrid: ActionsHolder, DoubleTapActionsHolder, LongTapActionsHolder {}

extension DivText.Ellipsis: ActionsHolder {
  var action: DivAction? { nil }
}

extension DivText.Range: ActionsHolder {
  var action: DivAction? { nil }
}

extension ActionHolder {
  func makeAction(context: DivBlockModelingActionContext) -> UserInterfaceAction? {
    action?.uiAction(context: context)
  }
}

extension ActionsHolder {
  func makeActions(context: DivBlockModelingActionContext)
    -> NonEmptyArray<UserInterfaceAction>? {
    let allActions = (actions ?? action.asArray()).map {
      $0.uiAction(context: context)
    }

    if let menuAction = allActions.first(where: { $0.payload.isMenu }) {
      // ignore other actions
      return NonEmptyArray(menuAction)
    }

    return NonEmptyArray(allActions)
  }
}

extension LongTapActionsHolder {
  func makeLongTapActions(context: DivBlockModelingActionContext)
    -> NonEmptyArray<UserInterfaceAction>? {
    NonEmptyArray(
      (longtapActions ?? []).map {
        $0.uiAction(context: context)
      }
    )
  }
}

extension DoubleTapActionsHolder {
  func makeDoubleTapActions(context: DivBlockModelingActionContext)
    -> NonEmptyArray<UserInterfaceAction>? {
    NonEmptyArray(
      (doubletapActions ?? []).map {
        $0.uiAction(context: context)
      }
    )
  }
}

extension DivAction {
  func uiAction(context: DivBlockModelingActionContext) -> UserInterfaceAction {
    let menuPayload = makeMenuPayload(context: context)
    // don't make .divAction payloads for menu actions until DivActionHandler
    // could handle it
    let divActionPayload = menuPayload == nil
      ? makeDivActionPayload(cardId: context.cardId, source: .tap)
      : nil
    let payloads: [UserInterfaceAction.Payload] = [
      menuPayload,
      makeJsonPayload(),
      divActionPayload,
    ].compactMap { $0 }

    let path: UIElementPath
    if let cardLogId = context.cardLogId {
      path = UIElementPath(cardLogId) + logId
    } else {
      path = UIElementPath(logId)
    }

    return UserInterfaceAction(
      payloads: payloads,
      path: path
    )
  }

  private func makeMenuPayload(
    context: DivBlockModelingActionContext
  ) -> UserInterfaceAction.Payload? {
    guard let menuItems = menuItems else { return nil }

    let items: [Menu.Item] = menuItems.compactMap { item in
      if let actions = item.actions {
        let uiActions = actions.compactMap { $0.makeUiAction(context: context) }
        if !uiActions.isEmpty {
          return Menu.Item(
            actions: uiActions,
            text: item.resolveText(context.expressionResolver) ?? ""
          )
        }
      }
      if let uiAction = item.action?.makeUiAction(context: context) {
        return Menu.Item(
          actions: [uiAction],
          text: item.resolveText(context.expressionResolver) ?? ""
        )
      }
      return nil
    }

    guard let menu = Menu(items: items) else { return nil }

    return .menu(menu)
  }

  func makeUiAction(context: DivBlockModelingActionContext) -> UserInterfaceAction? {
    let uiAction = uiAction(context: context)
    return uiAction.payload == .empty ? nil : uiAction
  }
}

extension UserInterfaceAction.Payload {
  fileprivate var isMenu: Bool {
    if case .menu = self { return true }
    return false
  }
}

extension DivAction: DivActionBase {}
