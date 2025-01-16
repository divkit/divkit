import LayoutKit
import Serialization
import VGSL

extension DivData: DivBlockModeling {
  static let rootPath = DivStatePath(rawValue: UIElementPath("{root}"))

  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    guard let state = getCurrentState(context: context) else {
      throw DivBlockModelingError("DivData has no states", path: context.parentPath)
    }

    let stateManager = context.stateManager
    if let previousRootState = getPreviousRootState(stateManager: stateManager) {
      context.lastVisibleBoundsCache.dropVisibleBounds(
        prefix: context.parentPath + previousRootState.rawValue
      )
    }

    let stateId = String(state.stateId)
    let statePath = DivStatePath(rawValue: UIElementPath(stateId))
    let div = state.div

    let divContext = context.modifying(
      cardLogId: logId,
      parentPath: context.parentPath + stateId,
      parentDivStatePath: statePath
    )

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
      .addingDebugInfo(context: divContext)
  }

  private func getCurrentState(context: DivBlockModelingContext) -> DivData.State? {
    guard let item = context.stateManager.get(stateBlockPath: DivData.rootPath) else {
      return states.first
    }

    let stateId = item.currentStateID.rawValue
    if let state = states.first(where: { String($0.stateId) == stateId }) {
      return state
    }

    context.addError(message: "DivData.State not found: \(stateId)")
    return states.first
  }

  private func getPreviousRootState(stateManager: DivStateManager) -> DivStateID? {
    guard let item = stateManager.get(stateBlockPath: DivData.rootPath) else {
      return nil
    }

    switch item.previousState {
    case .empty, .initial:
      return nil
    case let .withID(id):
      if item.currentStateID != id {
        return id
      }
      return nil
    }
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
