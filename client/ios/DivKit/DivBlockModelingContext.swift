import CoreGraphics

import BasePublic
import BaseUIPublic
import LayoutKit
import NetworkingPublic
import Serialization

public struct DivBlockModelingContext {
  public let cardId: DivCardID
  internal var cardLogId: String?
  public internal(set) var parentPath: UIElementPath
  internal var parentDivStatePath: DivStatePath?
  public var stateManager: DivStateManager
  public let blockStateStorage: DivBlockStateStorage
  public let visibilityCounter: DivVisibilityCounting
  public let imageHolderFactory: ImageHolderFactory
  public let highPriorityImageHolderFactory: ImageHolderFactory?
  public let divCustomBlockFactory: DivCustomBlockFactory
  public let fontProvider: DivFontProvider
  public let flagsInfo: DivFlagsInfo
  public let extensionHandlers: [String: DivExtensionHandler]
  public let stateInterceptors: [String: DivStateInterceptor]
  private let variables: DivVariables
  public let layoutDirection: UserInterfaceLayoutDirection
  public let debugParams: DebugParams
  public let playerFactory: PlayerFactory?
  public var childrenA11yDescription: String?
  public weak var parentScrollView: ScrollView?
  public let errorsStorage: DivErrorsStorage
  private let variableTracker: DivVariableTracker?
  private let persistentValuesStorage: DivPersistentValuesStorage

  var overridenWidth: DivOverridenSize?
  var overridenHeight: DivOverridenSize?

  public var expressionResolver: ExpressionResolver {
    ExpressionResolver(
      variables: variables,
      persistentValuesStorage: persistentValuesStorage,
      errorTracker: { [weak errorsStorage] error in
        errorsStorage?.add(DivBlockModelingRuntimeError(error, path: parentPath))
      },
      variableTracker: { [weak variableTracker] variables in
        variableTracker?.onVariablesUsed(cardId: cardId, variables: variables)
      }
    )
  }

  public init(
    cardId: DivCardID,
    cardLogId: String? = nil,
    parentPath: UIElementPath? = nil,
    parentDivStatePath: DivStatePath? = nil,
    stateManager: DivStateManager,
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    visibilityCounter: DivVisibilityCounting = DivVisibilityCounter(),
    imageHolderFactory: ImageHolderFactory,
    highPriorityImageHolderFactory: ImageHolderFactory? = nil,
    divCustomBlockFactory: DivCustomBlockFactory = EmptyDivCustomBlockFactory(),
    fontProvider: DivFontProvider? = nil,
    flagsInfo: DivFlagsInfo = .default,
    extensionHandlers: [DivExtensionHandler] = [],
    stateInterceptors: [DivStateInterceptor] = [],
    variables: DivVariables = [:],
    playerFactory: PlayerFactory? = nil,
    debugParams: DebugParams = DebugParams(),
    childrenA11yDescription: String? = nil,
    parentScrollView: ScrollView? = nil,
    errorsStorage: DivErrorsStorage = DivErrorsStorage(errors: []),
    layoutDirection: UserInterfaceLayoutDirection = UserInterfaceLayoutDirection.system,
    variableTracker: DivVariableTracker? = nil,
    persistentValuesStorage: DivPersistentValuesStorage = DivPersistentValuesStorage()
  ) {
    self.cardId = cardId
    self.cardLogId = cardLogId
    self.parentPath = parentPath ?? UIElementPath(cardId.rawValue)
    self.parentDivStatePath = parentDivStatePath
    self.stateManager = stateManager
    self.blockStateStorage = blockStateStorage
    self.visibilityCounter = visibilityCounter
    self.imageHolderFactory = imageHolderFactory
    self.highPriorityImageHolderFactory = highPriorityImageHolderFactory
    self.divCustomBlockFactory = divCustomBlockFactory
    self.flagsInfo = flagsInfo
    self.fontProvider = fontProvider ?? DefaultFontProvider()
    self.variables = variables
    self.playerFactory = playerFactory
    self.debugParams = debugParams
    self.childrenA11yDescription = childrenA11yDescription
    self.parentScrollView = parentScrollView
    self.errorsStorage = errorsStorage
    self.layoutDirection = layoutDirection
    self.variableTracker = variableTracker
    self.persistentValuesStorage = persistentValuesStorage

    var extensionsHandlersDictionary = [String: DivExtensionHandler]()
    extensionHandlers.forEach {
      let id = $0.id
      if extensionsHandlersDictionary[id] != nil {
        DivKitLogger.failure("Duplicate DivExtensionHandler for: \(id)")
        return
      }
      extensionsHandlersDictionary[id] = $0
    }
    self.extensionHandlers = extensionsHandlersDictionary

    var stateInterceptorsDictionary = [String: DivStateInterceptor]()
    stateInterceptors.forEach {
      let id = $0.id
      if stateInterceptorsDictionary[id] != nil {
        DivKitLogger.failure("Duplicate DivStateInterceptor for: \(id)")
        return
      }
      stateInterceptorsDictionary[id] = $0
    }
    self.stateInterceptors = stateInterceptorsDictionary
  }

  public func getExtensionHandlers(for div: DivBase) -> [DivExtensionHandler] {
    guard let extensions = div.extensions else {
      return []
    }
    return extensions.compactMap {
      let id = $0.id
      if !extensionHandlers.keys.contains(id) && !stateInterceptors.keys.contains(id) {
        addError(level: .warning, message: "No DivExtensionHandler/DivStateInterceptor for: \(id)")
      }
      return extensionHandlers[id]
    }
  }

  public func getStateInterceptor(for divState: DivState) -> DivStateInterceptor? {
    divState.extensions?.compactMap { stateInterceptors[$0.id] }.first
  }

  public func addError(level: DivErrorLevel, message: String) {
    let error: DivError
    switch level {
    case .warning:
      error = DivBlockModelingWarning(message, path: parentPath)
    case .error:
      error = DivBlockModelingError(message, path: parentPath)
    }
    errorsStorage.add(error)
  }

  func override(width: DivSize) -> DivSize {
    guard let overridenWidth = overridenWidth else {
      return width
    }
    if overridenWidth.original == width {
      return overridenWidth.overriden
    }
    return width
  }

  func override(height: DivSize) -> DivSize {
    guard let overridenHeight = overridenHeight else {
      return height
    }
    if overridenHeight.original == height {
      return overridenHeight.overriden
    }
    return height
  }
}
