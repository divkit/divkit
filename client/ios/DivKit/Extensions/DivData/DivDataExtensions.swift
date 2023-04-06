import CommonCorePublic
import LayoutKit
import Serialization

extension DivData: DivBlockModeling {
  static let rootPath = DivStatePath(rawValue: UIElementPath("{root}"))

  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let stateManager = context.stateManager
    guard let state = getCurrentState(stateManager: stateManager, context: context) else {
      throw DivBlockModelingError("DivData has no states", path: context.parentPath)
    }

    let stateId = String(state.stateId)
    let statePath = DivStatePath(rawValue: UIElementPath(stateId))
    let div = state.div
    let divContext = modified(context) {
      $0.cardLogId = $0.cardLogId ?? logId
      $0.parentPath = $0.parentPath + stateId
      $0.parentDivStatePath = statePath
      if case .divGallery = div {} else {
        $0.galleryResizableInsets = nil
      }
    }

    stateManager.updateBlockIdsWithStateChangeTransition(
      statePath: statePath,
      div: div
    )
    let block = try div.value.makeBlock(context: divContext)
    stateManager.setState(
      stateBlockPath: DivData.rootPath,
      stateID: DivStateID(rawValue: stateId)
    )
    return block
      .addingStateBlock(
        ids: stateManager.getVisibleIds(statePath: statePath)
      )
      .addingDebugInfo(
        debugParams: divContext.debugParams,
        errors: divContext.errorsStorage.errors,
        parentPath: divContext.parentPath
      )
  }

  private func getCurrentState(
    stateManager: DivStateManager,
    context: DivBlockModelingContext
  ) -> DivData.State? {
    guard let item = stateManager.get(stateBlockPath: DivData.rootPath) else {
      return states.first
    }

    let stateId = item.currentStateID.rawValue
    if let state = states.first(where: { String($0.stateId) == stateId }) {
      return state
    }

    context.addError(level: .error, message: "DivData.State not found: \(stateId)")
    return states.first
  }
}

extension DivData {
  public static func resolve(
    card cardDict: [String: Any],
    templates templatesDict: [String: Any]?
  ) -> DeserializationResult<DivData> {
    let divTemplates = templatesDict.map(DivTemplates.init) ?? .empty
    return divTemplates.parseValue(type: DivDataTemplate.self, from: cardDict)
  }
}

extension DivData {
  public func flatMap<T>(_ transform: (Div) -> T) -> [T] {
    var result: [T] = []
    func traverse(div: Div) {
      result.append(transform(div))
      div.children.forEach(traverse(div:))
    }
    states.map(\.div).forEach(traverse(div:))
    return result
  }
}
