import LayoutKit

struct ActionPathResolver {
  private let reporter: DivReporter
  private let pathResolver: PathResolver

  init(reporter: DivReporter, idToPath: IdToPath) {
    self.reporter = reporter
    pathResolver = PathResolver(idToPath: idToPath)
  }

  func resolveTarget(
    id: String,
    cardId: DivCardID,
    scopePath: UIElementPath?,
    divTypes: Set<String>? = nil,
    extensionId: String? = nil
  ) -> TargetResolution {
    pathResolver.resolveTarget(
      id: id,
      cardId: cardId,
      scopePath: scopePath,
      divTypes: divTypes,
      extensionId: extensionId
    )
  }

  /// Resolves `id` and performs the action. If the element is not modeled yet
  /// (e.g. it lives in a `gone` subtree that a preceding action in the same batch
  /// is revealing), the action is parked and retried after all views have been
  /// re-modeled in the next card-update tick. Ambiguity is reported immediately.
  ///
  /// `divTypes` restricts the lookup to elements of the given div types, so an
  /// action targeting a specific element kind (e.g. video) is not confused by
  /// other elements sharing the same `id`.
  func resolve(
    id: String,
    divTypes: Set<String>? = nil,
    context: DivActionHandlingContext,
    perform: @escaping (UIElementPath) -> Void
  ) {
    resolveTarget(id: id, divTypes: divTypes, context: context) { target in
      perform(target.path)
    }
  }

  func resolveTarget(
    id: String,
    divTypes: Set<String>? = nil,
    extensionId: String? = nil,
    context: DivActionHandlingContext,
    perform: @escaping (IdToPath.Target) -> Void
  ) {
    switch resolveTarget(
      id: id,
      cardId: context.cardId,
      scopePath: context.scopePath,
      divTypes: divTypes,
      extensionId: extensionId
    ) {
    case let .resolved(target):
      perform(target)
    case .notFound:
      context.actionHandler.enqueuePendingAction(
        id: id,
        divTypes: divTypes,
        extensionId: extensionId,
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

  func reportDeferred(
    id: String,
    cardId: DivCardID,
    scopePath: UIElementPath?,
    sourcePath: UIElementPath
  ) {
    reporter.reportError(
      cardId: cardId,
      error: DivUnknownWarning(
        "Element with id '\(id)' is not modeled yet, the action is deferred\(scopeSuffix(scopePath))",
        path: sourcePath
      )
    )
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
