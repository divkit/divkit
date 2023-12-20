import CommonCorePublic
import LayoutKit

extension DivAction: DivActionBase {}

extension DivAction {
  func uiAction(context: DivBlockModelingContext) -> UserInterfaceAction? {
    guard resolveIsEnabled(context.expressionResolver) else {
      return nil
    }
    let payload: UserInterfaceAction.Payload
    if let menuPayload = makeMenuPayload(context: context) {
      payload = menuPayload
    } else {
      // don't make .divAction payloads for menu actions until DivActionHandler could handle it
      payload = makeDivActionPayload(cardId: context.cardId, source: .tap)
    }

    let path: UIElementPath
    if let cardLogId = context.cardLogId {
      path = UIElementPath(cardLogId) + logId
    } else {
      path = UIElementPath(logId)
    }

    return UserInterfaceAction(payload: payload, path: path)
  }

  private func makeMenuPayload(context: DivBlockModelingContext) -> UserInterfaceAction.Payload? {
    guard let menuItems = menuItems else { return nil }
    let expressionResolver = context.expressionResolver

    let items: [Menu.Item] = menuItems.compactMap { item in
      if let actions = item.actions {
        let uiActions = actions.compactMap { $0.uiAction(context: context) }
        if !uiActions.isEmpty {
          return Menu.Item(
            actions: uiActions,
            text: item.resolveText(expressionResolver) ?? ""
          )
        }
      }
      if let uiAction = item.action?.uiAction(context: context) {
        return Menu.Item(
          actions: [uiAction],
          text: item.resolveText(expressionResolver) ?? ""
        )
      }
      return nil
    }

    guard let menu = Menu(items: items) else { return nil }

    return .menu(menu)
  }
}

extension Array where Element == DivAction {
  func uiActions(context: DivBlockModelingContext) -> [UserInterfaceAction] {
    compactMap { $0.uiAction(context: context) }
  }
}
