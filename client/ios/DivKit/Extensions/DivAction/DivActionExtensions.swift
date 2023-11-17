import CommonCorePublic
import LayoutKit

extension DivAction: DivActionBase {}

extension DivAction {
  func uiAction(context: DivBlockModelingContext) -> UserInterfaceAction? {
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

    let uiAction = UserInterfaceAction(
      payloads: payloads,
      path: path
    )
    return uiAction.payload == .empty ? nil : uiAction
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
