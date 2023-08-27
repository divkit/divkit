import CoreGraphics

import BasePublic
import BaseUIPublic
import CommonCorePublic
import LayoutKit
import NetworkingPublic
import Serialization

#if os(iOS)
import UIKit
#else
import AppKit
#endif

public struct DivBlockModelingContext {
  public let cardId: DivCardID
  internal var cardLogId: String?
  public internal(set) var parentPath: UIElementPath
  internal var parentDivStatePath: DivStatePath?
  public var stateManager: DivStateManager
  public let blockStateStorage: DivBlockStateStorage
  public let visibilityCounter: DivVisibilityCounting
  public let lastVisibleBoundsCache: DivLastVisibleBoundsCache
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
  public let scheduler: Scheduling
  public let playerFactory: PlayerFactory?
  public var childrenA11yDescription: String?
  public weak var parentScrollView: ScrollView?
  public internal(set) var errorsStorage: DivErrorsStorage
  internal let variableTracker: DivVariableTracker?
  private let persistentValuesStorage: DivPersistentValuesStorage
  public let tooltipViewFactory: DivTooltipViewFactory?

  var overridenWidth: DivOverridenSize?
  var overridenHeight: DivOverridenSize?
  private let variablesStorage: DivVariablesStorage?

  public var expressionResolver: ExpressionResolver {
    ExpressionResolver(
      variables: variables,
      persistentValuesStorage: persistentValuesStorage,
      errorTracker: { [weak errorsStorage] error in
        errorsStorage?.add(DivExpressionError(error, path: parentPath))
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
    lastVisibleBoundsCache: DivLastVisibleBoundsCache = DivLastVisibleBoundsCache(),
    imageHolderFactory: ImageHolderFactory,
    highPriorityImageHolderFactory: ImageHolderFactory? = nil,
    divCustomBlockFactory: DivCustomBlockFactory = EmptyDivCustomBlockFactory(),
    fontProvider: DivFontProvider? = nil,
    flagsInfo: DivFlagsInfo = .default,
    extensionHandlers: [DivExtensionHandler] = [],
    stateInterceptors: [DivStateInterceptor] = [],
    variablesStorage: DivVariablesStorage? = nil,
    playerFactory: PlayerFactory? = nil,
    debugParams: DebugParams = DebugParams(),
    scheduler: Scheduling? = nil,
    childrenA11yDescription: String? = nil,
    parentScrollView: ScrollView? = nil,
    errorsStorage: DivErrorsStorage = DivErrorsStorage(errors: []),
    layoutDirection: UserInterfaceLayoutDirection = .leftToRight,
    variableTracker: DivVariableTracker? = nil,
    persistentValuesStorage: DivPersistentValuesStorage = DivPersistentValuesStorage(),
    tooltipViewFactory: DivTooltipViewFactory? = nil
  ) {
    self.cardId = cardId
    self.cardLogId = cardLogId
    self.parentPath = parentPath ?? UIElementPath(cardId.rawValue)
    self.parentDivStatePath = parentDivStatePath
    self.stateManager = stateManager
    self.blockStateStorage = blockStateStorage
    self.visibilityCounter = visibilityCounter
    self.lastVisibleBoundsCache = lastVisibleBoundsCache
    self.imageHolderFactory = imageHolderFactory
    self.highPriorityImageHolderFactory = highPriorityImageHolderFactory
    self.divCustomBlockFactory = divCustomBlockFactory
    self.flagsInfo = flagsInfo
    self.fontProvider = fontProvider ?? DefaultFontProvider()
    self.variables = variablesStorage?.makeVariables(for: cardId) ?? [:]
    self.playerFactory = playerFactory
    self.debugParams = debugParams
    self.scheduler = scheduler ?? TimerScheduler()
    self.childrenA11yDescription = childrenA11yDescription
    self.parentScrollView = parentScrollView
    self.errorsStorage = errorsStorage
    self.layoutDirection = layoutDirection
    self.variableTracker = variableTracker
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
  }

  public func getExtensionHandlers(for div: DivBase) -> [DivExtensionHandler] {
    guard let extensions = div.extensions else {
      return []
    }
    return extensions.compactMap {
      let id = $0.id
      if !extensionHandlers.keys.contains(id) && !stateInterceptors.keys.contains(id) {
        addError(message: "No DivExtensionHandler/DivStateInterceptor for: \(id)")
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

  func makeBinding<T>(variableName: String, defaultValue: T) -> Binding<T> {
    variableTracker?.onVariablesUsed(
      cardId: cardId,
      variables: [DivVariableName(rawValue: variableName)]
    )
    let value: T = expressionResolver.getVariableValue(variableName) ?? defaultValue
    let valueProp = Property<T>.init(
      getter: { value },
      setter: {
        guard let divVariableValue = DivVariableValue($0) else { return }
        self.variablesStorage?.update(
          cardId: cardId,
          name: DivVariableName(rawValue: variableName),
          value: divVariableValue
        )
      }
    )
    return Binding(name: variableName, value: valueProp)
  }
}
