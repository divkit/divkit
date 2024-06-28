import CommonCorePublic
import LayoutKit

extension DivAction: DivActionBase {}

extension DivAction {
  public func uiAction(context: DivBlockModelingContext) -> UserInterfaceAction? {
    let expressionResolver = context.expressionResolver
    guard resolveIsEnabled(expressionResolver) else {
      return nil
    }
    let payload = if let menuPayload = makeMenuPayload(context: context) {
      menuPayload
    } else {
      // don't make .divAction payloads for menu actions until DivActionHandler could handle it
      makeDivActionPayload(
        path: context.parentPath,
        source: .tap,
        localValues: context.localValues
      )
    }

    let logId = resolveLogId(expressionResolver) ?? ""
    let path = if let cardLogId = context.cardLogId {
      UIElementPath(cardLogId) + logId
    } else {
      UIElementPath(logId)
    }

    return UserInterfaceAction(payload: payload, path: path)
  }

  private func makeMenuPayload(context: DivBlockModelingContext) -> UserInterfaceAction.Payload? {
    guard let menuItems else { return nil }
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

extension [DivAction] {
  public func uiActions(context: DivBlockModelingContext) -> [UserInterfaceAction] {
    compactMap { $0.uiAction(context: context) }
  }
}
