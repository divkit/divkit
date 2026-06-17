import LayoutKit

struct ActionPathResolver {
  private let reporter: DivReporter
  private let idToPath: IdToPath

  init(reporter: DivReporter, idToPath: IdToPath) {
    self.reporter = reporter
    self.idToPath = idToPath
  }

  func resolve(
    id: String,
    context: DivActionHandlingContext,
    perform: (UIElementPath) -> Void
  ) {
    let componentPaths = idToPath[context.cardId.path + id]
    let paths = context.scopePath
      .map { scope in componentPaths.filter { $0.starts(with: scope) } } ?? componentPaths

    switch paths.count {
    case 0:
      let suffix = context.scopePath == nil ? "" : " in scope"
      reportError("Element with id '\(id)' not found\(suffix)", context: context)
    case 1:
      perform(paths[0])
    default:
      let suffix = context.scopePath == nil ? "" : " in scope"
      reportError("Element with id '\(id)' is ambiguous\(suffix)", context: context)
    }
  }

  private func reportError(
    _ message: String,
    context: DivActionHandlingContext
  ) {
    reporter.reportError(
      cardId: context.cardId,
      error: DivUnknownError(message: message, path: context.sourcePath)
    )
  }
}
