import Foundation

final class SubmitActionHandler {
  private let submitter: DivSubmitter

  init(submitter: DivSubmitter) {
    self.submitter = submitter
  }

  func handle(_ action: DivActionSubmit, context: DivActionHandlingContext) {
    let resolver = context.expressionResolver

    guard let containerId = action.resolveContainerId(resolver) else {
      DivKitLogger.error("Invalid container id: \(action.containerId)")
      return
    }
    guard let url = action.request.resolveUrl(resolver) else {
      DivKitLogger.error("Invalid submit URL: \(action.request.url)")
      return
    }

    let containerData = context.variablesStorage.getVariables(
      cardId: context.path.cardId,
      elementId: containerId
    ).map(
      key: { $0.rawValue },
      value: { $0.toString() }
    )
    let request = SubmitRequest(
      url: url,
      method: action.request.resolveMethod(resolver).rawValue.uppercased(),
      headers: action.request.headers?.toDictionary(resolver: resolver) ?? [:]
    )

    submitter.submit(
      request: request,
      data: containerData
    ) { result in
      let actions = switch result {
      case .success: action.onSuccessActions
      case .failure: action.onFailActions
      }
      guard let actions else { return }

      for action in actions {
        context.actionHandler.handle(
          action,
          path: context.path,
          source: .callback,
          sender: nil
        )
      }
    }
  }
}

extension DivVariableValue {
  fileprivate func toString() -> String {
    switch self {
    case let .array(value): ExpressionValueConverter.stringify(value)
    case let .bool(value): value.description
    case let .color(value): value.argbString
    case let .dict(value): ExpressionValueConverter.stringify(value)
    case let .integer(value): value.description
    case let .number(value): ExpressionValueConverter.stringify(value)
    case let .string(value): value
    case let .url(value): value.description
    }
  }
}

extension [DivActionSubmit.Request.Header] {
  fileprivate func toDictionary(resolver: ExpressionResolver) -> [String: String] {
    var dict = [String: String]()
    forEach { param in
      guard let key = param.resolveName(resolver) else {
        DivKitLogger.warning("Expression \(param.name) is not valid")
        return
      }
      guard let value = param.resolveValue(resolver) else {
        DivKitLogger.warning("Expression \(param.value) is not valid")
        return
      }

      dict[key] = value
    }
    return dict
  }
}
