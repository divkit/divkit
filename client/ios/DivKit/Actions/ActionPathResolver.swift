import LayoutKit

enum PathResolution {
  case resolved(UIElementPath)
  case notFound
  case ambiguous
}

struct ActionPathResolver {
  private let reporter: DivReporter
  private let idToPath: IdToPath

  init(reporter: DivReporter, idToPath: IdToPath) {
    self.reporter = reporter
    self.idToPath = idToPath
  }

  func resolvePath(
    id: String,
    cardId: DivCardID,
    scopePath: UIElementPath?
  ) -> PathResolution {
    let componentPaths = idToPath[cardId.path + id]
    let paths = scopePath
      .map { scope in componentPaths.filter { $0.starts(with: scope) } } ?? componentPaths

    switch paths.count {
    case 0:
      return .notFound
    case 1:
      return .resolved(paths[0])
    default:
      return .ambiguous
    }
  }

  /// Resolves `id` and performs the action. If the element is not modeled yet
  /// (e.g. it lives in a `gone` subtree that a preceding action in the same batch
  /// is revealing), the action is parked and retried after the next re-model — see
  /// `DivActionHandler.applyPendingActions`. Ambiguity is reported immediately.
  func resolve(
    id: String,
    context: DivActionHandlingContext,
    perform: @escaping (UIElementPath) -> Void
  ) {
    switch resolvePath(id: id, cardId: context.cardId, scopePath: context.scopePath) {
    case let .resolved(path):
      perform(path)
    case .notFound:
      context.actionHandler.enqueuePendingAction(
        id: id,
        scopePath: context.scopePath,
        cardId: context.cardId,
        sourcePath: context.sourcePath,
        apply: perform
      )
    case .ambiguous:
      reportAmbiguous(
        id: id,
        cardId: context.cardId,
        scopePath: context.scopePath,
        sourcePath: context.sourcePath
      )
    }
  }

  func reportNotFound(
    id: String,
    cardId: DivCardID,
    scopePath: UIElementPath?,
    sourcePath: UIElementPath
  ) {
    reportError(
      "Element with id '\(id)' not found\(scopeSuffix(scopePath))",
      cardId: cardId,
      sourcePath: sourcePath
    )
  }

  func reportAmbiguous(
    id: String,
    cardId: DivCardID,
    scopePath: UIElementPath?,
    sourcePath: UIElementPath
  ) {
    reportError(
      "Element with id '\(id)' is ambiguous\(scopeSuffix(scopePath))",
      cardId: cardId,
      sourcePath: sourcePath
    )
  }

  private func scopeSuffix(_ scopePath: UIElementPath?) -> String {
    scopePath == nil ? "" : " in scope"
  }

  private func reportError(
    _ message: String,
    cardId: DivCardID,
    sourcePath: UIElementPath
  ) {
    reporter.reportError(
      cardId: cardId,
      error: DivUnknownError(message: message, path: sourcePath)
    )
  }
}
