import DivKit
import LayoutKit

@MainActor
final class UITestLogReporter: @preconcurrency DivReporter, @preconcurrency DivExtensionHandler {
  let id = "uitest-log"

  private var events: [UITestLogEvent] = []
  private var durationsByPath: [UIElementPath: [UITestLogDuration]] = [:]

  func accept(div: DivBase, context: DivBlockModelingContext) {
    let resolver = context.expressionResolver
    let visibilityActions = div.visibilityActions ?? div.visibilityAction.map { [$0] } ?? []
    var durations = logDurations(
      for: visibilityActions,
      source: .visibility,
      resolver: resolver
    ) { action, resolver in
      action.resolveVisibilityDuration(resolver)
    }
    durations += logDurations(
      for: div.disappearActions ?? [],
      source: .disappear,
      resolver: resolver
    ) { action, resolver in
      action.resolveDisappearDuration(resolver)
    }
    durationsByPath[context.path] = durations
  }

  func reportAction(cardId _: DivCardID, info: DivActionInfo) {
    guard let source = UITestLogSource(info.source) else { return }
    events.append(UITestLogEvent(source: source, id: info.logId))
  }

  func reportError(cardId _: DivCardID, error _: DivError) {}

  func logs() -> UITestLogSnapshot {
    return UITestLogSnapshot(
      events: events,
      durations: durationsByPath.values.flatMap { $0 }
    )
  }

  private func logDurations<Action: DivSightAction>(
    for actions: [Action],
    source: UITestLogSource,
    resolver: ExpressionResolver,
    duration: (Action, ExpressionResolver) -> Int
  ) -> [UITestLogDuration] {
    actions.map { action in
      UITestLogDuration(
        source: source,
        id: action.resolveLogId(resolver) ?? "",
        durationMilliseconds: duration(action, resolver)
      )
    }
  }
}

private extension UITestLogSource {
  init?(_ source: UserInterfaceAction.DivActionSource) {
    switch source {
    case .visibility:
      self = .visibility
    case .disappear:
      self = .disappear
    case .tap, .timer, .trigger, .callback, .property:
      return nil
    }
  }
}
