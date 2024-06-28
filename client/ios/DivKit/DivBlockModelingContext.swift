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
  let viewId: DivViewId
  private(set) var cardLogId: String?
  private(set) var parentDivStatePath: DivStatePath?
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
  let layoutDirection: UserInterfaceLayoutDirection
  let debugParams: DebugParams
  let scheduler: Scheduling
  let playerFactory: PlayerFactory?
  private(set) weak var parentScrollView: ScrollView?
  public private(set) var errorsStorage: DivErrorsStorage
  private let persistentValuesStorage: DivPersistentValuesStorage
  let tooltipViewFactory: DivTooltipViewFactory?
  public let variablesStorage: DivVariablesStorage
  public private(set) var expressionResolver: ExpressionResolver
  private let functionsProvider: FunctionsProvider
  public let variableTracker: DivVariableTracker?
  public private(set) var parentPath: UIElementPath
  private(set) var elementId: String?
  private(set) var sizeModifier: DivSizeModifier?
  private(set) var localValues = [String: AnyHashable]()

  public init(
    cardId: DivCardID,
    additionalId: String? = nil,
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
    variablesStorage: DivVariablesStorage = DivVariablesStorage(),
    playerFactory: PlayerFactory? = nil,
    debugParams: DebugParams = DebugParams(),
    scheduler: Scheduling? = nil,
    parentScrollView: ScrollView? = nil,
    errorsStorage: DivErrorsStorage? = nil,
    layoutDirection: UserInterfaceLayoutDirection = .leftToRight,
    variableTracker: DivVariableTracker? = nil,
    persistentValuesStorage: DivPersistentValuesStorage? = nil,
    tooltipViewFactory: DivTooltipViewFactory? = nil
  ) {
    var extensionsHandlersDictionary = [String: DivExtensionHandler]()
    for extensionHandler in extensionHandlers {
      let id = extensionHandler.id
      if extensionsHandlersDictionary[id] != nil {
        DivKitLogger.failure("Duplicate DivExtensionHandler for: \(id)")
        continue
      }
      extensionsHandlersDictionary[id] = extensionHandler
    }
    self.init(
      viewId: DivViewId(cardId: cardId, additionalId: additionalId),
      cardLogId: cardLogId,
      parentPath: parentPath,
      parentDivStatePath: parentDivStatePath,
      stateManager: stateManager,
      blockStateStorage: blockStateStorage,
      visibilityCounter: visibilityCounter,
      lastVisibleBoundsCache: lastVisibleBoundsCache,
      imageHolderFactory: imageHolderFactory,
      highPriorityImageHolderFactory: highPriorityImageHolderFactory,
      divCustomBlockFactory: divCustomBlockFactory,
      fontProvider: fontProvider,
      flagsInfo: flagsInfo,
      extensionHandlers: extensionsHandlersDictionary,
      variablesStorage: variablesStorage,
      playerFactory: playerFactory,
      debugParams: debugParams,
      scheduler: scheduler,
      parentScrollView: parentScrollView,
      errorsStorage: errorsStorage,
      layoutDirection: layoutDirection,
      variableTracker: variableTracker,
      persistentValuesStorage: persistentValuesStorage,
      tooltipViewFactory: tooltipViewFactory
    )
  }

  init(
    viewId: DivViewId,
    cardLogId: String?,
    parentPath: UIElementPath?,
    parentDivStatePath: DivStatePath?,
    stateManager: DivStateManager,
    blockStateStorage: DivBlockStateStorage,
    visibilityCounter: DivVisibilityCounting?,
    lastVisibleBoundsCache: DivLastVisibleBoundsCache?,
    imageHolderFactory: DivImageHolderFactory,
    highPriorityImageHolderFactory: DivImageHolderFactory?,
    divCustomBlockFactory: DivCustomBlockFactory?,
    fontProvider: DivFontProvider?,
    flagsInfo: DivFlagsInfo,
    extensionHandlers: [String: DivExtensionHandler],
    variablesStorage: DivVariablesStorage,
    playerFactory: PlayerFactory?,
    debugParams: DebugParams,
    scheduler: Scheduling?,
    parentScrollView: ScrollView?,
    errorsStorage: DivErrorsStorage?,
    layoutDirection: UserInterfaceLayoutDirection,
    variableTracker: DivVariableTracker?,
    persistentValuesStorage: DivPersistentValuesStorage?,
    tooltipViewFactory: DivTooltipViewFactory?
  ) {
    self.viewId = viewId
    self.cardLogId = cardLogId
    let cardId = viewId.cardId
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
    self.parentScrollView = parentScrollView
    let errorsStorage = errorsStorage ?? DivErrorsStorage(errors: [])
    self.errorsStorage = errorsStorage
    self.layoutDirection = layoutDirection
    self.variableTracker = variableTracker
    let persistentValuesStorage = persistentValuesStorage ?? DivPersistentValuesStorage()
    self.persistentValuesStorage = persistentValuesStorage
    self.tooltipViewFactory = tooltipViewFactory
    self.variablesStorage = variablesStorage
    self.extensionHandlers = extensionHandlers
    self.functionsProvider = FunctionsProvider(
      persistentValuesStorage: persistentValuesStorage
    )
    expressionResolver = makeExpressionResolver(
      functionsProvider: functionsProvider,
      viewId: viewId,
      path: parentPath,
      variablesStorage: variablesStorage,
      localValues: nil,
      variableTracker: variableTracker,
      errorsStorage: errorsStorage
    )
  }

  public var cardId: DivCardID {
    viewId.cardId
  }

  public func getExtensionHandlers(for div: DivBase) -> [DivExtensionHandler] {
    guard let extensions = div.extensions else {
      return []
    }
    return extensions.compactMap {
      let id = $0.id
      if !extensionHandlers.keys.contains(id) {
        addError(message: "No DivExtensionHandler for: \(id)")
      }
      return extensionHandlers[id]
    }
  }

  public func addError(message: String) {
    errorsStorage.add(DivBlockModelingError(message, path: parentPath))
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

  func makeBinding<T>(variableName: String?, defaultValue: T) -> Binding<T> {
    guard let variableName else {
      return Binding(name: "", value: Property(initialValue: defaultValue))
    }

    let divVariableName = DivVariableName(rawValue: variableName)
    variableTracker?.onVariableUsed(id: viewId, variable: divVariableName)
    let value: T = variablesStorage
      .getVariableValue(path: parentPath, name: divVariableName) ?? defaultValue
    let valueProp = Property<T>(
      getter: { value },
      setter: {
        guard let newValue = DivVariableValue($0) else { return }
        self.variablesStorage.update(
          path: parentPath,
          name: divVariableName,
          value: newValue
        )
      }
    )
    return Binding(name: variableName, value: valueProp)
  }

  func modifying(
    elementId: String? = nil,
    cardLogId: String? = nil,
    parentPath: UIElementPath? = nil,
    parentDivStatePath: DivStatePath? = nil,
    errorsStorage: DivErrorsStorage? = nil,
    sizeModifier: DivSizeModifier? = nil,
    prototypeParams: PrototypeParams? = nil
  ) -> Self {
    var context = self
    context.elementId = elementId
    if let cardLogId {
      context.cardLogId = cardLogId
    }
    if let parentDivStatePath {
      context.parentDivStatePath = parentDivStatePath
    }
    if let sizeModifier {
      context.sizeModifier = sizeModifier
    }

    if parentPath == nil, errorsStorage == nil, prototypeParams == nil {
      return context
    }

    if let prototypeParams {
      var localValues = self.localValues
      localValues[prototypeParams.variableName] = prototypeParams.value
      localValues["index"] = prototypeParams.index
      context.localValues = localValues
    }

    context.parentPath = parentPath ?? self.parentPath
    context.errorsStorage = errorsStorage ?? self.errorsStorage
    context.expressionResolver = makeExpressionResolver(
      functionsProvider: functionsProvider,
      viewId: viewId,
      path: context.parentPath,
      variablesStorage: variablesStorage,
      localValues: context.localValues,
      variableTracker: variableTracker,
      errorsStorage: context.errorsStorage
    )

    return context
  }
}

struct PrototypeParams {
  let index: Int
  let variableName: String
  let value: DivDictionary
}

private func makeExpressionResolver(
  functionsProvider: FunctionsProvider,
  viewId: DivViewId,
  path: UIElementPath,
  variablesStorage: DivVariablesStorage,
  localValues: [String: AnyHashable]?,
  variableTracker: DivVariableTracker?,
  errorsStorage: DivErrorsStorage
) -> ExpressionResolver {
  ExpressionResolver(
    functionsProvider: functionsProvider,
    variableValueProvider: {
      if let value = localValues?[$0] {
        return value
      }
      let variableName = DivVariableName(rawValue: $0)
      variableTracker?.onVariableUsed(id: viewId, variable: variableName)
      return variablesStorage.getVariableValue(path: path, name: variableName)
    },
    errorTracker: { error in
      errorsStorage.add(DivExpressionError(error, path: path))
    }
  )
}
