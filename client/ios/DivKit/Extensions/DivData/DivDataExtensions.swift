import CommonCore
import LayoutKit
import Serialization
import TemplatesSupport

extension DivData: DivBlockModeling {
  static let rootPath = DivStatePath(rawValue: UIElementPath("{root}"))

  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    guard let state = findState(for: context.parentDivStatePath?.stateId) else {
      throw DivBlockModelingError("DivData has no states", path: context.parentPath)
    }

    let stateIdString = String(state.stateId)
    let stateId = DivStateID(rawValue: stateIdString)
    let statePath = DivStatePath(rawValue: UIElementPath(stateIdString))
    let div = state.div
    let divContext = modified(context) {
      $0.cardLogId = $0.cardLogId ?? logId
      $0.parentPath = $0.parentPath + stateIdString
      $0.parentDivStatePath = statePath
      if case .divGallery = div {} else {
        $0.galleryResizableInsets = nil
      }
    }

    let stateManager = context.stateManager
    stateManager.updateBlockIdsWithStateChangeTransition(
      statePath: statePath,
      div: div
    )
    let block = try div.value.makeBlock(context: divContext)
    stateManager.setState(stateBlockPath: DivData.rootPath, stateID: stateId)
    return block
      .addingStateBlock(
        ids: stateManager.getVisibleIds(statePath: statePath)
      )
    #if INTERNAL_BUILD
      .addingErrorsButton(
        errors: divContext.blockModelingErrorsStorage.errors,
        parentPath: divContext.parentPath,
        safeAreaInsets: EdgeInsets(resolver: divContext.expressionResolver),
        showDebugInfo: divContext.showDebugInfo
      )
    #endif
  }

  public func findState(for stateId: DivDataStateID?) -> DivData.State? {
    guard let stateId = stateId?.rawValue else {
      return states.first
    }

    if let state = states.first(where: { $0.stateId == stateId }) {
      return state
    }

    DivKitLogger.error("DivData.State not found: \(stateId)")
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

extension Div {
  var children: [Div] {
    switch self {
    case let .divContainer(div): return div.items
    case let .divGrid(div): return div.items
    case let .divGallery(div): return div.items
    case let .divPager(div): return div.items
    case let .divTabs(div): return div.items.map(\.div)
    case let .divCustom(div): return div.items ?? []
    case let .divState(div): return div.states.compactMap(\.div)
    case .divImage, .divGifImage, .divText, .divSlider, .divIndicator, .divSeparator, .divInput:
      return []
    }
  }
}

extension EdgeInsets {
  fileprivate static let safeAreaTopVariable: DivVariableName = "safe_area_top"
  fileprivate static let safeAreaBottomVariable: DivVariableName = "safe_area_bottom"
  fileprivate static let safeAreaLeftVariable: DivVariableName = "safe_area_left"
  fileprivate static let safeAreaRightVariable: DivVariableName = "safe_area_right"

  fileprivate init(resolver: ExpressionResolver) {
    let top: Double = resolver.numericValue(Self.safeAreaTopVariable.rawValue) ?? 0
    let left: Double = resolver.numericValue(Self.safeAreaLeftVariable.rawValue) ?? 0
    let bottom: Double = resolver.numericValue(Self.safeAreaBottomVariable.rawValue) ?? 0
    let right: Double = resolver.numericValue(Self.safeAreaRightVariable.rawValue) ?? 0

    self.init(top: top, left: left, bottom: bottom, right: right)
  }

  public var asSafeAreaVariables: DivVariables {
    [
      Self.safeAreaTopVariable: .number(self.top),
      Self.safeAreaBottomVariable: .number(self.bottom),
      Self.safeAreaLeftVariable: .number(self.left),
      Self.safeAreaRightVariable: .number(self.right),
    ]
  }
}

extension ExpressionResolver {
  fileprivate func numericValue<T>(_ expression: String) -> T? {
    ExpressionLink(expression: expression, validator: nil)
      .flatMap { self.resolveNumericValue(expression: .link($0)) }
  }
}
