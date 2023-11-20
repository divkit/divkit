import CoreGraphics

import BasePublic
import BaseUIPublic
import LayoutKit
import NetworkingPublic

#if os(iOS)
import UIKit
#else
import AppKit
#endif

public struct DivBlockModelingContext {
  public let cardId: DivCardID
  var cardLogId: String?
  var parentDivStatePath: DivStatePath?
  let stateManager: DivStateManager
  public let blockStateStorage: DivBlockStateStorage
  let visibilityCounter: DivVisibilityCounting
  let lastVisibleBoundsCache: DivLastVisibleBoundsCache
  public let imageHolderFactory: DivImageHolderFactory
  let highPriorityImageHolderFactory: DivImageHolderFactory?
  let divCustomBlockFactory: DivCustomBlockFactory
  let fontProvider: DivFontProvider
  let flagsInfo: DivFlagsInfo
  let extensionHandlers: [String: DivExtensionHandler]
  let stateInterceptors: [String: DivStateInterceptor]
  let layoutDirection: UserInterfaceLayoutDirection
  let debugParams: DebugParams
  let scheduler: Scheduling
  let playerFactory: PlayerFactory?
  var childrenA11yDescription: String?
  private(set) weak var parentScrollView: ScrollView?
  public internal(set) var errorsStorage: DivErrorsStorage
  private let persistentValuesStorage: DivPersistentValuesStorage
  let tooltipViewFactory: DivTooltipViewFactory?
  public let variablesStorage: DivVariablesStorage
  private let variableTracker: DivVariableTracker?
  public private(set) var expressionResolver: ExpressionResolver

  public internal(set) var parentPath: UIElementPath {
    didSet {
      expressionResolver = makeExpressionResolver(
        cardId: cardId,
        parentPath: parentPath,
        variablesStorage: variablesStorage,
        persistentValuesStorage: persistentValuesStorage,
        errorsStorage: errorsStorage,
        variableTracker: variableTracker
      )
    }
  }

  var sizeModifier: DivSizeModifier?

  public init(
    cardId: DivCardID,
    cardLogId: String? = nil,
    parentPath: UIElementPath? = nil,
    parentDivStatePath: DivStatePath? = nil,
    stateManager: DivStateManager,
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    visibilityCounter: DivVisibilityCounting? = nil,
    lastVisibleBoundsCache: DivLastVisibleBoundsCache? = nil,
    imageHolderFactory: DivImageHolderFactory,
    highPriorityImageHolderFactory: DivImageHolderFactory? = nil,
    divCustomBlockFactory: DivCustomBlockFactory? = nil,
    fontProvider: DivFontProvider? = nil,
    flagsInfo: DivFlagsInfo = .default,
    extensionHandlers: [DivExtensionHandler] = [],
    stateInterceptors: [DivStateInterceptor] = [],
    variablesStorage: DivVariablesStorage = DivVariablesStorage(),
    playerFactory: PlayerFactory? = nil,
    debugParams: DebugParams = DebugParams(),
    scheduler: Scheduling? = nil,
    childrenA11yDescription: String? = nil,
    parentScrollView: ScrollView? = nil,
    errorsStorage: DivErrorsStorage? = nil,
    layoutDirection: UserInterfaceLayoutDirection = .leftToRight,
    variableTracker: DivVariableTracker? = nil,
    persistentValuesStorage: DivPersistentValuesStorage? = nil,
    tooltipViewFactory: DivTooltipViewFactory? = nil
  ) {
    self.cardId = cardId
    self.cardLogId = cardLogId
    let parentPath = parentPath ?? UIElementPath(cardId.rawValue)
    self.parentPath = parentPath
    self.parentDivStatePath = parentDivStatePath
    self.stateManager = stateManager
    self.blockStateStorage = blockStateStorage
    self.visibilityCounter = visibilityCounter ?? DivVisibilityCounter()
    self.lastVisibleBoundsCache = lastVisibleBoundsCache ?? DivLastVisibleBoundsCache()
    self.imageHolderFactory = imageHolderFactory
    self.highPriorityImageHolderFactory = highPriorityImageHolderFactory
    self.divCustomBlockFactory = divCustomBlockFactory ?? EmptyDivCustomBlockFactory()
    self.flagsInfo = flagsInfo
    self.fontProvider = fontProvider ?? DefaultFontProvider()
    self.playerFactory = playerFactory
    self.debugParams = debugParams
    self.scheduler = scheduler ?? TimerScheduler()
    self.childrenA11yDescription = childrenA11yDescription
    self.parentScrollView = parentScrollView
    self.errorsStorage = errorsStorage ?? DivErrorsStorage(errors: [])
    self.layoutDirection = layoutDirection
    self.variableTracker = variableTracker
    let persistentValuesStorage = persistentValuesStorage ?? DivPersistentValuesStorage()
    self.persistentValuesStorage = persistentValuesStorage
    self.tooltipViewFactory = tooltipViewFactory
    self.variablesStorage = variablesStorage

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

    expressionResolver = makeExpressionResolver(
      cardId: cardId,
      parentPath: parentPath,
      variablesStorage: variablesStorage,
      persistentValuesStorage: persistentValuesStorage,
      errorsStorage: errorsStorage,
      variableTracker: variableTracker
    )
  }

  public func getExtensionHandlers(for div: DivBase) -> [DivExtensionHandler] {
    guard let extensions = div.extensions else {
      return []
    }
    return extensions.compactMap {
      let id = $0.id
      if !extensionHandlers.keys.contains(id), !stateInterceptors.keys.contains(id) {
        addError(message: "No DivExtensionHandler for: \(id)")
      }
      return extensionHandlers[id]
    }
  }

  public func getStateInterceptor(for divState: DivState) -> DivStateInterceptor? {
    divState.extensions?.compactMap { stateInterceptors[$0.id] }.first
  }

  public func addError(message: String, causes: [DivError] = []) {
    errorsStorage.add(DivBlockModelingError(message, path: parentPath, causes: causes))
  }

  public func addWarning(message: String) {
    errorsStorage.add(DivBlockModelingWarning(message, path: parentPath))
  }

  func addError(error: Error) {
    if let divError = error as? DivError {
      errorsStorage.add(divError)
      return
    }
    errorsStorage.add(DivUnknownError(error, path: parentPath))
  }

  func makeBinding<T>(variableName: String, defaultValue: T) -> Binding<T> {
    let variableName = DivVariableName(rawValue: variableName)
    variableTracker?.onVariablesUsed(
      cardId: cardId,
      variables: [variableName]
    )
    let value: T = variablesStorage
      .getVariableValue(cardId: cardId, name: variableName) ?? defaultValue
    let valueProp = Property<T>(
      getter: { value },
      setter: {
        guard let newValue = DivVariableValue($0) else { return }
        self.variablesStorage.update(
          cardId: cardId,
          name: variableName,
          value: newValue
        )
      }
    )
    return Binding(name: variableName.rawValue, value: valueProp)
  }
}

private func makeExpressionResolver(
  cardId: DivCardID,
  parentPath: UIElementPath,
  variablesStorage: DivVariablesStorage,
  persistentValuesStorage: DivPersistentValuesStorage,
  errorsStorage: DivErrorsStorage?,
  variableTracker: DivVariableTracker?
) -> ExpressionResolver {
  ExpressionResolver(
    cardId: cardId,
    variablesStorage: variablesStorage,
    persistentValuesStorage: persistentValuesStorage,
    errorTracker: { [weak errorsStorage] error in
      errorsStorage?.add(DivExpressionError(error, path: parentPath))
    },
    variableTracker: { [weak variableTracker] variables in
      variableTracker?.onVariablesUsed(cardId: cardId, variables: variables)
    }
  )
}
