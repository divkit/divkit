import LayoutKit

enum PathResolution {
  case resolved(UIElementPath)
  case notFound
  case ambiguous
}

/// Turns an element id into a single path. Elements sharing an id are not distinguishable
/// by it, so an ambiguous lookup resolves to nothing instead of an arbitrary element.
struct PathResolver {
  private let idToPath: IdToPath

  init(idToPath: IdToPath) {
    self.idToPath = idToPath
  }

  func resolvePath(
    id: String,
    cardId: DivCardID,
    scopePath: UIElementPath? = nil,
    divTypes: Set<String>? = nil
  ) -> PathResolution {
    let paths = idToPath.paths(
      forId: id,
      cardId: cardId,
      scopePath: scopePath,
      divTypes: divTypes
    )

    switch paths.count {
    case 0:
      return .notFound
    case 1:
      return .resolved(paths[0])
    default:
      return .ambiguous
    }
  }
}
