import LayoutKit

enum TargetResolution {
  case resolved(IdToPath.Target)
  case notFound
  case ambiguous
}

/// Turns an element id into a single target. Elements sharing an id are not distinguishable
/// by it, so an ambiguous lookup resolves to nothing instead of an arbitrary element.
struct PathResolver {
  private let idToPath: IdToPath

  init(idToPath: IdToPath) {
    self.idToPath = idToPath
  }

  func resolveTarget(
    id: String,
    cardId: DivCardID,
    scopePath: UIElementPath? = nil,
    divTypes: Set<String>? = nil,
    extensionId: String? = nil
  ) -> TargetResolution {
    let targets = idToPath.targets(
      forId: id,
      cardId: cardId,
      scopePath: scopePath,
      divTypes: divTypes,
      extensionId: extensionId
    )

    switch targets.count {
    case 0:
      return .notFound
    case 1:
      return .resolved(targets[0])
    default:
      return .ambiguous
    }
  }
}
